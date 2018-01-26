package com.github.rhoar_ci.dashboard.jenkins;

import com.github.rhoar_ci.dashboard.ConsoleTextResource;
import com.github.rhoar_ci.dashboard.StartBuildResource;
import com.github.rhoar_ci.dashboard.ci.BuildStatus;
import com.github.rhoar_ci.dashboard.ci.TestCluster;
import com.github.rhoar_ci.dashboard.ci.TestDescription;
import com.github.rhoar_ci.dashboard.ci.TestResult;
import com.github.rhoar_ci.dashboard.ci.TestType;
import com.github.rhoar_ci.dashboard.openshift.TokenAuthorizingHttpClient;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.ContentResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.CloseableHttpClient;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestScoped
public class JenkinsClient {
    @Inject
    @TokenAuthorizingHttpClient
    private CloseableHttpClient httpClient;

    @Inject
    @ConfigurationValue("dashboard.jenkins.url")
    private String jenkinsBaseUrl;

    @PostConstruct
    public void init() {
        if (jenkinsBaseUrl == null) {
            // default inside OpenShift
            jenkinsBaseUrl = "http://jenkins/";
        }
    }

    public List<Job> getRelevantJobs() throws IOException {
        String url = jenkinsUrl("/api/json?tree=jobs[name,description,lastCompletedBuild[number,timestamp,result],lastBuild[building]]");
        String jsonString = Executor.newInstance(httpClient).execute(Request.Get(url)).returnContent().asString();

        Gson gson = new Gson();
        JsonJobs jobs = gson.fromJson(jsonString, JsonJobs.class);

        return jobs.jobs.stream()
                .map(it -> {
                    Optional<JsonJobDataInDescription> optionalData = readJobDescription(it, gson);
                    if (!optionalData.isPresent()) {
                        return Optional.<Job>empty();
                    }
                    JsonJobDataInDescription data = optionalData.get();

                    TestResult lastResult;
                    boolean buildingNow = it.lastBuild != null && it.lastBuild.building;
                    String runBuildLink = StartBuildResource.link(it.name);
                    if (it.lastCompletedBuild != null) {
                        String link = ConsoleTextResource.link(it.name, it.lastCompletedBuild.number);
                        LocalDateTime timestamp = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(it.lastCompletedBuild.timestamp), ZoneId.systemDefault());
                        lastResult = new TestResult(BuildStatus.from(it.lastCompletedBuild.result),
                                it.lastCompletedBuild.number, link, timestamp, buildingNow, runBuildLink);
                    } else {
                        lastResult = new TestResult(BuildStatus.UNKNOWN, -1, null, null, buildingNow, runBuildLink);
                    }

                    return Optional.of(new Job(new TestCluster(data.cluster), new TestDescription(data.description),
                            new TestType(data.type), lastResult));
                })
                .flatMap(it -> it.isPresent() ? Stream.of(it.get()) : Stream.empty())
                .collect(Collectors.toList());
    }

    private static Optional<JsonJobDataInDescription> readJobDescription(JsonJobs.JsonJob job, Gson gson) {
        if (job.description == null || job.description.isEmpty()) {
            return Optional.empty();
        }

        try {
            String description = job.description.replaceAll("<!-- .*? -->", "");
            return Optional.of(gson.fromJson(description, JsonJobDataInDescription.class));
        } catch (JsonParseException e) {
            return Optional.empty();
        }
    }

    public ConsoleText streamConsoleText(String buildName, String buildNumber) {
        String url = jenkinsUrl("/job/" + buildName + "/" + buildNumber + "/consoleText");
        return new ConsoleText(httpClient, url);
    }

    public StartedBuild startBuild(String buildName) throws IOException {
        String url = jenkinsUrl("/job/" + buildName + "/build?delay=0sec");
        HttpResponse response = Executor.newInstance(httpClient).execute(Request.Post(url)).returnResponse();

        if (response.getStatusLine().getStatusCode() == 201) {
            return new StartedBuild(true, null);
        }
        Content responseContent = new ContentResponseHandler().handleEntity(response.getEntity());
        return new StartedBuild(false, responseContent.asString());
    }

    private String jenkinsUrl(String part) {
        String base = jenkinsBaseUrl;
        if (!base.endsWith("/")) {
            base += "/";
        }
        if (part.startsWith("/")) {
            part = part.substring(1);
        }
        return base + part;
    }
}

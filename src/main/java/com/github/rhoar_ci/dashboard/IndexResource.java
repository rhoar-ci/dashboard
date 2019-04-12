package com.github.rhoar_ci.dashboard;

import com.github.rhoar_ci.dashboard.ci.TestCluster;
import com.github.rhoar_ci.dashboard.ci.TestDescription;
import com.github.rhoar_ci.dashboard.ci.TestResult;
import com.github.rhoar_ci.dashboard.ci.TestType;
import com.github.rhoar_ci.dashboard.jenkins.JenkinsClient;
import com.github.rhoar_ci.dashboard.jenkins.Job;
import com.github.rhoar_ci.dashboard.storage.JobService;
import com.github.rhoar_ci.dashboard.thymeleaf.View;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

@Path("/")
public class IndexResource {
    @Inject
    private JenkinsClient jenkins;

    @Inject
    private JobService jobService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public View get(@QueryParam("flash") String flash) throws Exception {
        List<Job> jobs = new ArrayList<>();
        jobs.addAll(jobService.getAll());
        jobs.addAll(jenkins.getRelevantJobs());

        SortedSet<TestType> header = newSortedSet(TestType::toString);
        Map<TestCluster, Map<TestDescription, Map<TestType, TestResult>>> tables = newSortedMap(TestCluster::toString);

        for (Job job : jobs) {
            header.add(job.getType());

            Map<TestDescription, Map<TestType, TestResult>> table =
                    tables.computeIfAbsent(job.getCluster(), ignored -> newSortedMap(TestDescription::toString));

            table.computeIfAbsent(job.getDescription(), ignored -> newSortedMap(TestType::toString))
                    .put(job.getType(), job.getLastResult());
        }

        return new View("index.html",
                "tables", tables,
                "header", header,
                "now", LocalDateTime.now(),
                "flash", flash
        );
    }

    private static <E, C extends Comparable<C>> SortedSet<E> newSortedSet(Function<E, C> compareBy) {
        return new TreeSet<>(Comparator.comparing(compareBy));
    }

    private static <K, V, C extends Comparable<C>> SortedMap<K, V> newSortedMap(Function<K, C> compareBy) {
        return new TreeMap<>(Comparator.comparing(compareBy));
    }
}

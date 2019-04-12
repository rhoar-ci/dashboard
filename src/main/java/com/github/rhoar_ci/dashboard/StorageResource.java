package com.github.rhoar_ci.dashboard;

import com.github.rhoar_ci.dashboard.ci.BuildStatus;
import com.github.rhoar_ci.dashboard.ci.TestCluster;
import com.github.rhoar_ci.dashboard.ci.TestDescription;
import com.github.rhoar_ci.dashboard.ci.TestResult;
import com.github.rhoar_ci.dashboard.ci.TestType;
import com.github.rhoar_ci.dashboard.jenkins.Job;
import com.github.rhoar_ci.dashboard.jenkins.JsonJobDataInDescription;
import com.github.rhoar_ci.dashboard.storage.JobService;
import com.github.rhoar_ci.dashboard.storage.UploadToken;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Optional;

@Path("/store")
public class StorageResource {
    @Inject
    private JobService jobService;

    @Inject
    private UploadToken uploadToken;

    /**
     * @param description Contains JSON with fields "cluster", "description" and "type"
     * @param result Result of the last build
     * @param buildNumber Number of the injected build
     * @return
     */
    @POST
    public Response store(@FormParam("description") String description,
                          @FormParam("result") String result,
                          @FormParam("buildNumber") String buildNumber,
                          @FormParam("token") String token){

        if (!uploadToken.get().equals(token)){
            return Response.status(403).build();
        }

        Optional<JsonJobDataInDescription> data = JsonJobDataInDescription.parse(description);
        if (!data.isPresent()){
            return Response.status(400).build();
        }

        Job job = new Job(
                new TestCluster(data.get().cluster),
                new TestDescription(data.get().description),
                new TestType(data.get().type),
                new TestResult(
                        BuildStatus.from(result),
                        Integer.parseInt(buildNumber),
                        null,
                        LocalDateTime.now(),
                        false,
                        null
                )
        );
        jobService.updateJob(job);

        return Response.ok().build();
    }
}

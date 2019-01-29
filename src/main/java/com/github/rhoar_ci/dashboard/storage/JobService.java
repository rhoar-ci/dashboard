package com.github.rhoar_ci.dashboard.storage;

import com.github.rhoar_ci.dashboard.ci.TestCluster;
import com.github.rhoar_ci.dashboard.ci.TestDescription;
import com.github.rhoar_ci.dashboard.ci.TestType;
import com.github.rhoar_ci.dashboard.jenkins.Job;
import org.apache.commons.lang3.tuple.Triple;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class JobService {
    private Map<Triple<TestCluster, TestDescription, TestType>, Job> jobStorage= new HashMap<>();

    public void updateJob(Job jenkinsJob){
        Triple<TestCluster, TestDescription, TestType> triple = jobToTriple(jenkinsJob);
        jobStorage.put(triple, jenkinsJob);
    }

    public Collection<Job> getAll(){
        return jobStorage.values();
    }

    private Triple<TestCluster, TestDescription, TestType> jobToTriple(Job jenkinsJob){
        return Triple.of(jenkinsJob.getCluster(), jenkinsJob.getDescription(), jenkinsJob.getType());
    }
}

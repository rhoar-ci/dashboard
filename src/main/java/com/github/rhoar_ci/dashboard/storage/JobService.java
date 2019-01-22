package com.github.rhoar_ci.dashboard.storage;

import com.github.rhoar_ci.dashboard.ci.TestCluster;
import com.github.rhoar_ci.dashboard.ci.TestDescription;
import com.github.rhoar_ci.dashboard.jenkins.Job;
import org.apache.commons.lang3.tuple.Pair;

import javax.enterprise.context.RequestScoped;
import java.util.*;

@RequestScoped
public class JobService {
    private static Map<Pair<TestCluster, TestDescription>, Job> jobStorage= new HashMap<>();

    public void updateJob(Job jenkinsJob){
        Pair<TestCluster, TestDescription> pair= jobToPair(jenkinsJob);
        jobStorage.put(pair, jenkinsJob);
    }

    public Collection<Job> getAll(){
        return jobStorage.values();
    }

    private Pair<TestCluster, TestDescription> jobToPair(Job jenkinsJob){
        return Pair.of(jenkinsJob.getCluster(), jenkinsJob.getDescription());
    }
}

package com.github.rhoar_ci.dashboard.jenkins;

import java.util.List;

class JsonJobs {
    List<JsonJob> jobs;

    static class JsonJob {
        String name;
        String description;
        boolean inQueue;
        JsonLastCompletedBuild lastCompletedBuild;
        JsonLastBuild lastBuild;
    }

    static class JsonLastCompletedBuild {
        int number;
        long timestamp;
        String result;
    }

    static class JsonLastBuild {
        boolean building;
    }
}

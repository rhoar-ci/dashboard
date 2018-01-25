package com.github.rhoar_ci.dashboard.jenkins;

public final class StartedBuild {
    public final boolean success;
    public final String errorMessage;

    StartedBuild(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
}

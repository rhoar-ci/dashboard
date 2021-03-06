package com.github.rhoar_ci.dashboard.ci;

import java.time.LocalDateTime;

/**
 * Inner cell of the CI status table.
 */
public final class TestResult {
    private final BuildStatus status;
    private final int buildNumber;
    private final String link;
    private final LocalDateTime timestamp;

    private final boolean anotherBuildRunningNow;
    private final String runBuildLink;

    public TestResult(BuildStatus status, int buildNumber, String link, LocalDateTime timestamp,
                      boolean anotherBuildRunningNow, String runBuildLink) {
        this.status = status;
        this.buildNumber = buildNumber;
        this.link = link;
        this.timestamp = timestamp;
        this.anotherBuildRunningNow = anotherBuildRunningNow;
        this.runBuildLink = runBuildLink;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isAnotherBuildRunningNow() {
        return anotherBuildRunningNow;
    }

    public String getRunBuildLink() {
        return runBuildLink;
    }
}

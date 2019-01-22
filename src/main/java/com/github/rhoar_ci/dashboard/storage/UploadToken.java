package com.github.rhoar_ci.dashboard.storage;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UploadToken {
    @Inject
    @ConfigurationValue("dashboard.upload.token")
    private String value;

    @PostConstruct
    public void init() {
        if (value == null) {
            value = System.getenv("UPLOAD_TOKEN");
        }
    }

    public String get() {
        return value;
    }
}

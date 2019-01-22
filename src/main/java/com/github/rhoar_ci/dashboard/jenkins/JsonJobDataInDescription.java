package com.github.rhoar_ci.dashboard.jenkins;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.Optional;

public class JsonJobDataInDescription {
    public String cluster;
    public String description;
    public String type;

    public static Optional<JsonJobDataInDescription> parse(String inputDescription){
        try {
            String description = inputDescription.replaceAll("<!-- .*? -->", "");
            return Optional.of((new Gson()).fromJson(description, JsonJobDataInDescription.class));
        } catch (JsonParseException e) {
            return Optional.empty();
        }
    }
}

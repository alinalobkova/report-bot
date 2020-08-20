package com.github.alinalobkova.reportbot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class ReportConfig {
    @JsonProperty("users")
    List<User> users;
    @JsonProperty("reports")
    List<Report> reports;

    public ReportConfig() {
        users = Collections.emptyList();
        reports = Collections.emptyList();
    }
}

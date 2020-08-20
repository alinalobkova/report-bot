package com.github.alinalobkova.reportbot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.alinalobkova.reportbot.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sql")
    private String sql;
    @JsonProperty("title")
    private String title = null;
    @JsonProperty("source")
    private String source;
    @JsonProperty("cron")
    private String cron;
    @JsonProperty("type")
    private ReportType type = ReportType.EXCEL_DETAIL;
    @JsonProperty("users")
    private List<Long> users = new ArrayList<>();
}

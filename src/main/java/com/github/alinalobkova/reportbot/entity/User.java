package com.github.alinalobkova.reportbot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("idTelegram")
    private Long idTelegram;
    @JsonProperty("name")
    private String name;
}

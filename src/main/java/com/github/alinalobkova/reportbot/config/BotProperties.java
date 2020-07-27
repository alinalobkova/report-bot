package com.github.alinalobkova.reportbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Параметры конфигурации бота
 *
 * @author MikeSafonov
 */
@Component
@ConfigurationProperties(value = "reportbot")
@Data
public class BotProperties {

    private String token;
    private String username;
    private String proxyHost;
    private int proxyPort;
    private boolean proxyEnable;

}

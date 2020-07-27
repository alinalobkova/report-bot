package com.github.alinalobkova.reportbot.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
@AllArgsConstructor
public class TelegramConfig {

    /**
     * Объект с данными для работы с ботом
     */
    private BotProperties properties;


    @Bean
    public TelegramBot telegramBot() {
        String botToken = properties.getToken();
        OkHttpClient client = buildHttpClient();
        return new TelegramBot.Builder(botToken).okHttpClient(client).build();
    }

    /**
     * Построение клиента http с подключением через прокси
     *
     * @return ссылка на объект клиента http
     */
    private OkHttpClient buildHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (properties.isProxyEnable()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(properties.getProxyHost(), properties.getProxyPort()));
            builder.proxy(proxy);
        }
        return builder.build();
    }
}

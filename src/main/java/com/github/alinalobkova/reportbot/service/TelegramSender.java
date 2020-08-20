package com.github.alinalobkova.reportbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class TelegramSender {

    private final TelegramBot reportBot;

    public void sendToTelegram(Long id, String text) {
        reportBot.execute(new SendMessage(id, text));
    }

    public void sendToTelegram(Long id, File file) {
        reportBot.execute(new SendDocument(id, file));
    }
}

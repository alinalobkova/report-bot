package com.github.alinalobkova.reportbot.service;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import com.github.alinalobkova.reportbot.entity.Report;
import com.github.alinalobkova.reportbot.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Setter
    @Getter
    private List<User> users;


    /**
     * Получение списка получателей отчетов
     *
     * @param report - отчет
     * @return
     */
    public List<Long> getTelegramUsersForReport(Report report) {
        return getUsers()
                .stream()
                .filter(user -> report.getUsers().contains(user.getId()))
                .map(User::getIdTelegram)
                .collect(Collectors.toList());
    }

}

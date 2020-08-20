package com.github.alinalobkova.reportbot.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import com.github.alinalobkova.reportbot.entity.Report;
import com.github.alinalobkova.reportbot.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportCreatorFactory reportCreatorFactory;

    @Getter
    @Setter
    private List<Report> reports;

    private final UserService userService;


    /**
     * Создание и отправка отчета пользвоателю
     *
     * @param idUser   - ид пользователя
     * @param idReport - ид отчета
     */
    public void createAndSendReports(Long idUser, Long idReport) {

        List<Report> reports = getReports().stream().filter(r -> r.getId().equals(idReport)).collect(Collectors.toList());
        List<Long> listIdsTelegram = userService.getUsers().stream().filter(u -> u.getId().equals(idUser)).map(User::getIdTelegram)
                .collect(Collectors.toList());

        reports.forEach(r -> runCreateAndSendReport(r, listIdsTelegram));
    }



    private void runCreateAndSendReport(Report report, List<Long> listIdsTelegram) {
        ReportCreator reportCreator = reportCreatorFactory.getReportCreator(report, listIdsTelegram);
        new Thread(reportCreator).start();
    }
}

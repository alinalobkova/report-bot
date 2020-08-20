package com.github.alinalobkova.reportbot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.rmi.UnexpectedException;
import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Month {


    JANUARY((short) 1, "Январь"),
    FEBRUARY((short) 2, "Февраль"),
    MARCH((short) 3, "Март"),
    APRIL((short) 4, "Апрель"),
    MAY((short) 5, "Май"),
    JUNE((short) 6, "Июнь"),
    JULY((short) 7, "Июль"),
    AUGUST((short) 8, "Август"),
    SEPTEMBER((short) 9, "Сентябрь"),
    OCTOBER((short) 10, "Октябрь"),
    NOVEMBER((short) 11, "Ноябрь"),
    DECEMBER((short) 12, "Декабрь");


    private final short id;
    private final String russianName;


    public static Month valueOf(int id) throws UnexpectedException {
        return Arrays.stream(values()).filter(status -> status.id == id).findFirst()
                .orElseThrow(() -> new UnexpectedException("Unknown moth id: " + id));
    }


}

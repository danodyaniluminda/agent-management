package com.biapay.agentmanagement.web.dto.common;

import lombok.Data;

@Data
public class BillDate {

    private int year;
    private String month;
    private int monthValue;
    private int dayOfMonth;
    private boolean leapYear;
    private Chronology chronology;
    private String dayOfWeek;
    private int dayOfYear;
    private String era;
}

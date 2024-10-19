package com.biapay.agentmanagement.web.dto.common;

import lombok.Data;

@Data
public class ServiceOption {

    private long serviceid;
    private String merchant;
    private String payItemId;
    private String payItemDescr;
    private String amountType;
    private String localCur;
    private String name;
    private long amountLocalCur;
    private String description;
    private String optStrg;
    private String optNmb;
}

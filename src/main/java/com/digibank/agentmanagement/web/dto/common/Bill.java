package com.digibank.agentmanagement.web.dto.common;

import lombok.Data;

@Data
public class Bill {

    private String billType;
    private long penaltyAmount;
    private long payOrder;
    private String payItemId;
    private String payItemDescr;
    private String serviceNumber;
    private long serviceid;
    private String merchant;
    private String amountType;
    private String localCur;
    private long amountLocalCur;
    private String billNumber;
    private String customerNumber;
    private String billMonth;
    private String billYear;
    private BillDate billDate;
    private BillDate billDueDate;
    private String optStrg;
    private String optNmb;
}

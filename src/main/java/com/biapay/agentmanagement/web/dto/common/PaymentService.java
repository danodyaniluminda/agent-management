package com.biapay.agentmanagement.web.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class PaymentService {

    private int serviceid;
    private String merchant;
    private String title;
    private String description;
    private String category;
    private String country;
    private String localCur;
    private String type;
    private String status;
    private List<PaymentServiceLabel> labelCustomerNumber;
    private List<PaymentServiceLabel> labelServiceNumber;
    private boolean isVerifiable;
    private String validationMask;
    private List<PaymentServiceLabel> hint;
}
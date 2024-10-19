package com.biapay.agentmanagement.web.dto.common;

import lombok.Data;

@Data
public class PaymentMerchant {

    private String merchant;
    private String name;
    private String description;
    private String category;
    private String country;
    private String status;
    private String logo;
    private String logoHash;
}
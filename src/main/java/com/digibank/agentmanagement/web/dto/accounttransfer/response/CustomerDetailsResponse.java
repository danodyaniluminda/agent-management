package com.digibank.agentmanagement.web.dto.accounttransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerDetailsResponse {

        private String customerId;
        private String name;
        private String phoneNumber;
        private String address;
        private String idCard;
        private String expIdDate;
        private String type;
        private String dna;
        private String sex;
        private String lang;
        private String manager;
        private String email;
        private String firstName;
        private String lastName;
}

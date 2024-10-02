package com.digibank.agentmanagement.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AgentManagementPropertyHolder {

    @Value("${services.wallet.url}")
    private String walletAdaptorServiceURL;

    @Value("${services.usermanagement.url}")
    private String userManagementURL;

    @Value("${commission.percentage.rounding.scale}")
    private int commissionPercentageRoundingScale;

    @Value("${key.separator.character}")
    private String keySeparatorCharacter;

    @Value("${services.bankAdaptor.url}")
    private String bankAdaptorServiceURL;

    @Value("${services.backOffice.url}")
    private String backOfficeAdaptorServiceURL;

    @Value("${fileStore.basePath}")
    private String fileStoreBasePath;
}

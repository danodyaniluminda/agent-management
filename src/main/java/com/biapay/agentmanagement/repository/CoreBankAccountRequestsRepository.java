package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.CoreBankAccountOpeningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoreBankAccountRequestsRepository extends JpaRepository<CoreBankAccountOpeningRequest, Long> {
}

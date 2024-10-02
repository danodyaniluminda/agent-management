package com.digibank.agentmanagement.repository;

import com.digibank.agentmanagement.domain.CoreBankAccountOpeningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoreBankAccountRequestsRepository extends JpaRepository<CoreBankAccountOpeningRequest, Long> {
}

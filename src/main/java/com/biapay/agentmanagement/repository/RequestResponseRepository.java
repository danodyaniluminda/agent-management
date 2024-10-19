package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.RequestResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestResponseRepository extends JpaRepository<RequestResponseLog, Long> {}

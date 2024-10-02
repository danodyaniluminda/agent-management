package com.digibank.agentmanagement.repository;

import com.digibank.agentmanagement.domain.RequestResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestResponseRepository extends JpaRepository<RequestResponseLog, Long> {}

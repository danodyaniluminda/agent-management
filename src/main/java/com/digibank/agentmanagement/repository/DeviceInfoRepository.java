package com.digibank.agentmanagement.repository;

import com.digibank.agentmanagement.domain.DeviceInfo;
import com.digibank.agentmanagement.domain.DocumentIdInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {
}

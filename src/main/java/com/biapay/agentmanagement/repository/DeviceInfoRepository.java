package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {
}

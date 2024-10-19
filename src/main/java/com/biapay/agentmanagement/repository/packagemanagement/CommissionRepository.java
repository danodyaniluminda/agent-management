package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Commission;
import com.biapay.agentmanagement.domain.packagemanagement.CommissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {

    Optional<Commission> findByCommissionType(CommissionType commissionType);

    Optional<Commission> findByCommissionTypeAndCommissionIdNot(CommissionType commissionType, Long commissionId);
}

package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AgentPackage;
import com.biapay.agentmanagement.domain.packagemanagement.Currency;
import com.biapay.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageCurrencyLimitRepository extends JpaRepository<PackageCurrencyLimit, Long> {

    void deleteByAgentPackage(AgentPackage agentPackage);

    List<PackageCurrencyLimit> findAllByAgentPackage(AgentPackage agentPackage);

    Optional<PackageCurrencyLimit> findByCurrencyAndAgentPackage(Currency currency, AgentPackage agentPackage);
}

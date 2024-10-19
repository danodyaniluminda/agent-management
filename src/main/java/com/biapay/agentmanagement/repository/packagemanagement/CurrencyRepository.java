package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByName(String name);

    Optional<Currency> findByCode(String Code);

    Optional<Currency> findByNameAndCurrencyIdNot(String name, Long currencyId);
}

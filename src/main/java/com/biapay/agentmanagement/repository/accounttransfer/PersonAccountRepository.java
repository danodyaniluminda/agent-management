package com.biapay.agentmanagement.repository.accounttransfer;

import com.biapay.agentmanagement.domain.accounttransfer.PersonAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonAccountRepository extends JpaRepository<PersonAccount, Long> {
}

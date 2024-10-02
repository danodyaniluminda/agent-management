package com.digibank.agentmanagement.repository.accounttransfer;

import com.digibank.agentmanagement.domain.accounttransfer.PersonAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonAccountRepository extends JpaRepository<PersonAccount, Long> {
}

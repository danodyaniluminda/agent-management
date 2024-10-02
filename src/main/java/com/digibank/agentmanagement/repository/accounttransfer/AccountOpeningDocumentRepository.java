package com.digibank.agentmanagement.repository.accounttransfer;

import com.digibank.agentmanagement.domain.accounttransfer.AccountOpeningDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOpeningDocumentRepository extends JpaRepository<AccountOpeningDocument, Long> {
}

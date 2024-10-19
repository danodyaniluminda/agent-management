package com.biapay.agentmanagement.repository.accounttransfer;

import com.biapay.agentmanagement.domain.accounttransfer.AccountOpeningDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOpeningDocumentRepository extends JpaRepository<AccountOpeningDocument, Long> {
}

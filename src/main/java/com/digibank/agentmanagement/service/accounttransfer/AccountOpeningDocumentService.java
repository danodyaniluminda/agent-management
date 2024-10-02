package com.digibank.agentmanagement.service.accounttransfer;

import com.digibank.agentmanagement.domain.accounttransfer.AccountOpeningDocument;

import java.util.List;

public interface AccountOpeningDocumentService {

    void add(AccountOpeningDocument accountOpeningDocument);

    void addAll(List<AccountOpeningDocument> accountOpeningDocuments);
}

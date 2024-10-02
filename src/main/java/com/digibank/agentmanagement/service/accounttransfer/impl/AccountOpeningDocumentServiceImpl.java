package com.digibank.agentmanagement.service.accounttransfer.impl;

import com.digibank.agentmanagement.domain.accounttransfer.AccountOpeningDocument;
import com.digibank.agentmanagement.repository.accounttransfer.AccountOpeningDocumentRepository;
import com.digibank.agentmanagement.service.accounttransfer.AccountOpeningDocumentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountOpeningDocumentServiceImpl implements AccountOpeningDocumentService {

    private final AccountOpeningDocumentRepository accountOpeningDocumentRepository;

    @Override
    @Transactional
    public void add(AccountOpeningDocument accountOpeningDocument) {
        accountOpeningDocumentRepository.save(accountOpeningDocument);
    }

    @Override
    @Transactional
    public void addAll(List<AccountOpeningDocument> accountOpeningDocuments) {
        for (AccountOpeningDocument accountOpeningDocument: accountOpeningDocuments) {
            accountOpeningDocumentRepository.save(accountOpeningDocument);
        }
    }
}

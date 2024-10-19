package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.AgentDetails;
import com.biapay.agentmanagement.domain.DocumentIdInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DocumentRepository extends JpaRepository<DocumentIdInfo, Long> {

    Optional<DocumentIdInfo> findByDocumentFileNameAndAgentDetails(String fileName , AgentDetails agentDetails);

    Optional<DocumentIdInfo> findByDocumentFileName(String fileName);
}

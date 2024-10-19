package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Asset;
import com.biapay.agentmanagement.domain.packagemanagement.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    Optional<Operation> findByName(String name);

    Optional<Operation> findByNameAndAssetAndOperationIdNot(String name, Asset asset, Long operationId);

    Optional<Operation> findByNameAndAsset(String name , Asset asset);
	
	List<Operation> findByAsset(Asset asset);

    List<Operation> findAllByName(String operationName);

    List<Operation> findAllByAssetName(String assetName);
}

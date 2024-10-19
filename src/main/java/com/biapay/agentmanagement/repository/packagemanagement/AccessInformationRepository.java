package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AccessInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessInformationRepository extends JpaRepository<AccessInformation, Long> {

    List<AccessInformation> findAllByUserName(String userName);
}

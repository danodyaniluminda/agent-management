package com.digibank.agentmanagement.repository.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentUserRepository extends JpaRepository<AgentUser, Long> {

    Optional<AgentUser> findByUserName(String userName);

    Optional<AgentUser> findByEmail(String email);

    Optional<AgentUser> findByMobileNumber(String mobileNumber);



    Optional<AgentUser> findByUserNameAndAgentUserIdNot(String userName, Long agentUserId);

    Optional<AgentUser> findByEmailAndAgentUserIdNot(String email, Long agentUserId);

    Optional<AgentUser> findByMobileNumberAndAgentUserIdNot(String mobileNumber, Long agentUserId);


}

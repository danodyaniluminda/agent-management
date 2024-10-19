/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByTransactionDate(LocalDate todayDate , Pageable pageable);
//    @Query(value = "SELECT SUM(total_days) FROM MyEntity", nativeQuery = true)
//    BigDecimal sumAgentTransactionByDate(String fromDate, String toDate, Long agentId, String transactionType);

    Page<Transaction> findAll(Pageable pageable);

}


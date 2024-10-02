/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digibank.agentmanagement.repository.packagemanagement;

import com.digibank.agentmanagement.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByTransactionDate(LocalDate todayDate , Pageable pageable);
//    @Query(value = "SELECT SUM(total_days) FROM MyEntity", nativeQuery = true)
//    BigDecimal sumAgentTransactionByDate(String fromDate, String toDate, Long agentId, String transactionType);

    Page<Transaction> findAll(Pageable pageable);

}


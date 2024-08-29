package com.picpay.Transactions.repository;

import com.picpay.Transactions.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}

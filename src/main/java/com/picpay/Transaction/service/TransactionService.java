package com.picpay.Transaction.service;

import com.picpay.Shared.service.AuthorizationService;
import com.picpay.User.service.UserService;
import com.picpay.Transaction.domain.Transaction;
import com.picpay.User.domain.User;
import com.picpay.Transaction.dtos.TransactionDTO;
import com.picpay.Transaction.repository.TransactionRepository;
import com.picpay.Shared.dtos.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AuthorizationService authService;

    @Autowired
    NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User reciver = this.userService.findUserById(transactionDTO.reciverId());

        userService.validateTransaction(sender, transactionDTO.value());

        boolean isAuthorized = this.authService.authorizeTransaction(sender, transactionDTO.value());
        if (!isAuthorized) {
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transactionDTO.value());
        newTransaction.setSender(reciver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        reciver.setBalance(reciver.getBalance().add(transactionDTO.value()));

        this.repository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(reciver);

        this.notificationService.sendNotification(sender, "Transação realizada com sucesso.");
        this.notificationService.sendNotification(reciver, "Transação recebida com sucesso.");

        return newTransaction;
    }



}

package com.picpay.services;

import com.picpay.domain.transaction.Transaction;
import com.picpay.domain.user.User;
import com.picpay.dtos.TransactionDTO;
import com.picpay.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private AuthorizationService authService;

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User reciver = this.userService.findUserById(transactionDTO.receiverId());

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

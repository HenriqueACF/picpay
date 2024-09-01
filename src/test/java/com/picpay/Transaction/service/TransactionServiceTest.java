package com.picpay.Transaction.service;

import com.picpay.Shared.service.AuthorizationService;
import com.picpay.Shared.service.NotificationService;
import com.picpay.Transaction.dtos.TransactionDTO;
import com.picpay.Transaction.repository.TransactionRepository;
import com.picpay.User.domain.User;
import com.picpay.User.domain.UserType;
import com.picpay.User.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AuthorizationService authService;

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Retorna sucesso quando está autorizado a realizar uma trasanção ")
    void createTransactionSuccess() throws Exception {
        //CRIA USUARIOS
        User sender = new User(1L,
                "Henrique",
                "Assis",
                "12345678910",
                "henrique@test.com",
                "Senha123#",
                new BigDecimal(10),
                UserType.COMMON);

        User receiver = new User(2L,
                "Pedro",
                "Ferreira",
                "12345678911",
                "pedro@test.com",
                "Senha123#",
                new BigDecimal(10),
                UserType.COMMON);

        // CRIA MOCK DO RETORNO
        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        // CRIA MOCK PARA O RETORNO DO AUTHSERVICE SEJA FALSO
        when(authService.authorizeTransaction(any(), any())).thenReturn(true);

        // CRIA UM DTO COM O VALOR DA TRANSAÇÃO, PASSANDO O ID DE SENDER E RECEIVER
        TransactionDTO req = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(req);

        verify(repository, times(1)).save(any());

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso.");
        verify(notificationService, times(1)).sendNotification(receiver, "Transação recebida com sucesso.");
    }

    @Test
    @DisplayName("Retorna erro quando nao esta autorizado a realizar uma transação")
    void createTransactionError() throws Exception {

        User sender = new User(1L,
                "Henrique",
                "Assis",
                "12345678910",
                "henrique@test.com",
                "Senha123#",
                new BigDecimal(10),
                UserType.COMMON);

        User receiver = new User(2L,
                "Pedro",
                "Ferreira",
                "12345678911",
                "pedro@test.com",
                "Senha123#",
                new BigDecimal(10),
                UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(false);

        // PEGA EXCEPTION ANTES QUE PARE A APLICAÇÃO
        Exception thrown = Assertions.assertThrows(Exception.class, ()->{
            TransactionDTO req = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(req);
        });

        // VERIFICA SE A MENSAGEM DA EXCEPTION ERA A ESPERADA
        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }
}

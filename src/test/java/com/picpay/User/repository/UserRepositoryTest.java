package com.picpay.User.repository;

import com.picpay.User.domain.User;
import com.picpay.User.domain.UserType;
import com.picpay.User.dtos.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should get User return successfully from database")
    void findUserByDocumentSuccess() {
        String document = "12345678910";
        UserDTO data = new UserDTO(
                "Henrique",
                "Assis",
                document,
                new BigDecimal(10),
                "henrique@test.com",
                "Senha123#",
                UserType.COMMON);

        this.createUser(data);

        Optional<User> result =  this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent());
    }

    private User createUser(UserDTO data){
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}

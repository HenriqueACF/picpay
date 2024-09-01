package com.picpay.User.dtos;

import com.picpay.User.domain.UserType;

import java.math.BigDecimal;

public record UserDTO(String firstName, String lastName, String document, BigDecimal balance, String email,
                      String password, UserType userType) {}

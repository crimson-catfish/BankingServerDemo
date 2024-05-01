package ru.ivanov_savelii.bankingserverdemo.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class User implements Serializable {

    private Long id;
    private String login;
    private String passhash;
    private BigDecimal balance;

    public User() {}

    public User(String login, String passhash, BigDecimal balance) {
        this.login = login;
        this.passhash = passhash;
        this.balance = balance;
    }
}

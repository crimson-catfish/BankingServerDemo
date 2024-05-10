package ru.ivanov_savelii.bankingserverdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ivanov_savelii.bankingserverdemo.repository.UserRepository;

import java.math.BigDecimal;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/money")
public class MoneyController {

    @Autowired
    UserRepository repository;

    @GetMapping()
    public ResponseEntity<Object> getBalance(Authentication auth) {
        BigDecimal balance;

        try {
            balance = repository.findByLogin(auth.getName()).getBalance();
            if (balance == null) throw new Exception();
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot get user balance from database.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(balance, HttpStatus.OK);
    }
}
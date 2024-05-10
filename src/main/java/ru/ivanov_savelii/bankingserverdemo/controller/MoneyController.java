package ru.ivanov_savelii.bankingserverdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ivanov_savelii.bankingserverdemo.dto.request.SendMoneyRequest;
import ru.ivanov_savelii.bankingserverdemo.entity.User;
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

    @PostMapping()
    public ResponseEntity<Object> sendMoney(Authentication auth, @RequestBody SendMoneyRequest request) {
        User receiver;
        User sender;
        try {
            receiver = repository.findByLogin(request.getTo());
            sender = repository.findByLogin(auth.getName());
            System.out.println(request.getAmount());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (sender.getBalance().compareTo(request.getAmount()) < 0)
            return new ResponseEntity<>("Not enough money to send.", HttpStatus.BAD_REQUEST);

        try {
            sender.setBalance(sender.getBalance().subtract(request.getAmount()));
            repository.update(sender);
        } catch (Exception e) {
            return new ResponseEntity<>("Error in database, money not send, but not disappeared either", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            receiver.setBalance(receiver.getBalance().add(request.getAmount()));
            repository.update(receiver);
        } catch (Exception e) {
            return new ResponseEntity<>("Error in database, money not received, probably it's disappeared :(", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
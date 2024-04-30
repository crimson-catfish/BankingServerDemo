package ru.ivanov_savelii.bankingserverdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov_savelii.bankingserverdemo.model.SignUpRequest;
import ru.ivanov_savelii.bankingserverdemo.model.User;
import ru.ivanov_savelii.bankingserverdemo.repository.UserRepository;

import java.math.BigDecimal;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
//@RequestMapping("/")
public class UserController {

    @Autowired
    UserRepository repository;

    @Value("${startMoney}")
    private BigDecimal startMoney;


    @GetMapping("/money")
    public ResponseEntity<BigDecimal> getBalance(Long id) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        BigDecimal balance;

        try {
            balance = repository.findById(id).getBalance();
            if (balance == null) throw new Exception();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest request) {
        if (request.getLogin() == null || request.getPassword() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User user = new User(request.getLogin(), request.getPassword(), startMoney);

        try {
            repository.save(user);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/moneyId")
    public ResponseEntity<String> sendMoneyById(@RequestBody Long senderId, Long receiverId, BigDecimal amount) {
        User sender;
        User receiver;

        try {
            sender = repository.findById(senderId);
        } catch (Exception e) {
            return new ResponseEntity<>("Sender not found", HttpStatus.NOT_FOUND);
        }

        try {
            receiver = repository.findById(receiverId);
        } catch (Exception e) {
            return new ResponseEntity<>("Receiver not found", HttpStatus.NOT_FOUND);
        }

        if (sender.getBalance().compareTo(amount) < 0)
            return new ResponseEntity<>("Not enough money to send chosen amount", HttpStatus.BAD_REQUEST);

        try {
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            repository.update(sender);
        } catch (Exception e) {
            return new ResponseEntity<>("can't update sender data due to database error, money not send from sender account", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            repository.update(receiver);
        } catch (Exception e) {
            // probably money just disappeared :(
            return new ResponseEntity<>(
                    "FATAL: can't update receiver data due to database error, at this point probably some money disappeared from this world :(",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                "Send " + amount.toString() + " from " + sender.getLogin() + " to " + receiver.getLogin(),
                HttpStatus.OK);
    }
}
package ru.ivanov_savelii.bankingserverdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ivanov_savelii.bankingserverdemo.entity.User;
import ru.ivanov_savelii.bankingserverdemo.repository.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Value("${startMoney}")
    private BigDecimal startMoney;

    public User getByLogin(String login) {
        try {
            return repository.findByLogin(login);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByLogin(username);

        if (user == null) return null;

        return org.springframework.security.core.userdetails.User.withUsername(user.getLogin())
                .password(user.getPassword())
                .build();
    }
}
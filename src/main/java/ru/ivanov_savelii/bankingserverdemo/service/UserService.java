package ru.ivanov_savelii.bankingserverdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository repository;

    @Value("${startMoney}")
    private BigDecimal startMoney;

    public void save(User user) {
        repository.save(user);
    }

    public void create(User user) {
        if (repository.existsByLogin(user.getLogin())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        save(user);
    }

    public UserDetailsService userDetailsService() {
        return this::getByLogin;
    }

    public User getCurrentUser() {
        var login = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByLogin(login);
    }

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
                .password(user.getEncryptedPassword())
                .build();
    }
}
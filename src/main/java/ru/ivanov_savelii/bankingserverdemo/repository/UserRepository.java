package ru.ivanov_savelii.bankingserverdemo.repository;

import ru.ivanov_savelii.bankingserverdemo.model.User;

public interface UserRepository {

    int save(User user);
    int update(User user);
    User findById(Long id);
    User findByLogin(String login);
    int deleteById(Long id);
}

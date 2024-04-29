package ru.ivanov_savelii.bankingserverdemo.repository;

import ru.ivanov_savelii.bankingserverdemo.model.User;

public interface UserRepository {

    int save(User user);
    int update(User user);
    User findById(long id);
    User findByLogin(String login);
    int deleteById(long id);
}

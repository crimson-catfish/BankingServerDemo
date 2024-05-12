package ru.ivanov_savelii.bankingserverdemo.repository;

import ru.ivanov_savelii.bankingserverdemo.entity.User;

public interface UserRepository {

    void save(User user);
    void update(User user);
    User findByLogin(String login);
    void deleteById(Long id);
}

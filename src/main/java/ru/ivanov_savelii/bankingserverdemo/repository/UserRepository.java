package ru.ivanov_savelii.bankingserverdemo.repository;

import ru.ivanov_savelii.bankingserverdemo.entity.User;

public interface UserRepository {

    int save(User user);
    int update(User user);
    User findById(Long id);
    User findByLogin(String login);
    boolean existsByLogin(String login);
    int deleteById(Long id);
}

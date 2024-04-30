package ru.ivanov_savelii.bankingserverdemo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ivanov_savelii.bankingserverdemo.model.User;

@Repository
public class JdbcUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(User user) {
        return jdbcTemplate.update(
                "INSERT INTO users (login, passhash, balance) VALUES (?,?,?)",
                user.getLogin(), user.getPasshash(), user.getBalance()
        );
    }

    @Override
    public int update(User user) {
        return jdbcTemplate.update(
                "UPDATE users SET login=?, passhash=?, balance=? WHERE id=?",
                user.getLogin(), user.getPasshash(), user.getBalance(), user.getId()
        );
    }

    @Override
    public User findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE id=?",
                    BeanPropertyRowMapper.newInstance(User.class), id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findByLogin(String login) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE login=?",
                    BeanPropertyRowMapper.newInstance(User.class), login);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
    }
}
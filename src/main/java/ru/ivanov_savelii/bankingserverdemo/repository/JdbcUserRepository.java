package ru.ivanov_savelii.bankingserverdemo.repository;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ivanov_savelii.bankingserverdemo.entity.User;

@Repository
@NoArgsConstructor
public class JdbcUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (login, password, balance) VALUES (?,?,?)",
                user.getLogin(), user.getPassword(), user.getBalance()
        );
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update(
                "UPDATE users SET login=?, password=?, balance=? WHERE id=?",
                user.getLogin(), user.getPassword(), user.getBalance(), user.getId()
        );
    }

    @Override
    public User findByLogin(String login) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE login=?",
                    BeanPropertyRowMapper.newInstance(User.class),
                    login
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
    }
}

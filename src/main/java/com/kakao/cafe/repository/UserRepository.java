package com.kakao.cafe.repository;

import com.kakao.cafe.domain.User;
import com.kakao.cafe.mapper.UserMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into USER_TABLE (USERID, PASSWORD, NAME, EMAIL, TIME) values (?,?,?,?,now())", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            return ps;
        }, keyHolder);
        return (Long)keyHolder.getKey();
    }

    public List<User> findAll() {
        return jdbcTemplate.query("select * from USER_TABLE", UserMapper.INSTANCE);
    }

    public Optional<User> findByUserId(String userId) {
        return jdbcTemplate.
                query("select * from USER_TABLE where userId = ?", UserMapper.INSTANCE, userId).
                stream().findAny();
    }

    public Optional<User> findById(Long id) {
        return jdbcTemplate.
                query("select * from USER_TABLE where id = ?", UserMapper.INSTANCE, id).
                stream().findAny();
    }

    public void updateUser(Long id, User updateUser) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE USER_TABLE SET NAME = ?, EMAIL = ? where id = ?");
            ps.setString(1, updateUser.getName());
            ps.setString(2, updateUser.getEmail());
            ps.setString(3, String.valueOf(id));
            return ps;
        });
    }

}

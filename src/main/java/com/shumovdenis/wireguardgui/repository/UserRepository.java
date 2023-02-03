package com.shumovdenis.wireguardgui.repository;

import com.shumovdenis.wireguardgui.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteUser(String username){
        jdbcTemplate.update("DELETE from wgusers WHERE username = ?", username);
    }

    public void addUser(User user) {
        jdbcTemplate.update("INSERT INTO wgusers (username, email, allowedIPs, privatekey, publickey, lastHandShake)"
                        + " VALUES (?,?,?,?,?,?)",
                user.getUsername(),
                user.getUserEmail(),
                user.getAllowedIPs(),
                user.getPrivateKey(),
                user.getPublicKey(),
                user.getLastHandShake()
        );
    }

    public User findUser(String username) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM wgusers WHERE username = ?", username);

        String allowedIPs = null;
        String privatekey = null;
        String publickey = null;


        while (sqlRowSet.next()) {
            allowedIPs = sqlRowSet.getString("allowedIPs");
            privatekey = sqlRowSet.getString("privatekey");
            publickey = sqlRowSet.getString("publickey");
        }

        User user = new User(username, allowedIPs, privatekey, publickey);
        return user;
    }

    public SqlRowSet getAllUsers() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM wgusers");
        return sqlRowSet;
    }


}

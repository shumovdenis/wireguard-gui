package com.shumovdenis.wireguardgui.repository;

import com.shumovdenis.wireguardgui.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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


}

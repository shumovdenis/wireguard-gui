package com.shumovdenis.wireguardgui.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    String username;
    String userEmail;
    String lastHandShake;
    String privateKey;
    String publicKey;
    String allowedIPs;
    public User(String username, String allowedIPs, String privateKey, String publicKey) {
        this.username = username;
        this.allowedIPs = allowedIPs;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.lastHandShake = "None";
    }
}

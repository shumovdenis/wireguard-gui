package com.shumovdenis.wireguardgui.utils;

import com.shumovdenis.wireguardgui.entity.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateClientConfig {

    private static final String FILE_PATH = "/etc/wireguard/";
    private static final String CONFIG_DIR = "/etc/wireguard/config/";

    public Path createUserConfig(User user, String serverIP) {

        File configDir = new File(CONFIG_DIR);
        if(!configDir.exists()) {
            configDir.mkdir();
        }

        Path userConfig = Paths.get(CONFIG_DIR + user.getUsername());

        String serverPublicKey = "NO";

        try {
            serverPublicKey = Files.readString(Path.of(FILE_PATH + "publickey"));
            serverPublicKey = serverPublicKey.substring(1, serverPublicKey.length() - 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String config = configTextBlock(user.getPrivateKey(), user.getAllowedIPs(),
                serverPublicKey, serverIP);

        try {
            Path file =  Files.write(userConfig, config.getBytes());
            return file;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String configTextBlock(String userPrivateKey, String allowedIPs,
                                  String serverPublicKey, String serverIP) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Interface]" + "\n")
                .append("PrivateKey = " + userPrivateKey + "\n")
                .append("Address = " + allowedIPs + "\n")
                .append("DNS = 8.8.8.8" + "\n")
                .append("\n")
                .append("[Peer]" + "\n")
                .append("PublicKey = " + serverPublicKey + "\n")
                .append("Endpoint = " + serverIP + "\n")
                .append("AllowedIPs = 0.0.0.0/0" + "\n")
                .append("PersistentKeepalive = 20");
        return sb.toString();
    }

}

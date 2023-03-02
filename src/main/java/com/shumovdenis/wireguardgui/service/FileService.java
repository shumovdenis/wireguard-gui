package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.utils.GenUserKeysScript;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;

@Service
public class FileService {

    @Value("${wg.file.path}")
    private String FILE_PATH;


    public HashMap<String, String> createPublicAndPrivateKeys(String username, String allowedIps) {
        GenUserKeysScript genKeys = new GenUserKeysScript();
        genKeys.executeCommands(username);
        String publicKey = readFile(Path.of(FILE_PATH + username + "_publickey")).toString();
        publicKey = publicKey.substring(1, publicKey.length() - 1);
        String privateKey = readFile(Path.of(FILE_PATH + username + "_privatekey")).toString();
        privateKey = privateKey.substring(1, privateKey.length() - 1);
        HashMap<String, String> keys = new HashMap<>();
        keys.put("privateKey", privateKey);
        keys.put("publicKey", publicKey);
        return keys;
    }

    public void addUserToConfigFile(User user){
        String peer = peerTextBlock(user.getUsername(), user.getPublicKey(), user.getAllowedIPs());
        writeFile(peer, FILE_PATH + "wg0.conf");
    }

    public String peerTextBlock(String username, String publicKey, String allowedIPs) {
        String sb = "#" + username + "\n" +
                "[Peer]\n" +
                "PublicKey = " + publicKey + "\n" +
                "AllowedIPs = " + allowedIPs + "\n\n";
        return sb;
    }

    public void writeFile(String peer, String filepath) {
        try {
            Files.write(Paths.get(filepath), peer.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public List<String> readFile(Path path)  {
        List<String> list = null;
        try {
            list = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }



}

package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.utils.GenUserKeysScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class UserService {

    public void addUser(String username, String allowedIPs) {
        GenUserKeysScript genKeys = new GenUserKeysScript();
        //добавить обработку ошибок
        try {
            genKeys.executeCommands(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String privateKey = null;
        String publicKey = null;

        //добавить обработку ошибок
        try {
            publicKey = readFile(Path.of("/etc/wireguard/" + username + "_publickey")).toString();
            privateKey =  readFile(Path.of("/etc/wireguard/" + username + "_privatekey")).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        User user = new User(username, allowedIPs, publicKey, privateKey);

        String peer = addPeerToConf(user.getUsername(), user.getPublicKey(), user.getAllowedIPs());
        writeFile(peer, "/etc/wireguard/wg0conf");

    }


    public static String addPeerToConf(String username, String publicKey, String allowedIPs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Peer]\n")
                .append("#" + username + "\n")
                .append("PublicKey = " + publicKey + "\n")
                .append("AllowedIPs = " + allowedIPs + "\n\n");
        return sb.toString();
    }

    public List<String> readFile(Path path) throws IOException {
        List<String> list = Files.readAllLines(path);
        return list;
    }



    public void writeFile(String peer, String filepath) {
        try {
            Files.write(Paths.get(filepath), peer.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e);
        }
    }


}

//    public static List<String> readFile1(Path path) throws IOException {
//        List<String> list = Files.readAllLines(path);
//        for (String str : list)
//            System.out.println(str);
//        return list;
//    }
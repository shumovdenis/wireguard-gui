package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.repository.UserRepository;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    public UserService(UserRepository userRepository, FileService fileService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    public List<User> getUsers() {
        SqlRowSet result = userRepository.getAllUsers();
        List<User> userList = new ArrayList<>();
        while (result.next()) {
            User user = new User(
                    result.getInt("id"),
                    result.getString("username"),
                    result.getString("email"),
                    result.getString("lastHandShake"),
                    result.getString("allowedIPs")
            );
            userList.add(user);
        }
        return userList;
    }


    public void addUser(String username, String allowedIPs) {
        HashMap<String, String> keys = new HashMap<>();

        try {
            keys = fileService.createPublicAndPrivateKeys(username);
        } catch (IOException e) {
            System.out.println("Can not create Keys");
        }

        User user = new User(username, allowedIPs, keys.get("privateKey"), keys.get("publicKey"));
        try {
            fileService.addUserToConfigFile(user);
            userRepository.addUser(user);
        } catch (IOException e) {
            System.out.println("Error! addUser");
        }
    }

    public void deleteUser(String username) {
        userRepository.deleteUser(username);
        fileService.deletePeerFromConf(username);
        fileService.deleteKeysFiles(username);
    }


    //чтение из консоли
    public void wgShow() {
        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec("systemctl status  wg-quick@wg0");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
        }
    }

}

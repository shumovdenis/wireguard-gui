package com.shumovdenis.wireguardgui.controller;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.repository.UserRepository;
import com.shumovdenis.wireguardgui.service.UserService;
import com.shumovdenis.wireguardgui.utils.CreateClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class MainController {
    UserService userService;
    UserRepository userRepository;

    @Value("${wg.address}")
    private String serverIP;


    public MainController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PostMapping("/wgshow")
    public void wgShow() {
        userService.wgShow();
    }

    @DeleteMapping("/")
    public void deleteUser(@RequestParam("username") String username) {
        userService.deleteUser(username);
    }


    @PostMapping("/")
    public void addUser(@RequestParam("name") String username,
                        @RequestParam("allowedIPs") String allowedIPs
    ) {
        userService.addUser(username, allowedIPs);
    }


    @GetMapping("/cfg")
    public void getUserConfig(@RequestParam("username") String username) {
        User user = userRepository.findUser(username);
        CreateClientConfig ccc = new CreateClientConfig();
        ccc.createUserConfig(user, serverIP);
    }

    @GetMapping("/download")
    @ResponseBody
    public void download(@RequestParam("username") String username, HttpServletResponse response) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream("/etc/wireguard/config/" + username + ".conf");
            int len;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}

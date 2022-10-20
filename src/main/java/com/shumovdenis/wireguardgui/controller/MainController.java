package com.shumovdenis.wireguardgui.controller;

import com.shumovdenis.wireguardgui.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String hello() {
        return "hi!";
    }


    @PostMapping("/")
    public void addUser(@RequestParam("name") String username,
                        @RequestParam("allowedIPs") String allowedIPs
    ) {
        userService.addUser(username, allowedIPs);
    }
}

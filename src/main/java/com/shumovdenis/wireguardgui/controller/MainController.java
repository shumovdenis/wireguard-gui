package com.shumovdenis.wireguardgui.controller;

import com.shumovdenis.wireguardgui.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
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


}

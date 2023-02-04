package com.shumovdenis.wireguardgui.controller;

import com.shumovdenis.wireguardgui.repository.UserRepository;
import com.shumovdenis.wireguardgui.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ThController {

    UserService userService;

    @Value("${wg.address}")
    private String serverIP;


    public ThController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

    @PostMapping("/index")
    public String addUser(@RequestParam("name") String username,
                        @RequestParam("allowedIPs") String allowedIPs,
                          Model model
    ) {
        model.addAttribute("name", username);
        model.addAttribute("allowedIPs", allowedIPs);
        userService.addUser(username, allowedIPs);
        return "redirect:index";
    }


    @PostMapping("/index/delete")
    public String deleteUser(@RequestParam("username") String username) {
        userService.deleteUser(username);
        return "redirect:/index";
    }


}

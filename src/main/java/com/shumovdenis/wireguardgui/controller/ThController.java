package com.shumovdenis.wireguardgui.controller;

import com.shumovdenis.wireguardgui.repository.UserRepository;
import com.shumovdenis.wireguardgui.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ThController {

    UserService userService;
    UserRepository userRepository;

    @Value("${wg.address}")
    private String serverIP;


    public ThController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "index";
    }



}

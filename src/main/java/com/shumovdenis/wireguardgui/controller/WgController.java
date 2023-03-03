package com.shumovdenis.wireguardgui.controller;

import com.shumovdenis.wireguardgui.repository.UserRepository;
import com.shumovdenis.wireguardgui.service.FileService;
import com.shumovdenis.wireguardgui.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class WgController {

    private final UserService userService;
    private final FileService fileService;

    public WgController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping("/index")
    public String getPage(Model model) {
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

    @GetMapping("/index/download/{username}")
    public void downloadConfig(@PathVariable("username") String username, HttpServletResponse response) {
        fileService.downloadUserFileConfig(username, response);
    }


}

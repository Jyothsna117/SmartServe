package com.smartserve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller handling administrator authentication views.
 */
@Controller
public class AdminLoginController {

    @GetMapping("/admin/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid administrator credentials. Please check your username and password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been successfully logged out of SmartServe.");
        }
        return "admin-login";
    }
}

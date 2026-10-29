package com.smartserve.controller;

import com.smartserve.service.ComplaintService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller serving the SmartServe landing page.
 */
@Controller
public class HomeController {

    private final ComplaintService complaintService;

    public HomeController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            model.addAttribute("metrics", complaintService.getStatusMetrics());
        } catch (Exception e) {
            // Graceful fallback if database is not reachable on cold start
            model.addAttribute("dbOffline", true);
        }
        return "index";
    }
}

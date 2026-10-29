package com.smartserve.controller;

import com.smartserve.dto.ComplaintRequestDto;
import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintCategory;
import com.smartserve.service.ComplaintService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Public controller managing complaint submission and citizen tracking workflows.
 */
@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private static final Logger log = LoggerFactory.getLogger(ComplaintController.class);
    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /**
     * Show complaint submission form.
     */
    @GetMapping("/new")
    public String showComplaintForm(Model model) {
        if (!model.containsAttribute("complaintForm")) {
            model.addAttribute("complaintForm", new ComplaintRequestDto());
        }
        model.addAttribute("categories", ComplaintCategory.values());
        return "submit-complaint";
    }

    /**
     * Process complaint submission with server-side validation.
     */
    @PostMapping
    public String submitComplaint(
            @Valid @ModelAttribute("complaintForm") ComplaintRequestDto form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for complaint submission: {}", bindingResult.getAllErrors());
            model.addAttribute("categories", ComplaintCategory.values());
            return "submit-complaint";
        }

        try {
            Complaint saved = complaintService.createComplaint(form);
            redirectAttributes.addFlashAttribute("trackingId", saved.getTrackingId());
            redirectAttributes.addFlashAttribute("complaint", saved);
            return "redirect:/complaints/success";
        } catch (Exception ex) {
            log.error("Failed to persist complaint: {}", ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Unable to submit complaint. Database connection failed. Please verify MongoDB service.");
            model.addAttribute("categories", ComplaintCategory.values());
            return "submit-complaint";
        }
    }

    /**
     * Confirmation page displayed upon successful submission.
     */
    @GetMapping("/success")
    public String showSuccessPage(Model model) {
        if (!model.containsAttribute("trackingId")) {
            // If visited directly without prior submission redirect to home
            return "redirect:/";
        }
        return "complaint-success";
    }

    /**
     * Citizen complaint tracking search and view endpoint.
     */
    @GetMapping("/track")
    public String trackComplaint(
            @RequestParam(value = "trackingId", required = false) String trackingId,
            Model model) {

        if (trackingId != null && !trackingId.trim().isEmpty()) {
            String sanitizedId = trackingId.trim().toUpperCase();
            model.addAttribute("searchedId", sanitizedId);

            Optional<Complaint> complaintOpt = complaintService.getComplaintByTrackingId(sanitizedId);
            if (complaintOpt.isPresent()) {
                model.addAttribute("complaint", complaintOpt.get());
            } else {
                model.addAttribute("notFound", true);
            }
        }
        return "track-complaint";
    }
}

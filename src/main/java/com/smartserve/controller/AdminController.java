package com.smartserve.controller;

import com.smartserve.dto.ComplaintStatusUpdateDto;
import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintStatus;
import com.smartserve.service.ComplaintService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller providing administrative oversight, filtering, and status progression operations.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final ComplaintService complaintService;

    public AdminController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /**
     * Admin Dashboard displaying system metrics, complaints list, and status filtering.
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(value = "status", required = false) ComplaintStatus status,
            Model model) {

        List<Complaint> complaints;
        if (status != null) {
            complaints = complaintService.getComplaintsByStatus(status);
            model.addAttribute("selectedStatus", status);
        } else {
            complaints = complaintService.getAllComplaints();
        }

        Map<String, Long> metrics = complaintService.getStatusMetrics();

        model.addAttribute("complaints", complaints);
        model.addAttribute("metrics", metrics);
        model.addAttribute("allStatuses", ComplaintStatus.values());
        return "admin-dashboard";
    }

    /**
     * View detailed complaint information and status update form.
     */
    @GetMapping("/complaints/{trackingId}")
    public String viewComplaintDetail(@PathVariable("trackingId") String trackingId, Model model) {
        Optional<Complaint> complaintOpt = complaintService.getComplaintByTrackingId(trackingId);

        if (complaintOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Complaint not found with Tracking ID: " + trackingId);
            return "redirect:/admin/dashboard";
        }

        Complaint complaint = complaintOpt.get();
        if (!model.containsAttribute("updateForm")) {
            ComplaintStatusUpdateDto form = new ComplaintStatusUpdateDto();
            form.setStatus(complaint.getStatus());
            form.setAdminRemarks(complaint.getAdminRemarks());
            model.addAttribute("updateForm", form);
        }

        model.addAttribute("complaint", complaint);
        model.addAttribute("allStatuses", ComplaintStatus.values());
        return "admin-update";
    }

    /**
     * Process status and remarks update for a complaint.
     */
    @PostMapping("/complaints/{trackingId}/status")
    public String updateStatus(
            @PathVariable("trackingId") String trackingId,
            @Valid @ModelAttribute("updateForm") ComplaintStatusUpdateDto form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            Optional<Complaint> complaintOpt = complaintService.getComplaintByTrackingId(trackingId);
            complaintOpt.ifPresent(c -> model.addAttribute("complaint", c));
            model.addAttribute("allStatuses", ComplaintStatus.values());
            return "admin-update";
        }

        try {
            Complaint updated = complaintService.updateComplaintStatus(trackingId, form.getStatus(), form.getAdminRemarks());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Complaint " + updated.getTrackingId() + " successfully updated to " + updated.getStatus().getDisplayName() + ".");
            return "redirect:/admin/dashboard";
        } catch (Exception ex) {
            log.error("Failed to update complaint {}: {}", trackingId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating complaint: " + ex.getMessage());
            return "redirect:/admin/complaints/" + trackingId;
        }
    }
}

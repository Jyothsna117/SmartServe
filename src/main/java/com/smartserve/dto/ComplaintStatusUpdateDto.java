package com.smartserve.dto;

import com.smartserve.model.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for administrator status updates.
 */
public class ComplaintStatusUpdateDto {

    @NotNull(message = "Please select a valid complaint status.")
    private ComplaintStatus status;

    @Size(max = 500, message = "Administrator remarks cannot exceed 500 characters.")
    private String adminRemarks;

    public ComplaintStatusUpdateDto() {
    }

    public ComplaintStatusUpdateDto(ComplaintStatus status, String adminRemarks) {
        this.status = status;
        this.adminRemarks = adminRemarks;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }
}

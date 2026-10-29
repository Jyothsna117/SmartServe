package com.smartserve.dto;

import com.smartserve.model.ComplaintCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for public complaint submission form.
 * Enforces server-side validation to guarantee data integrity.
 */
public class ComplaintRequestDto {

    @NotBlank(message = "Complaint title is required.")
    @Size(min = 5, max = 100, message = "Complaint title must be between 5 and 100 characters.")
    private String title;

    @NotNull(message = "Please select a valid complaint category.")
    private ComplaintCategory category;

    @NotBlank(message = "Detailed description is required.")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters.")
    private String description;

    public ComplaintRequestDto() {
    }

    public ComplaintRequestDto(String title, ComplaintCategory category, String description) {
        this.title = title;
        this.category = category;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.smartserve.model;

import com.smartserve.dto.ComplaintRequestDto;
import com.smartserve.dto.ComplaintStatusUpdateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests verifying Jakarta Bean Validation constraints on complaint DTOs.
 */
class ComplaintValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid ComplaintRequestDto should pass validation with zero violations")
    void testValidComplaintRequest() {
        ComplaintRequestDto dto = new ComplaintRequestDto(
                "Overflowing Garbage Dumpster",
                ComplaintCategory.SANITATION,
                "The garbage dumpster on 4th Main road has not been cleared for three days."
        );

        Set<ConstraintViolation<ComplaintRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Blank title should violate @NotBlank constraint")
    void testBlankTitleViolation() {
        ComplaintRequestDto dto = new ComplaintRequestDto(
                "",
                ComplaintCategory.SANITATION,
                "Detailed valid description of civic grievance."
        );

        Set<ConstraintViolation<ComplaintRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("Title shorter than 5 characters should violate @Size constraint")
    void testShortTitleViolation() {
        ComplaintRequestDto dto = new ComplaintRequestDto(
                "Leak",
                ComplaintCategory.WATER,
                "Detailed valid description of civic grievance."
        );

        Set<ConstraintViolation<ComplaintRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("Null category should violate @NotNull constraint")
    void testNullCategoryViolation() {
        ComplaintRequestDto dto = new ComplaintRequestDto(
                "Valid Title of Complaint",
                null,
                "Detailed valid description of civic grievance."
        );

        Set<ConstraintViolation<ComplaintRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    @DisplayName("Description shorter than 10 characters should violate @Size constraint")
    void testShortDescriptionViolation() {
        ComplaintRequestDto dto = new ComplaintRequestDto(
                "Valid Title of Complaint",
                ComplaintCategory.ELECTRICITY,
                "Too short"
        );

        Set<ConstraintViolation<ComplaintRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    @DisplayName("Valid ComplaintStatusUpdateDto should pass validation")
    void testValidStatusUpdateDto() {
        ComplaintStatusUpdateDto dto = new ComplaintStatusUpdateDto(
                ComplaintStatus.RESOLVED,
                "Team replaced faulty capacitor and restored power."
        );

        Set<ConstraintViolation<ComplaintStatusUpdateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Null status in ComplaintStatusUpdateDto should violate @NotNull")
    void testNullStatusUpdateDto() {
        ComplaintStatusUpdateDto dto = new ComplaintStatusUpdateDto(null, "Remarks without status");

        Set<ConstraintViolation<ComplaintStatusUpdateDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }
}

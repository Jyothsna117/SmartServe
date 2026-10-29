package com.smartserve.model;

/**
 * Enumeration of supported complaint lifecycle statuses in SmartServe.
 */
public enum ComplaintStatus {
    SUBMITTED("Submitted", "secondary", "Your complaint has been successfully registered and is awaiting admin review."),
    IN_PROGRESS("In Progress", "warning text-dark", "An official team has been assigned and resolution work is currently underway."),
    RESOLVED("Resolved", "success", "The reported issue has been addressed and verified resolved by the civic team."),
    REJECTED("Rejected", "danger", "The complaint was closed or declined due to duplicates, jurisdiction, or insufficient details.");

    private final String displayName;
    private final String badgeClass;
    private final String description;

    ComplaintStatus(String displayName, String badgeClass, String description) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getDescription() {
        return description;
    }
}

package com.smartserve.model;

/**
 * Enumeration of supported complaint categories in SmartServe.
 */
public enum ComplaintCategory {
    SANITATION("Sanitation & Waste Management", "Garbage collection, drainage overflow, street cleaning"),
    WATER("Water Supply & Sewage", "Pipeline leaks, water contamination, low pressure"),
    ELECTRICITY("Electricity & Power", "Streetlights, power outages, damaged transformers"),
    ROADS("Roads & Infrastructure", "Potholes, damaged footpaths, road obstruction"),
    OTHER("General Civic Issues", "Public nuisance, park maintenance, other municipal services");

    private final String displayName;
    private final String description;

    ComplaintCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

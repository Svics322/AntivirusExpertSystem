package com.example.antivirus.model;

public record CriterionOption(
        String criterionId,
        String id,
        String label,
        int sortOrder,
        boolean active
) {
    @Override
    public String toString() {
        return label;
    }
}

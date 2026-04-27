package com.example.antivirus.model;

public record Criterion(
        String id,
        String title,
        int sortOrder,
        boolean active
) {
    @Override
    public String toString() {
        return title;
    }
}

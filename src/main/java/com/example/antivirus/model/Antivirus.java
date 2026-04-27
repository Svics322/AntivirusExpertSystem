package com.example.antivirus.model;

public record Antivirus(
        String id,
        String name,
        String shortAdvice,
        String fullAdvice,
        boolean active
) {
    @Override
    public String toString() {
        return name;
    }
}

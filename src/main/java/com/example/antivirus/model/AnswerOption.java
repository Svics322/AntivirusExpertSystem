package com.example.antivirus.model;

public record AnswerOption(String id, String text) {
    @Override
    public String toString() {
        return text;
    }
}

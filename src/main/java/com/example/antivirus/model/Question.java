package com.example.antivirus.model;

import java.util.List;

public record Question(String id, String text, List<AnswerOption> options) {
}

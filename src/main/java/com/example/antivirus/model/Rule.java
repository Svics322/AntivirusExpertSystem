package com.example.antivirus.model;

import java.util.List;
import java.util.stream.Collectors;

public record Rule(
        String id,
        String antivirusId,
        String antivirusName,
        String title,
        int weight,
        String explanation,
        boolean active,
        List<RuleCondition> conditions
) {
    public String conditionText() {
        if (conditions == null || conditions.isEmpty()) {
            return "Без умов";
        }
        return conditions.stream()
                .map(RuleCondition::readableText)
                .collect(Collectors.joining("; "));
    }
}

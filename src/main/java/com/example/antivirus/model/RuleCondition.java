package com.example.antivirus.model;

import java.util.Map;

public record RuleCondition(
        String criterionId,
        String optionId,
        String criterionTitle,
        String optionLabel
) {
    public boolean matches(Map<String, String> answers) {
        return optionId.equals(answers.get(criterionId));
    }

    public String readableText() {
        String left = criterionTitle == null || criterionTitle.isBlank() ? criterionId : criterionTitle;
        String right = optionLabel == null || optionLabel.isBlank() ? optionId : optionLabel;
        return left + " = " + right;
    }
}

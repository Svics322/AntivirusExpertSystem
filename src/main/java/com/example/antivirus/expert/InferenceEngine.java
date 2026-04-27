package com.example.antivirus.expert;

import com.example.antivirus.model.Antivirus;
import com.example.antivirus.model.EvaluationResult;
import com.example.antivirus.model.Rule;
import com.example.antivirus.model.RuleCondition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InferenceEngine {
    private static final double PARTIAL_RULE_THRESHOLD = 0.67;

    public List<EvaluationResult> evaluate(List<Antivirus> antiviruses, List<Rule> rules, Map<String, String> answers) {
        Map<String, EvaluationResult> results = new LinkedHashMap<>();
        for (Antivirus antivirus : antiviruses) {
            results.put(antivirus.id(), new EvaluationResult(antivirus));
        }

        for (Rule rule : rules) {
            EvaluationResult result = results.get(rule.antivirusId());
            if (result == null || !rule.active()) {
                continue;
            }

            result.addMaxScore(rule.weight());
            double matchRatio = calculateMatchRatio(rule, answers);
            if (matchRatio >= PARTIAL_RULE_THRESHOLD) {
                double earned = rule.weight() * matchRatio;
                result.addEvidence(earned, String.format(
                        "%s — %s: збіг %.0f%%. %s",
                        rule.id(),
                        rule.title(),
                        matchRatio * 100,
                        rule.explanation()
                ));
            }
        }

        List<EvaluationResult> sorted = new ArrayList<>(results.values());
        sorted.sort(Comparator.naturalOrder());
        return sorted;
    }

    private double calculateMatchRatio(Rule rule, Map<String, String> answers) {
        if (rule.conditions() == null || rule.conditions().isEmpty()) {
            return 0;
        }
        int matched = 0;
        for (RuleCondition condition : rule.conditions()) {
            if (condition.matches(answers)) {
                matched++;
            }
        }
        return (double) matched / rule.conditions().size();
    }
}

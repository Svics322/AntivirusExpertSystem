package com.example.antivirus.model;

import java.util.ArrayList;
import java.util.List;

public final class EvaluationResult implements Comparable<EvaluationResult> {
    private final Antivirus antivirus;
    private final List<String> evidence = new ArrayList<>();
    private double score;
    private double maxScore;

    public EvaluationResult(Antivirus antivirus) {
        this.antivirus = antivirus;
    }

    public Antivirus antivirus() {
        return antivirus;
    }

    public double score() {
        return score;
    }

    public double maxScore() {
        return maxScore;
    }

    public int percentage() {
        if (maxScore <= 0) {
            return 0;
        }
        return Math.min(100, (int) Math.round(score * 100.0 / maxScore));
    }

    public List<String> evidence() {
        return evidence;
    }

    public void addMaxScore(double value) {
        maxScore += value;
    }

    public void addEvidence(double value, String item) {
        score += value;
        evidence.add(item);
    }

    @Override
    public int compareTo(EvaluationResult other) {
        int byPercent = Integer.compare(other.percentage(), percentage());
        if (byPercent != 0) {
            return byPercent;
        }
        return Double.compare(other.score, score);
    }
}

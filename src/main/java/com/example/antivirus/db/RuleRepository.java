package com.example.antivirus.db;

import com.example.antivirus.model.Rule;
import com.example.antivirus.model.RuleCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuleRepository {
    public List<Rule> search(String query) {
        String sql = """
                SELECT r.id, r.antivirus_id, a.name AS antivirus_name, r.title, r.weight, r.explanation, r.active
                FROM rules r
                JOIN antiviruses a ON a.id = r.antivirus_id
                WHERE lower(r.id) LIKE ? OR lower(r.title) LIKE ? OR lower(r.explanation) LIKE ? OR lower(a.name) LIKE ?
                ORDER BY r.id
                """;
        String like = "%" + normalize(query) + "%";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                statement.setString(i, like);
            }
            try (ResultSet rs = statement.executeQuery()) {
                return mapRules(connection, rs);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка пошуку правил: " + ex.getMessage(), ex);
        }
    }

    public List<Rule> findActive() {
        String sql = """
                SELECT r.id, r.antivirus_id, a.name AS antivirus_name, r.title, r.weight, r.explanation, r.active
                FROM rules r
                JOIN antiviruses a ON a.id = r.antivirus_id
                WHERE r.active = 1 AND a.active = 1
                ORDER BY r.id
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapRules(connection, rs);
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка читання правил: " + ex.getMessage(), ex);
        }
    }

    public void save(Rule rule) {
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                save(connection, rule);
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка збереження правила: " + ex.getMessage(), ex);
        }
    }

    public void save(Connection connection, Rule rule) throws SQLException {
        String sql = """
                INSERT INTO rules(id, antivirus_id, title, weight, explanation, active)
                VALUES(?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    antivirus_id = excluded.antivirus_id,
                    title = excluded.title,
                    weight = excluded.weight,
                    explanation = excluded.explanation,
                    active = excluded.active
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rule.id());
            statement.setString(2, rule.antivirusId());
            statement.setString(3, rule.title());
            statement.setInt(4, rule.weight());
            statement.setString(5, rule.explanation());
            statement.setInt(6, rule.active() ? 1 : 0);
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rule_conditions WHERE rule_id = ?")) {
            statement.setString(1, rule.id());
            statement.executeUpdate();
        }

        String insertCondition = "INSERT INTO rule_conditions(rule_id, criterion_id, option_id) VALUES(?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertCondition)) {
            for (RuleCondition condition : rule.conditions()) {
                statement.setString(1, rule.id());
                statement.setString(2, condition.criterionId());
                statement.setString(3, condition.optionId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public void delete(String id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM rules WHERE id = ?")) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка видалення правила: " + ex.getMessage(), ex);
        }
    }

    public void deleteAll(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rules")) {
            statement.executeUpdate();
        }
    }

    private List<Rule> mapRules(Connection connection, ResultSet rs) throws SQLException {
        List<Rule> items = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("id");
            items.add(new Rule(
                    id,
                    rs.getString("antivirus_id"),
                    rs.getString("antivirus_name"),
                    rs.getString("title"),
                    rs.getInt("weight"),
                    rs.getString("explanation"),
                    rs.getInt("active") == 1,
                    findConditions(connection, id)
            ));
        }
        return items;
    }

    private List<RuleCondition> findConditions(Connection connection, String ruleId) throws SQLException {
        String sql = """
                SELECT rc.criterion_id, rc.option_id, c.title AS criterion_title, o.label AS option_label
                FROM rule_conditions rc
                JOIN criteria c ON c.id = rc.criterion_id
                JOIN criterion_options o ON o.criterion_id = rc.criterion_id AND o.id = rc.option_id
                WHERE rc.rule_id = ?
                ORDER BY c.sort_order, o.sort_order
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ruleId);
            try (ResultSet rs = statement.executeQuery()) {
                List<RuleCondition> conditions = new ArrayList<>();
                while (rs.next()) {
                    conditions.add(new RuleCondition(
                            rs.getString("criterion_id"),
                            rs.getString("option_id"),
                            rs.getString("criterion_title"),
                            rs.getString("option_label")
                    ));
                }
                return conditions;
            }
        }
    }

    private String normalize(String query) {
        return query == null ? "" : query.toLowerCase().trim();
    }
}

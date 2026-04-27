package com.example.antivirus.db;

import com.example.antivirus.model.AnswerOption;
import com.example.antivirus.model.Criterion;
import com.example.antivirus.model.CriterionOption;
import com.example.antivirus.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CriterionRepository {
    public List<Criterion> searchCriteria(String query) {
        String sql = """
                SELECT id, title, sort_order, active
                FROM criteria
                WHERE lower(id) LIKE ? OR lower(title) LIKE ?
                ORDER BY sort_order, title
                """;
        String like = "%" + normalize(query) + "%";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, like);
            statement.setString(2, like);
            try (ResultSet rs = statement.executeQuery()) {
                return mapCriteria(rs);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка пошуку критеріїв: " + ex.getMessage(), ex);
        }
    }

    public List<Question> findActiveQuestions() {
        List<Question> questions = new ArrayList<>();
        for (Criterion criterion : findActiveCriteria()) {
            List<AnswerOption> options = findActiveOptions(criterion.id()).stream()
                    .map(item -> new AnswerOption(item.id(), item.label()))
                    .toList();
            questions.add(new Question(criterion.id(), criterion.title(), options));
        }
        return questions;
    }

    public List<Criterion> findActiveCriteria() {
        String sql = """
                SELECT id, title, sort_order, active
                FROM criteria
                WHERE active = 1
                ORDER BY sort_order, title
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapCriteria(rs);
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка читання критеріїв: " + ex.getMessage(), ex);
        }
    }

    public List<CriterionOption> searchOptions(String criterionId, String query) {
        String sql = """
                SELECT criterion_id, id, label, sort_order, active
                FROM criterion_options
                WHERE criterion_id = ? AND (lower(id) LIKE ? OR lower(label) LIKE ?)
                ORDER BY sort_order, label
                """;
        String like = "%" + normalize(query) + "%";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, criterionId);
            statement.setString(2, like);
            statement.setString(3, like);
            try (ResultSet rs = statement.executeQuery()) {
                return mapOptions(rs);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка пошуку варіантів критерію: " + ex.getMessage(), ex);
        }
    }

    public List<CriterionOption> findActiveOptions(String criterionId) {
        String sql = """
                SELECT criterion_id, id, label, sort_order, active
                FROM criterion_options
                WHERE criterion_id = ? AND active = 1
                ORDER BY sort_order, label
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, criterionId);
            try (ResultSet rs = statement.executeQuery()) {
                return mapOptions(rs);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка читання варіантів критерію: " + ex.getMessage(), ex);
        }
    }

    public void saveCriterion(Criterion criterion) {
        try (Connection connection = DatabaseManager.getConnection()) {
            saveCriterion(connection, criterion);
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка збереження критерію: " + ex.getMessage(), ex);
        }
    }

    public void saveCriterion(Connection connection, Criterion criterion) throws SQLException {
        String sql = """
                INSERT INTO criteria(id, title, sort_order, active)
                VALUES(?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    title = excluded.title,
                    sort_order = excluded.sort_order,
                    active = excluded.active
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, criterion.id());
            statement.setString(2, criterion.title());
            statement.setInt(3, criterion.sortOrder());
            statement.setInt(4, criterion.active() ? 1 : 0);
            statement.executeUpdate();
        }
    }

    public void saveOption(CriterionOption option) {
        try (Connection connection = DatabaseManager.getConnection()) {
            saveOption(connection, option);
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка збереження варіанта критерію: " + ex.getMessage(), ex);
        }
    }

    public void saveOption(Connection connection, CriterionOption option) throws SQLException {
        String sql = """
                INSERT INTO criterion_options(criterion_id, id, label, sort_order, active)
                VALUES(?, ?, ?, ?, ?)
                ON CONFLICT(criterion_id, id) DO UPDATE SET
                    label = excluded.label,
                    sort_order = excluded.sort_order,
                    active = excluded.active
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, option.criterionId());
            statement.setString(2, option.id());
            statement.setString(3, option.label());
            statement.setInt(4, option.sortOrder());
            statement.setInt(5, option.active() ? 1 : 0);
            statement.executeUpdate();
        }
    }

    public void deleteCriterion(String id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM criteria WHERE id = ?")) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка видалення критерію: " + ex.getMessage(), ex);
        }
    }

    public void deleteOption(String criterionId, String optionId) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM criterion_options WHERE criterion_id = ? AND id = ?")) {
            statement.setString(1, criterionId);
            statement.setString(2, optionId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка видалення варіанта критерію: " + ex.getMessage(), ex);
        }
    }

    public void deleteAll(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM criteria")) {
            statement.executeUpdate();
        }
    }

    private List<Criterion> mapCriteria(ResultSet rs) throws SQLException {
        List<Criterion> items = new ArrayList<>();
        while (rs.next()) {
            items.add(new Criterion(
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getInt("sort_order"),
                    rs.getInt("active") == 1
            ));
        }
        return items;
    }

    private List<CriterionOption> mapOptions(ResultSet rs) throws SQLException {
        List<CriterionOption> items = new ArrayList<>();
        while (rs.next()) {
            items.add(new CriterionOption(
                    rs.getString("criterion_id"),
                    rs.getString("id"),
                    rs.getString("label"),
                    rs.getInt("sort_order"),
                    rs.getInt("active") == 1
            ));
        }
        return items;
    }

    private String normalize(String query) {
        return query == null ? "" : query.toLowerCase().trim();
    }
}

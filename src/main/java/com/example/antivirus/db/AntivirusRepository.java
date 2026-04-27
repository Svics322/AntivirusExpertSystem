package com.example.antivirus.db;

import com.example.antivirus.model.Antivirus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AntivirusRepository {
    public List<Antivirus> search(String query) {
        String sql = """
                SELECT id, name, short_advice, full_advice, active
                FROM antiviruses
                WHERE lower(id) LIKE ? OR lower(name) LIKE ? OR lower(short_advice) LIKE ? OR lower(full_advice) LIKE ?
                ORDER BY name
                """;
        String like = "%" + normalize(query) + "%";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                statement.setString(i, like);
            }
            try (ResultSet rs = statement.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка пошуку антивірусів: " + ex.getMessage(), ex);
        }
    }

    public List<Antivirus> findActive() {
        String sql = """
                SELECT id, name, short_advice, full_advice, active
                FROM antiviruses
                WHERE active = 1
                ORDER BY name
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapList(rs);
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка читання антивірусів: " + ex.getMessage(), ex);
        }
    }

    public void save(Antivirus item) {
        try (Connection connection = DatabaseManager.getConnection()) {
            save(connection, item);
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка збереження антивірусу: " + ex.getMessage(), ex);
        }
    }

    public void save(Connection connection, Antivirus item) throws SQLException {
        String sql = """
                INSERT INTO antiviruses(id, name, short_advice, full_advice, active)
                VALUES(?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    short_advice = excluded.short_advice,
                    full_advice = excluded.full_advice,
                    active = excluded.active
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.id());
            statement.setString(2, item.name());
            statement.setString(3, item.shortAdvice());
            statement.setString(4, item.fullAdvice());
            statement.setInt(5, item.active() ? 1 : 0);
            statement.executeUpdate();
        }
    }

    public void delete(String id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM antiviruses WHERE id = ?")) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Помилка видалення антивірусу: " + ex.getMessage(), ex);
        }
    }

    public void deleteAll(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM antiviruses")) {
            statement.executeUpdate();
        }
    }

    private List<Antivirus> mapList(ResultSet rs) throws SQLException {
        List<Antivirus> items = new ArrayList<>();
        while (rs.next()) {
            items.add(new Antivirus(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("short_advice"),
                    rs.getString("full_advice"),
                    rs.getInt("active") == 1
            ));
        }
        return items;
    }

    private String normalize(String query) {
        return query == null ? "" : query.toLowerCase().trim();
    }
}

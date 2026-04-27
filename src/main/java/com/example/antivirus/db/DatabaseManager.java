package com.example.antivirus.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    public static final Path DB_PATH = Path.of("data", "antivirus_expert.sqlite");
    private static final String URL = "jdbc:sqlite:" + DB_PATH.toAbsolutePath();

    private DatabaseManager() {
    }

    public static void initialize() {
        try {
            Files.createDirectories(DB_PATH.getParent());
            createSchema();
            seedIfNeeded();
        } catch (Exception ex) {
            throw new IllegalStateException("Не вдалося підготувати SQLite БД: " + ex.getMessage(), ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private static void createSchema() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS antiviruses (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        short_advice TEXT NOT NULL,
                        full_advice TEXT NOT NULL,
                        active INTEGER NOT NULL DEFAULT 1
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS criteria (
                        id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        sort_order INTEGER NOT NULL DEFAULT 0,
                        active INTEGER NOT NULL DEFAULT 1
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS criterion_options (
                        criterion_id TEXT NOT NULL,
                        id TEXT NOT NULL,
                        label TEXT NOT NULL,
                        sort_order INTEGER NOT NULL DEFAULT 0,
                        active INTEGER NOT NULL DEFAULT 1,
                        PRIMARY KEY (criterion_id, id),
                        FOREIGN KEY (criterion_id) REFERENCES criteria(id) ON DELETE CASCADE
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS rules (
                        id TEXT PRIMARY KEY,
                        antivirus_id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        weight INTEGER NOT NULL DEFAULT 10,
                        explanation TEXT NOT NULL,
                        active INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY (antivirus_id) REFERENCES antiviruses(id) ON DELETE CASCADE
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS rule_conditions (
                        rule_id TEXT NOT NULL,
                        criterion_id TEXT NOT NULL,
                        option_id TEXT NOT NULL,
                        PRIMARY KEY (rule_id, criterion_id, option_id),
                        FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE,
                        FOREIGN KEY (criterion_id) REFERENCES criteria(id) ON DELETE CASCADE,
                        FOREIGN KEY (criterion_id, option_id) REFERENCES criterion_options(criterion_id, id) ON DELETE CASCADE
                    )
                    """);

            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_antiviruses_name ON antiviruses(name)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_criteria_order ON criteria(sort_order)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_options_order ON criterion_options(criterion_id, sort_order)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_rules_antivirus ON rules(antivirus_id)");
        }
    }

    private static void seedIfNeeded() throws SQLException {
        if (isEmpty("antiviruses") || isEmpty("criteria") || isEmpty("rules")) {
            SeedData.seed();
        }
    }

    private static boolean isEmpty(String tableName) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }
}

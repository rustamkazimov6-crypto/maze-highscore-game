package com.mycompany.assingnment3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String HOST_URL =
            "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/coinmaze?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASS = "root1234";

    public static void init() {
        try (Connection c = DriverManager.getConnection(HOST_URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS coinmaze");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String tableSql = """
                CREATE TABLE IF NOT EXISTS highscores (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    player_name VARCHAR(50) NOT NULL,
                    difficulty VARCHAR(10) NOT NULL,
                    seed BIGINT NOT NULL,
                    grid_w INT NOT NULL,
                    grid_h INT NOT NULL,
                    coins_total INT NOT NULL,
                    coins_collected INT NOT NULL,
                    elapsed_seconds DOUBLE NOT NULL,
                    steps INT NOT NULL,
                    score INT NOT NULL,
                    created_at_ms BIGINT NOT NULL
                );
                """;

        try (Connection c = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.execute(tableSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insert(Highscore s) {
        String sql = """
                INSERT INTO highscores
                (player_name, difficulty, seed, grid_w, grid_h, coins_total, coins_collected,
                 elapsed_seconds, steps, score, created_at_ms)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.playerName);
            ps.setString(2, s.difficulty);
            ps.setLong(3, s.seed);
            ps.setInt(4, s.gridW);
            ps.setInt(5, s.gridH);
            ps.setInt(6, s.coinsTotal);
            ps.setInt(7, s.coinsCollected);
            ps.setDouble(8, s.elapsedSeconds);
            ps.setInt(9, s.steps);
            ps.setInt(10, s.score);
            ps.setLong(11, s.createdAtMs);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Highscore> top10All() {
        String sql = "SELECT * FROM highscores ORDER BY score DESC, elapsed_seconds ASC LIMIT 10";
        return query(sql, null);
    }

    public static List<Highscore> top10ByDifficulty(String difficulty) {
        String sql = "SELECT * FROM highscores WHERE difficulty = ? ORDER BY score DESC, elapsed_seconds ASC LIMIT 10";
        return query(sql, difficulty);
    }

    private static List<Highscore> query(String sql, String diffOrNull) {
        List<Highscore> list = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (diffOrNull != null) ps.setString(1, diffOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Highscore s = new Highscore();
                    s.id = rs.getLong("id");
                    s.playerName = rs.getString("player_name");
                    s.difficulty = rs.getString("difficulty");
                    s.seed = rs.getLong("seed");
                    s.gridW = rs.getInt("grid_w");
                    s.gridH = rs.getInt("grid_h");
                    s.coinsTotal = rs.getInt("coins_total");
                    s.coinsCollected = rs.getInt("coins_collected");
                    s.elapsedSeconds = rs.getDouble("elapsed_seconds");
                    s.steps = rs.getInt("steps");
                    s.score = rs.getInt("score");
                    s.createdAtMs = rs.getLong("created_at_ms");
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}

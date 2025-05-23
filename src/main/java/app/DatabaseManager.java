package app;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "asteroids_game_db";
    private static final String USER = "root";
    private static final String PASSWORD = "pass1357";

    // Method to create the database and the high_scores table if they don't exist
    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Create the database if it doesn't exist
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            statement.executeUpdate(createDatabaseQuery);

            // Use the database
            statement.executeUpdate("USE " + DB_NAME);

            // Create the table if it doesn't exist
            String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS high_scores (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        score INT
                    )
                    """;
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert high score into the table and ensure only top 10 scores are stored
    public static void insertHighScore(int score) {
        try (Connection connection = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO high_scores (score) VALUES (?)")) {

            // Insert the new high score
            preparedStatement.setInt(1, score);
            preparedStatement.executeUpdate();

            // Ensure only the top 10 scores are kept
            String deleteQuery = """
            DELETE hs FROM high_scores hs
            JOIN (
                SELECT id
                FROM high_scores
                ORDER BY score DESC
                LIMIT 10, 18446744073709551615
            ) AS sub
            ON hs.id = sub.id;
            """;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(deleteQuery);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve the top 10 high scores from the table
    public static ArrayList<String> getTopHighScores() {
        ArrayList<String> highScores = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT score FROM high_scores ORDER BY score DESC LIMIT 10")) {

            int rank = 1;
            while (resultSet.next()) {
                int score = resultSet.getInt("score");
                highScores.add(rank + ". " + score + " points");
                rank++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highScores;
    }
}
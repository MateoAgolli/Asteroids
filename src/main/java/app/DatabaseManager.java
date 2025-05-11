package app;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/asteroids_game_db";
    private static final String USER = "root";
    private static final String PASSWORD = "pass1357";

    // Method to create the high_scores table (if it doesn't exist)
    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS high_scores ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "score INT)";
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert high score into the table and ensure only top 10 scores are stored
    public static void insertHighScore(int score) {
        String insertQuery = "INSERT INTO high_scores (score) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

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
        String selectQuery = "SELECT score FROM high_scores ORDER BY score DESC LIMIT 10";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

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
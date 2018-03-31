package codeforcescrawl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final DataSource db;

    public Database(DataSource db) {
        this.db = db;
    }

    public boolean alreadyScraped(int submissionId) throws SQLException {
        String query = "SELECT 1 FROM submissions WHERE submission_id = ?;";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, submissionId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void save(Submission submission) throws SQLException {
        String query = "INSERT INTO submissions (submission_id, source, status, " +
                    "language, problem) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, submission.getSubmissionId());
            statement.setString(2, submission.getSource());
            statement.setString(3, submission.getStatus());
            statement.setString(4, submission.getLanguage());
            statement.setString(5, submission.getProblem());
            statement.execute();
        }
    }

    public boolean urlAlreadyProcessed(String url) throws SQLException {
        String query = "SELECT 1 FROM urls WHERE url = ?;";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, url);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void markUrlAsProcessed(String url) throws SQLException {
        String query = "INSERT INTO urls (url) VALUES (?)";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, url);
            statement.execute();
        }
    }

    public boolean taskExists(String taskUrl) throws SQLException {
        String query = "SELECT 1 FROM tasks WHERE url = ?;";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, taskUrl);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }


    public void addTask(String taskUrl) throws SQLException {
        String query = "INSERT INTO tasks (url) VALUES (?)";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, taskUrl);
            statement.execute();
        }
    }

    public List<String> allTasks() throws SQLException {
        List<String> tasks = new ArrayList<>(0);
        String query = "SELECT url FROM tasks;";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String task = rs.getString(1);
                tasks.add(task);
            }
        }

        return tasks;
    }
}

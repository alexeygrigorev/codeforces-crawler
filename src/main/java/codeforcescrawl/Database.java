package codeforcescrawl;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    public List<String> allUnscrapedTasks() throws SQLException {
        List<String> tasks = new ArrayList<>(0);
        String query = "SELECT url FROM tasks WHERE scraped = false;";

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

    public boolean markTaskScraped(String url) throws SQLException {
        String sql = "UPDATE tasks SET scraped = true WHERE url = ?;";

        try (Connection connection = db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, url);
            return ps.execute();
        }
    }

    public void iterateOverAllScrapedSubmissions(Consumer<Submission> callback) throws SQLException {
        String query = "SELECT submission_id, source, language, status FROM submissions";

        try (Connection conn = db.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                int submissionId = rs.getInt(1);
                String source = rs.getString(2);
                String language = rs.getString(3);
                String status = rs.getString(4);
                callback.accept(new Submission(submissionId, language, status, null, source));
            }
        }
    }
}

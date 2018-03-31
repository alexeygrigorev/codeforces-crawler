package codeforcescrawl;

public class Submission {

    private final int submissionId;
    private final String language;
    private final String status;
    private final String problem;
    private final String source;

    public Submission(int submissionId, String language, String status, String problem,
                      String source) {
        this.submissionId = submissionId;
        this.language = language;
        this.status = status;
        this.problem = problem;
        this.source = source;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public String getLanguage() {
        return language;
    }

    public String getStatus() {
        return status;
    }

    public String getProblem() {
        return problem;
    }

    public String getSource() {
        return source;
    }
}



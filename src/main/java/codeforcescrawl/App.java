package codeforcescrawl;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            CodeExtractor.main(args);
        }

        String name = args[0];

        if ("get-tasks".equals(name)) {
            ProblemSubmissionUrlExtractor.main(args);
        } else if ("scrape".equals(name)) {
            CodeExtractor.main(args);
        }
    }
}

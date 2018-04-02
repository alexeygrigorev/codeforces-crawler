package codeforcescrawl;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            CodeExtractor.main(args);
        }

        String name = args[0];

        if ("scrape-tasks".equals(name)) {
            ProblemSubmissionUrlExtractor.main(args);
        } else if ("enqueue-tasks".equals(name)) {
            EnqueueTasks.main(args);
        } else if ("scrape-code".equals(name)) {
            CodeExtractor.main(args);
        } else {
            System.err.println("unrecognized parameter " + name);
            System.exit(1);
        }
    }
}

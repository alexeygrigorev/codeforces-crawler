package codeforcescrawl;

import java.util.Arrays;

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
        } else if ("get-data".equals(name)) {
            String[] bowArgs = Arrays.copyOfRange(args, 1, args.length);
            BowFeatureExtractor.main(bowArgs);
        } else {
            System.err.println("unrecognized parameter " + name);
            System.exit(1);
        }
    }
}

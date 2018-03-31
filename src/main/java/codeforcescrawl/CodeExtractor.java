package codeforcescrawl;

import com.google.common.collect.Queues;
import com.google.common.io.Closer;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CodeExtractor {

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.gecko.driver", "bin/geckodriver");

        int numExecutors = Integer.parseInt(System.getenv().getOrDefault("NUM_EXECUTORS", "1"));
        System.out.println("number of threads: " + numExecutors);

        Database db = Factory.createDatasource();

        List<String> allTasks = db.allTasks();
        Queue<String> queue = Queues.newLinkedBlockingQueue(allTasks);

        ExecutorService executor = Executors.newFixedThreadPool(numExecutors);
        Closer closer = Closer.create();

        for (int i = 0; i < numExecutors; i++) {
            WebDriver driver = Factory.createFirefoxDriver();
            CodeforcesScraper scraper = new CodeforcesScraper(driver, db);

            executor.submit(new CodeExtractorRunnable(queue, scraper));

            closer.register(scraper);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        closer.close();
    }

    private static class CodeExtractorRunnable implements Runnable {

        private final Queue<String> queue;
        private final CodeforcesScraper scraper;

        public CodeExtractorRunnable(Queue<String> queue, CodeforcesScraper scraper) {
            this.queue = queue;
            this.scraper = scraper;
        }

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                String url = queue.poll();
                try {
                    scraper.scrapeTask(url);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }


}

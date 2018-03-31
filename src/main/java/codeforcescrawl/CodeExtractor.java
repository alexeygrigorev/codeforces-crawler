package codeforcescrawl;

import com.google.common.collect.Queues;
import com.google.common.io.Closer;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
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

        CountDownLatch latch = new CountDownLatch(numExecutors);

        List<Runnable> tasks = new ArrayList<>(numExecutors);
        for (int i = 0; i < numExecutors; i++) {
            WebDriver driver = Factory.createFirefoxDriver();
            CodeforcesScraper scraper = new CodeforcesScraper(driver, db);
            closer.register(scraper);

            tasks.add(new CodeExtractorRunnable(queue, latch, scraper));
        }

        System.out.println("waiting 15 seconds for the drivers to start...");
        Thread.sleep(15000);

        for (Runnable task : tasks) {
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        System.out.println("waiting for tasks to finish...");
        latch.await();

        System.out.println("done, can shut down safely");
        closer.close();
    }

    private static class CodeExtractorRunnable implements Runnable {

        private final Queue<String> queue;
        private final CountDownLatch latch;
        private final CodeforcesScraper scraper;

        public CodeExtractorRunnable(Queue<String> queue, CountDownLatch latch, CodeforcesScraper scraper) {
            this.queue = queue;
            this.latch = latch;
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

            latch.countDown();
        }
    }


}

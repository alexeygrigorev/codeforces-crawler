package codeforcescrawl;

import org.openqa.selenium.WebDriver;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeExtractor.class);

    public static void main(String[] args) throws Exception {
        Env.printEnv();

        System.setProperty("webdriver.gecko.driver", "bin/geckodriver");
        Database db = Factory.createDatabase();

        RedissonClient redisson = Factory.createRedissonClient();
        RQueue<String> tasks = redisson.getQueue("tasks");

        LOGGER.debug("there are {} tasks in the queue", tasks.size());

        while (!tasks.isEmpty()) {
            String url = tasks.poll();

            WebDriver driver = Factory.createFirefoxDriver();
            CodeforcesScraper scraper = new CodeforcesScraper(driver, db);

            try {
                scraper.scrapeTask(url);
            } catch (Exception e) {
                LOGGER.warn("got an exception:", e);
                e.printStackTrace();
                LOGGER.info("resubmitting the task {} to be scraped later", url);
                tasks.offer(url);
            }

            // for long running jobs it's better to restart the browser
            // to avoid memory leaks
            driver.close();
            driver.quit();
        }

        redisson.shutdown();
    }

}

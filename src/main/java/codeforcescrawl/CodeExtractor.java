package codeforcescrawl;

import org.openqa.selenium.WebDriver;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

public class CodeExtractor {

    public static void main(String[] args) throws Exception {
        Env.printEnv();

        System.setProperty("webdriver.gecko.driver", "bin/geckodriver");
        Database db = Factory.createDatabase();
        WebDriver driver = Factory.createFirefoxDriver();
        CodeforcesScraper scraper = new CodeforcesScraper(driver, db);

        RedissonClient redisson = Factory.createRedissonClient();
        RQueue<String> tasks = redisson.getQueue("tasks");

        while (!tasks.isEmpty()) {
            try {
                String url = tasks.poll();
                scraper.scrapeTask(url);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        redisson.shutdown();
    }

}

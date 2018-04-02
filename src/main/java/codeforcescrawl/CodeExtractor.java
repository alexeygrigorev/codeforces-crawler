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
        WebDriver driver = Factory.createFirefoxDriver();
        CodeforcesScraper scraper = new CodeforcesScraper(driver, db);

        RedissonClient redisson = Factory.createRedissonClient();
        RQueue<String> tasks = redisson.getQueue("tasks");

        LOGGER.debug("there are {} tasks in the queue", tasks.size());

//        while (!tasks.isEmpty()) {
//            try {
//                String url = tasks.poll();
        String url = "http://codeforces.com/problemset/status/954/problem/F";
                scraper.scrapeTask(url);
//            } catch (Exception e) {
//                e.printStackTrace();
//                continue;
//            }
//        }

        redisson.shutdown();
    }

}

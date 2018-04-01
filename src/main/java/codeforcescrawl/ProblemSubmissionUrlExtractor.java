package codeforcescrawl;

import org.openqa.selenium.WebDriver;

public class ProblemSubmissionUrlExtractor {

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.gecko.driver", "bin/geckodriver");

        Database db = Factory.createDatabase();
        WebDriver driver = Factory.createFirefoxDriver();
        CodeforcesScraper scraper = new CodeforcesScraper(driver, db);

        scraper.populateTaskQueue();
    }

}

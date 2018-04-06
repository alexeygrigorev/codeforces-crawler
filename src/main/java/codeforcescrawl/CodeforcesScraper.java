package codeforcescrawl;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CodeforcesScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeforcesScraper.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Database db;

    public CodeforcesScraper(WebDriver driver, Database db) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 5);
        this.db = db;
    }

    public void populateTaskQueue() throws Exception {
        for (int page = 1; page <= 41; page++) {
            String problemsUrl = "http://codeforces.com/problemset/page/" + page;
            driver.get(problemsUrl);
            Thread.sleep(2000);

            List<WebElement> elements = driver.findElements(By.cssSelector("a[title='Participants solved the problem']"));

            for (WebElement a : elements) {
                String taskUrl = a.getAttribute("href");
                db.addTask(taskUrl);
                System.out.println(taskUrl);
            }
        }
    }

    public void scrapeTask(String url) throws Exception {
        LOGGER.info("processing task {}", url);

        String startUrl = url + "/page/1?order=BY_ARRIVED_DESC";
        driver.get(startUrl);
        Thread.sleep(2000);

        WebElement form = driver.findElement(By.cssSelector("form.status-filter"));
        Select select = new Select(form.findElement(By.cssSelector("#verdictName")));
        select.selectByValue("anyVerdict");
        form.submit();
        Thread.sleep(2000);

        int max = -1;

        List<WebElement> pageIndexSpans = driver.findElements(By.className("page-index"));
        for (WebElement span : pageIndexSpans) {
            max = Math.max(max, Integer.parseInt(span.getAttribute("pageindex")));
        }

        LOGGER.debug("task {} has {} pages", url, max);

        for (int page = 1; page <= max; page++) {
            String pageUrl = url + "/page/" + page + "?order=BY_ARRIVED_DESC";
            if (db.urlAlreadyProcessed(pageUrl)) {
                LOGGER.debug("already processed page {}", pageUrl);
                continue;
            }

            LOGGER.debug("scraping all submissions from {}", pageUrl);

            try {
                scrapeUrl(pageUrl);
                LOGGER.debug("page {} is scraped", pageUrl);
                db.markUrlAsProcessed(pageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        LOGGER.debug("scraping all submissions from {}", pageUrl);
        db.markTaskScraped(url);
    }

    private void scrapeUrl(String url) throws Exception {
        driver.get(url);
        Thread.sleep(2000);

        List<WebElement> rows = driver.findElements(By.cssSelector("table.status-frame-datatable tr"));
        for (WebElement row : rows) {
            if ("first-row".equals(row.getAttribute("class"))) {
                continue;
            }

            try {
                Submission submission = scrapeRow(row);
                if (submission != null) {
                    db.save(submission);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }


    private Submission scrapeRow(WebElement row) throws Exception {
        WebElement link = row.findElement(By.cssSelector("a.view-source"));
        int submissionId = Integer.parseInt(link.getText());
        if (db.alreadyScraped(submissionId)) {
            LOGGER.debug("submission {} is already scraped, skipping it", submissionId);
            return null;
        }

        return scrapeSubmission(submissionId, row, link);
    }

    private Submission scrapeSubmission(int submissionId, WebElement row, WebElement link)
            throws Exception {
        LOGGER.debug("scraping submission {}...", submissionId);

        WebElement languageTd = row.findElement(By.xpath("td[5]"));
        String language = languageTd.getText();

        WebElement statusTd = row.findElement(By.xpath("td[6]"));
        String status = statusTd.getText();

        WebElement problemTd = row.findElement(By.xpath("td[4]"));
        String problem = problemTd.getText();

        scrollAndClick(link);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#facebox .close")));
        WebElement sourceElem = driver.findElement(By.xpath("//*[@id=\"facebox\"]/div/div/div/pre"));
        String source = sourceElem.getText();

        Submission result = new Submission(submissionId, language, status, problem, source);

        driver.findElement(By.cssSelector("#facebox .close")).click();
        Thread.sleep(100);

        if (source.length() >= 20000) {
            LOGGER.info("submission {} is too large, skipping it", submissionId);
            return null;
        }

        return result;
    }

    private void scrollAndClick(WebElement link) throws Exception {
        Point p = link.getLocation();
        ((JavascriptExecutor) driver)
                .executeScript("window.scroll(" + p.getX() + "," + (p.getY() + 200) + ");");
        Thread.sleep(200);
        link.click();
    }

}

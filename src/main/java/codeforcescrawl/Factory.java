package codeforcescrawl;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

public class Factory {
    public static Database createDatasource() {
        Map<String, String> env = System.getenv();
        System.out.println("environment variables: " + env);

        String host = env.getOrDefault("MYSQL_HOST", "172.17.0.2");
        String database = env.getOrDefault("MYSQL_DATABASE", "code");
        String user = env.getOrDefault("MYSQL_USER", "code");
        String password = env.getOrDefault("MYSQL_PASSWORD", "heynottoorough");

        int port = 3306;

        MysqlDataSource datasource = new MysqlDataSource();
        datasource.setServerName(host);
        datasource.setDatabaseName(database);
        datasource.setUser(user);
        datasource.setPassword(password);
        datasource.setPort(port);

        return new Database(datasource);
    }

    public static WebDriver createFirefoxDriver() {
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        return new FirefoxDriver(firefoxOptions);
    }

}

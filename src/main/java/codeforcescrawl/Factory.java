package codeforcescrawl;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Map;

public class Factory {
    public static Database createDatabase() {
        String host = Env.getString("MYSQL_HOST", "172.17.0.2");
        String database = Env.getString("MYSQL_DATABASE", "code");
        String user = Env.getString("MYSQL_USER", "code");
        String password = Env.getString("MYSQL_PASSWORD", "heynottoorough");

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

    public static RedissonClient createRedissonClient() {
        String redisHost = Env.getString("REDIS_HOST", "172.17.0.3");
        int redisPort = Env.getInt("REDIS_PORT", 6379);
        String url = "redis://" + redisHost + ":" + redisPort;

        Config config = new Config();
        config.useSingleServer().setAddress(url);

        return Redisson.create(config);
    }
}

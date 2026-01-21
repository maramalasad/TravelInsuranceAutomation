package Base;

import ActionDriver.Action;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Base class for all test cases
 * Handles WebDriver initialization and configuration loading
 */
public abstract class BaseClass {
    public static Properties prop;
    public static WebDriver driver;
    public static Action action;

    /**
     * Load configuration from properties file
     */
    @BeforeSuite
    public void loadConfig() {
        try {
            prop = new Properties();
            boolean loaded = false;
            try {
                FileInputStream ip = new FileInputStream(
                        System.getProperty("user.dir") + "\\Configuration\\config.properties");
                prop.load(ip);
                loaded = true;
            } catch (FileNotFoundException e) {
                // Fallback to classpath resource
                InputStream cp = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
                if (cp != null) {
                    prop.load(cp);
                    loaded = true;
                } else {
                    System.err.println("✗ Config file not found: " + System.getProperty("user.dir") + "\\Configuration\\config.properties");
                }
            }
            if (!loaded) {
                // minimal safe defaults
                prop.setProperty("browser", "Chrome");
                prop.setProperty("url", "about:blank");
            }
            System.out.println("✓ Configuration loaded");
        } catch (IOException e) {
            System.err.println("✗ Error loading config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Launch the application in browser
     */
    @BeforeMethod
    public void launchApp() {
        action = new Action();
        WebDriverManager.chromedriver().setup();
        String browserName = prop != null ? prop.getProperty("browser") : null;
        if (browserName == null) {
            browserName = "Chrome";
        }

        String bn = browserName.toLowerCase();
        if (bn.contains("chrome")) {
            driver = new ChromeDriver();
        } else if (bn.contains("firefox") || bn.contains("fire fox") || bn.contains("ff")) {
            driver = new FirefoxDriver();
        } else if (bn.contains("ie") || bn.contains("internet explorer")) {
            driver = new InternetExplorerDriver();
        }

        action.implicitWait(driver, 10);
        action.pageLoadTimeOut(driver, 30);
        driver.manage().window().maximize();
        String url = prop != null ? prop.getProperty("url") : null;
        if (url == null || url.isEmpty()) {
            url = "about:blank";
        }
        driver.get(url);
        System.out.println("✓ Application launched: " + prop.getProperty("url"));
    }

    /**
     * Close the browser after test
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✓ Browser closed successfully");
        }
    }
}

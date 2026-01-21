package ActionDriver;

import Base.BaseClass;
import Interface.ActionInterface;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * Action driver class implementing reusable Selenium actions
 * Provides common operations for element interaction and verification
 */
public class Action extends BaseClass implements ActionInterface {

    @Override
    public void click(WebDriver driver, WebElement element) {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().build().perform();
            System.out.println("Clicked on element");
        } catch (Exception e) {
            System.err.println("Failed to click element: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean JSClick(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", element);
            System.out.println("JavaScript click performed");
            return true;
        } catch (Exception e) {
            System.err.println("JavaScript click failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean type(WebElement element, String text) {
        try {
            if (!element.isDisplayed()) {
                System.err.println("Element not displayed");
                return false;
            }
            element.clear();
            element.sendKeys(text);
            System.out.println("Typed text: " + text);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to type text: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean findElement(WebDriver driver, WebElement element) {
        try {
            element.isDisplayed();
            System.out.println("Element found");
            return true;
        } catch (Exception e) {
            System.err.println("Element not found: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isDisplayed(WebDriver driver, WebElement element) {
        try {
            boolean displayed = element.isDisplayed();
            if (displayed) {
                System.out.println("Element is displayed");
            } else {
                System.out.println("Element is not displayed");
            }
            return displayed;
        } catch (Exception e) {
            System.err.println("Element visibility check failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isSelected(WebDriver driver, WebElement element) {
        try {
            boolean selected = element.isSelected();
            if (selected) {
                System.out.println("Element is selected");
            } else {
                System.out.println("Element is not selected");
            }
            return selected;
        } catch (Exception e) {
            System.err.println("Element selection check failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isEnabled(WebDriver driver, WebElement element) {
        try {
            boolean enabled = element.isEnabled();
            if (enabled) {
                System.out.println("Element is enabled");
            } else {
                System.out.println("Element is not enabled");
            }
            return enabled;
        } catch (Exception e) {
            System.err.println("Element enabled check failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean selectByIndex(WebElement element, int index) {
        try {
            Select select = new Select(element);
            select.selectByIndex(index);
            System.out.println("Selected option by index: " + index);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to select by index: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean selectByValue(WebElement element, String value) {
        try {
            Select select = new Select(element);
            select.selectByValue(value);
            System.out.println("Selected option by value: " + value);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to select by value: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean selectByVisibleText(String visibleText, WebElement element) {
        try {
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
            System.out.println("Selected option by visible text: " + visibleText);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to select by visible text: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean selectBySendKeys(String value, WebElement element) {
        try {
            element.sendKeys(value);
            System.out.println("Selected option by sendKeys: " + value);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to select by sendKeys: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void scrollByVisibilityOfElement(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            System.out.println("Scrolled to element");
        } catch (Exception e) {
            System.err.println("Failed to scroll to element: " + e.getMessage());
        }
    }

    @Override
    public void implicitWait(WebDriver driver, int timeOut) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeOut));
            System.out.println("Implicit wait set to: " + timeOut + " seconds");
        } catch (Exception e) {
            System.err.println("Failed to set implicit wait: " + e.getMessage());
        }
    }

    @Override
    public void explicitWait(WebDriver driver, WebElement element, int timeOut) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
            wait.until(ExpectedConditions.visibilityOf(element));
            System.out.println("Explicit wait completed for element");
        } catch (Exception e) {
            System.err.println("Explicit wait failed: " + e.getMessage());
        }
    }

    @Override
    public void pageLoadTimeOut(WebDriver driver, int timeOut) {
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeOut));
            System.out.println("Page load timeout set to: " + timeOut + " seconds");
        } catch (Exception e) {
            System.err.println("Failed to set page load timeout: " + e.getMessage());
        }
    }

    @Override
    public String screenShot(WebDriver driver, String fileName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
            String path = System.getProperty("user.dir") + "/screenshots/" + fileName + ".png";
            File destFile = new File(path);
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("Screenshot saved: " + path);
            return path;
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getCurrentURL(WebDriver driver) {
        try {
            String url = driver.getCurrentUrl();
            System.out.println("Current URL: " + url);
            return url;
        } catch (Exception e) {
            System.err.println("Failed to get current URL: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getTitle(WebDriver driver) {
        try {
            String title = driver.getTitle();
            System.out.println("Page title: " + title);
            return title;
        } catch (Exception e) {
            System.err.println("Failed to get page title: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}

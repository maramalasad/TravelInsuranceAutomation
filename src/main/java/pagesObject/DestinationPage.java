package pagesObject;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Travel Policy Destination step
 */
public class DestinationPage {
    private final WebDriver driver;
    private static final String EXPECTED_PATH = "/travel-policy/wizard/destination";
    private static final String NEXT_PATH = "/travel-policy/wizard/date";

    private final By usaTile = By.xpath("//div[contains(text(),'ארה')]");
    private final By canadaTile = By.xpath("//div[contains(text(),'קנדה')]");
    private final By africaTile = By.xpath("//div[contains(text(),'אפריקה')]");
    private final By asiaTile = By.xpath("//div[contains(text(),'אסיה')]");
    private final By southAmericaTile = By.xpath("//div[contains(text(),'דרום')]");
    private final By europeTile = By.xpath("//div[contains(text(),'אירופה')]");
    private final By australiaTile = By.xpath("//div[contains(text(),'אוסטרליה')]");
    private final By antarcticaTile = By.xpath("//div[contains(text(),'אנטארקט')]");
    private final By nextButton = By.xpath("//button[@data-hrl-bo='wizard-next-button']");
    private final By antarcticaWarningYesBtn = By.xpath("//button[@data-hrl-bo='warningPopup-yes-button']");

    public DestinationPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isAt() {
        String url = driver.getCurrentUrl();
        return url != null && url.contains(EXPECTED_PATH);
    }

    private boolean click(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(el));
            new Actions(driver).moveToElement(el).click().build().perform();
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception ex) {
                return false;
            }
        }
        // After clicking region, wait until Next button becomes enabled (selection registered)
        boolean nextEnabled = wait.until(d -> isNextEnabled());
        System.out.println("Next button enabled after region click: " + nextEnabled);
        return nextEnabled;
    }

    private boolean isNextEnabled() {
        try {
            WebElement b = driver.findElement(nextButton);
            String cls = b.getAttribute("class");
            String disabledAttr = b.getAttribute("disabled");
            String ariaDisabled = b.getAttribute("aria-disabled");
            boolean disabledByClass = cls != null && cls.toLowerCase().contains("disabled");
            boolean disabled = (disabledAttr != null) || (ariaDisabled != null && ariaDisabled.equalsIgnoreCase("true"));
            return b.isDisplayed() && b.isEnabled() && !disabled && !disabledByClass;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickUSA() { return click(usaTile); }
    public boolean clickCanada() { return click(canadaTile); }
    public boolean clickAfrica() { return click(africaTile); }
    public boolean clickAsia() { return click(asiaTile); }
    public boolean clickSouthAmerica() { return click(southAmericaTile); }
    public boolean clickEurope() { return click(europeTile); }
    public boolean clickAustralia() { return click(australiaTile); }
    public boolean clickAntarctica() { return click(antarcticaTile); }

    public boolean acknowledgeAntarcticaWarning() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            WebElement yes = wait.until(ExpectedConditions.visibilityOfElementLocated(antarcticaWarningYesBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", yes);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(yes));
                new Actions(driver).moveToElement(yes).click().build().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yes);
            }
            return wait.until(ExpectedConditions.urlContains(EXPECTED_PATH));
        } catch (Exception e) {
            return false;
        }
    }


    public boolean clickNextAndWaitForDate() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(nextButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        // Ensure the Next button is enabled
        wait.until(d -> isNextEnabled());
        // Try up to 2 attempts in case the first click occurs during animation
        for (int i = 0; i < 2; i++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(btn));
                new Actions(driver).moveToElement(btn).click().build().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
            try {
                return new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.urlContains(NEXT_PATH));
            } catch (Exception retry) {
                System.out.println("URL did not change after click attempt " + (i+1) + ", retrying...");
            }
        }
        // Final wait for URL change with overall timeout
        return wait.until(ExpectedConditions.urlContains(NEXT_PATH));
    }
}

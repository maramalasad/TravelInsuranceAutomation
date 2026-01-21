package pagesObject;

import ActionDriver.Action;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Travel Policy landing page
 */
public class TravelPolicyPage {
    private final WebDriver driver;
    private final Action action;

    private static final String PAGE_URL = "https://digital.harel-group.co.il/travel-policy";

    private final By purchaseForNewCustomerBtn = By.xpath("//button[@data-hrl-bo='purchase-for-new-customer']");

    public TravelPolicyPage(WebDriver driver) {
        this.driver = driver;
        this.action = new Action();
    }

    public TravelPolicyPage open() {
        driver.get(PAGE_URL);
        return this;
    }

    public DestinationPage clickPurchaseForNewCustomer() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(purchaseForNewCustomerBtn));
        WebElement button = driver.findElement(purchaseForNewCustomerBtn);
        action.click(driver, button);
        // Optionally wait for navigation to destination path
        wait.until(ExpectedConditions.urlContains("/travel-policy/wizard/destination"));
        return new DestinationPage(driver);
    }
}

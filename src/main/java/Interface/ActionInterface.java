package Interface;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public interface ActionInterface {
    
    // Click operations
    void click(WebDriver driver, WebElement element);
    boolean JSClick(WebDriver driver, WebElement element);
    
    // Text input operations
    boolean type(WebElement element, String text);
    
    // Element visibility and state checks
    boolean findElement(WebDriver driver, WebElement element);
    boolean isDisplayed(WebDriver driver, WebElement element);
    boolean isSelected(WebDriver driver, WebElement element);
    boolean isEnabled(WebDriver driver, WebElement element);
    
    // Dropdown operations
    boolean selectByIndex(WebElement element, int index);
    boolean selectByValue(WebElement element, String value);
    boolean selectByVisibleText(String visibleText, WebElement element);
    boolean selectBySendKeys(String value, WebElement element);
    
    // Scroll operations
    void scrollByVisibilityOfElement(WebDriver driver, WebElement element);
    
    // Wait operations
    void implicitWait(WebDriver driver, int timeOut);
    void explicitWait(WebDriver driver, WebElement element, int timeOut);
    void pageLoadTimeOut(WebDriver driver, int timeOut);
    
    // Screenshot operations
    String screenShot(WebDriver driver, String fileName);
    
    // Utility operations
    String getCurrentURL(WebDriver driver);
    String getTitle(WebDriver driver);
    String getCurrentTime();
}

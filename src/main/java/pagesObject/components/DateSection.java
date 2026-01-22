package pagesObject.components;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DateUtils;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Component representing the Date section in the travel policy wizard.
 * Encapsulates interactions with the start/end inputs, calendar day buttons,
 * trip days summary, and advancing to the next component.
 */
public class DateSection {
    private final WebDriver driver;

    // Timeouts and scroll offsets
    private static final int WAIT_SHORT_SEC = 10;
    private static final int WAIT_MEDIUM_SEC = 30;
    private static final int SCROLL_NUDGE_PX = 150;

    // Locators
    private final By startDateInput = By.xpath("//input[@id='travel_start_date' or @name='start']");
    private final By endDateInput = By.xpath("//input[@name='end' or @id='travel_end_date']");
    private final By nextButtonById = By.id("nextButton");
    private final By daysSummaryCandidates = By.xpath("//*[contains(normalize-space(text()), 'ימים')]");
    private final By screenTitle = By.xpath("//h2[@data-hrl-bo='screen_title']");
    private final By nextMonthArrow = By.xpath("(//button[@data-hrl-bo='arrow-forward' and @aria-label='לעבור לחודש הבא'])[2]");
    private final By generalError = By.xpath("//div[@data-hrl-bo='general_error']");

    public DateSection(WebDriver driver) {
        this.driver = driver;
    }

    public String getStartValue() {
        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                .until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
        return el.getAttribute("value");
    }

    public String getEndValue() {
        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                .until(ExpectedConditions.visibilityOfElementLocated(endDateInput));
        return el.getAttribute("value");
    }

    public boolean selectDatesAbsolute(String startStr, String endStr) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_MEDIUM_SEC));
        WebElement startInputEl = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
        scrollIntoView(startInputEl);
        setInputValue(startInputEl, startStr);

        WebElement endInputEl = wait.until(ExpectedConditions.visibilityOfElementLocated(endDateInput));
        scrollIntoView(endInputEl);
        setInputValue(endInputEl, endStr);

        return startStr.equals(startInputEl.getAttribute("value")) && endStr.equals(endInputEl.getAttribute("value"));
    }

    public boolean selectDatesRelative(int startOffsetDays, int tripLengthDays) {
        LocalDate start = DateUtils.todayPlusDays(Math.max(0, startOffsetDays));
        LocalDate end = start.plusDays(Math.max(1, tripLengthDays));
        return selectDatesAbsolute(DateUtils.formatUi(start), DateUtils.formatUi(end));
    }

    public boolean selectDatesViaPickerRelative(int startOffsetDays, int tripLengthDays) {
        LocalDate start = DateUtils.todayPlusDays(Math.max(0, startOffsetDays));
        LocalDate end = start.plusDays(Math.max(1, tripLengthDays));
        String startIso = start.toString();
        String endIso = end.toString();
        // Ensure calendar is open
        try {
            WebElement startInputEl = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
            startInputEl.click();
        } catch (Exception ignored) {}
        boolean s = clickCalendarDayIso(startIso);
        boolean e = clickCalendarDayIso(endIso);
        String sv = getStartValue();
        String ev = getEndValue();
        return s && e && DateUtils.formatUi(start).equals(sv) && DateUtils.formatUi(end).equals(ev);
    }

    public boolean clickCalendarDayIso(String isoDate) {
        try {
            By day = By.xpath("//button[@data-hrl-bo='" + isoDate + "']");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_MEDIUM_SEC));
            for (int i = 0; i < 6; i++) {
                try {
                    WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(day));
                    scrollIntoView(el);
                    click(el);
                    return true;
                } catch (Exception notVisible) {
                    // next month
                    try {
                        WebElement arrow = wait.until(ExpectedConditions.elementToBeClickable(nextMonthArrow));
                        scrollIntoView(arrow);
                        arrow.click();
                        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
                    } catch (Exception arrowFail) {
                        break;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public int getTripDaysFromUI() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            scrollIntoView(btn);
            String text = btn.getText();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(text);
            if (m.find()) return Integer.parseInt(m.group(1));
            // fallback: container and summary nodes
            try {
                WebElement container = btn.findElement(By.xpath("ancestor::*[self::section or self::div][1]"));
                String cText = container.getText();
                java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("(\\d+)").matcher(cText);
                if (m2.find()) return Integer.parseInt(m2.group(1));
                String inner = container.getAttribute("innerText");
                if (inner != null) {
                    java.util.regex.Matcher m3 = java.util.regex.Pattern.compile("(\\d+)").matcher(inner);
                    if (m3.find()) return Integer.parseInt(m3.group(1));
                }
                String txt = container.getAttribute("textContent");
                if (txt != null) {
                    java.util.regex.Matcher m4 = java.util.regex.Pattern.compile("(\\d+)").matcher(txt);
                    if (m4.find()) return Integer.parseInt(m4.group(1));
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        // scan summary labels
        java.util.List<WebElement> nodes = driver.findElements(daysSummaryCandidates);
        int best = -1;
        if (nodes != null) {
            for (WebElement n : nodes) {
                try {
                    scrollIntoView(n);
                    String t = n.getText();
                    if (t == null || t.trim().isEmpty()) continue;
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(t);
                    if (m.find()) best = Math.max(best, Integer.parseInt(m.group(1)));
                    if (best < 0) {
                        String inner = n.getAttribute("innerText");
                        if (inner != null) {
                            java.util.regex.Matcher mi = java.util.regex.Pattern.compile("(\\d+)").matcher(inner);
                            if (mi.find()) best = Math.max(best, Integer.parseInt(mi.group(1)));
                        }
                        String txt = n.getAttribute("textContent");
                        if (txt != null) {
                            java.util.regex.Matcher mt = java.util.regex.Pattern.compile("(\\d+)").matcher(txt);
                            if (mt.find()) best = Math.max(best, Integer.parseInt(mt.group(1)));
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return best;
    }

    public int waitForTripDaysOnNext(int timeoutSeconds) {
        long end = System.currentTimeMillis() + Math.max(1, timeoutSeconds) * 1000L;
        int val = -1;
        while (System.currentTimeMillis() < end) {
            try {
                // nudge viewport
                try {
                    WebElement btn = driver.findElement(nextButtonById);
                    scrollIntoView(btn);
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + SCROLL_NUDGE_PX + ");");
                } catch (Exception ignored) {}
                val = getTripDaysFromUI();
                if (val >= 0) return val;
            } catch (Exception ignored) {}
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        return val;
    }

    public boolean verifyTripDaysOnNextMatchesInputs(int toleranceDays) {
        try {
            LocalDate start = DateUtils.parseUi(getStartValue());
            LocalDate end = DateUtils.parseUi(getEndValue());
            long inclusive = DateUtils.inclusiveDays(start, end);
            int shown = getTripDaysFromUI();
            return shown >= 0 && Math.abs(shown - inclusive) <= Math.max(0, toleranceDays);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickNext() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            scrollIntoView(btn);
            click(btn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickNextAndWaitForScreenTitle() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            scrollIntoView(btn);
            click(btn);
            WebElement title = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(screenTitle));
            return title != null && title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickNextAndWaitForGeneralError(String expectedContains) {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            scrollIntoView(btn);
            click(btn);
            WebElement err = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(generalError));
            if (err == null || !err.isDisplayed()) return false;
            if (expectedContains == null || expectedContains.isEmpty()) return true;
            String txt = err.getText();
            if (txt == null || txt.trim().isEmpty()) {
                txt = err.getAttribute("innerText");
                if (txt == null || txt.trim().isEmpty()) txt = err.getAttribute("textContent");
            }
            return txt != null && txt.contains(expectedContains);
        } catch (Exception e) {
            return false;
        }
    }

    // Helpers
    private void setInputValue(WebElement input, String value) {
        try {
            input.click();
            input.clear();
            input.sendKeys(value);
            input.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try { js.executeScript("arguments[0].removeAttribute('readonly');", input); } catch (Exception e) {}
            js.executeScript(
                    "arguments[0].value=arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));" +
                            "arguments[0].dispatchEvent(new Event('blur',{bubbles:true}));",
                    input, value
            );
        }
    }

    private void scrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
        } catch (Exception ignored) {}
    }

    private void click(WebElement el) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(el));
            new Actions(driver).moveToElement(el).click().build().perform();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}

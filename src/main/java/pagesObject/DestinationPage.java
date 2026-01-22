package pagesObject;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.Keys;
import utils.DateUtils;
import pagesObject.components.DateSection;

/**
 * Page Object for the Travel Policy Destination step
 */
public class DestinationPage {
    private final WebDriver driver;
    private static final String EXPECTED_PATH = "/travel-policy/wizard/destination";
    private static final String NEXT_PATH = "/travel-policy/wizard/date";

    // Timeouts and scroll offsets (avoid magic numbers)
    private static final int WAIT_SHORT_SEC = 10;
    private static final int WAIT_MEDIUM_SEC = 30;
    private static final int WAIT_LONG_SEC = 60;
    private static final int SCROLL_NUDGE_PX = 150;

    private final By usaTile = By.xpath("//div[contains(text(),'ארה')]");
    private final By canadaTile = By.xpath("//div[contains(text(),'קנדה')]");
    private final By africaTile = By.xpath("//div[contains(text(),'אפריקה')]");
    private final By asiaTile = By.xpath("//div[contains(text(),'אסיה')]");
    private final By southAmericaTile = By.xpath("//div[contains(text(),'דרום')]");
    private final By europeTile = By.xpath("//div[contains(text(),'אירופה')]");
    private final By australiaTile = By.xpath("//div[contains(text(),'אוסטרליה')]");
    private final By antarcticaTile = By.xpath("//div[contains(text(),'אנטארקט')]");
    private final By nextButton = By.xpath("//button[@data-hrl-bo='wizard-next-button']");
    private final By nextButtonById = By.id("nextButton");
    private final By antarcticaWarningYesBtn = By.xpath("//button[@data-hrl-bo='warningPopup-yes-button']");
    private final By screenTitle = By.xpath("//h2[@data-hrl-bo='screen_title']");
    private final By destinationGeneralError = By.xpath("//div[@data-hrl-bo='general_error']");
    // Date inputs (use union to support alternative attributes on the site)
    private final By startDateInput = By.xpath("//input[@id='travel_start_date' or @name='start']");
    private final By endDateInput = By.xpath("//input[@name='end' or @id='travel_end_date']");
    // Calendar selected day badges (site-specific classes may change). Try multiple strategies.
    private final By selectedDayBadges = By.xpath("//span[contains(@class,'jss219') and normalize-space(text())!='']");
    private final By muiSelectedBadges = By.xpath("//*[contains(@class,'Mui-selected') and normalize-space(text())!='']");
    private final By ariaSelectedBadges = By.cssSelector("[aria-selected='true']");
    private final By genericSelectedBadges = By.xpath("//*[contains(@class,'selected') and normalize-space(text())!='']");
    // Summary text showing total days (Hebrew: "סה\"כ: NN ימים"). We'll search broadly for elements containing "ימים"
    private final By daysSummaryCandidates = By.xpath("//*[contains(normalize-space(text()), 'ימים')]");
    // Date picker next-month arrow (per user-provided locator)
    private final By nextMonthArrow = By.xpath("(//button[@data-hrl-bo='arrow-forward' and @aria-label='לעבור לחודש הבא'])[2]");

    public DestinationPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isAt() {
        String url = driver.getCurrentUrl();
        return url != null && url.contains(EXPECTED_PATH);
    }

    /** Exposes the Date section component for date interactions on the Date step. */
    public DateSection getDateSection() {
        return new DateSection(driver);
    }

    private boolean click(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_MEDIUM_SEC));
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

    /** Generic region click to reduce duplication (clicks tile that contains given text). */
    public boolean clickRegionByPartialText(String partialTextHebrew) {
        By dynamic = By.xpath("//div[contains(text(), '" + partialTextHebrew + "')]");
        return click(dynamic);
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

    /**
     * On the Destination step, attempt to click Next without selecting a region
     * and return the general error text if displayed.
     */
    public String clickNextWithoutRegionAndGetError() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(nextButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(btn));
                new Actions(driver).moveToElement(btn).click().build().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
            WebElement error = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(destinationGeneralError));
            return error.getText();
        } catch (Exception e) {
            return null;
        }
    }

    /** Reads the total days displayed on the Next button (id="nextButton"). */
    public int getTripDaysFromNextButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
        String text = btn.getText();
        // Extract first integer found
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(text);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignored) {}
        }
        return -1;
    }

    /**
     * Reads the total days from either the Next button or the nearby summary text (e.g., "סה"+"כ: 33 ימים").
     */
    public int getTripDaysFromUI() {
        int fromButton = -1;
        try {
            // Ensure the area is visible
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                    .until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); window.scrollBy(0, " + SCROLL_NUDGE_PX + ");", btn);
            fromButton = getTripDaysFromNextButton();
            if (fromButton >= 0) return fromButton;
            // Try reading container text around the button
            try {
                WebElement container = btn.findElement(By.xpath("ancestor::*[self::section or self::div][1]"));
                String cText = container.getText();
                if (cText != null && !cText.trim().isEmpty()) {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(cText);
                    if (m.find()) {
                        return Integer.parseInt(m.group(1));
                    }
                }
                // innerText/textContent fallbacks
                String inner = container.getAttribute("innerText");
                if (inner != null) {
                    java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("(\\d+)").matcher(inner);
                    if (m2.find()) return Integer.parseInt(m2.group(1));
                }
                String txt = container.getAttribute("textContent");
                if (txt != null) {
                    java.util.regex.Matcher m3 = java.util.regex.Pattern.compile("(\\d+)").matcher(txt);
                    if (m3.find()) return Integer.parseInt(m3.group(1));
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        if (fromButton >= 0) return fromButton;
        // Try summary labels that contain the word "ימים"
        java.util.List<WebElement> nodes = driver.findElements(daysSummaryCandidates);
        int best = -1;
        if (nodes != null) {
            for (WebElement n : nodes) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", n);
                    String t = n.getText();
                    if (t == null || t.trim().isEmpty()) continue;
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(t);
                    if (m.find()) {
                        int val = Integer.parseInt(m.group(1));
                        if (val > best) best = val;
                    }
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
                } catch (Exception ignored) { }
            }
        }
        return best;
    }

    /** Waits until the Next button shows a numeric days count, and returns it. */
    public int waitForTripDaysOnNext(int timeoutSeconds) {
        long end = System.currentTimeMillis() + Math.max(1, timeoutSeconds) * 1000L;
        int val = -1;
        while (System.currentTimeMillis() < end) {
            try {
                // Nudge the viewport near the button each iteration
                try {
                    WebElement btn = driver.findElement(nextButtonById);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); window.scrollBy(0, " + SCROLL_NUDGE_PX + ");", btn);
                } catch (Exception ignored) {}
                val = getTripDaysFromUI();
                if (val >= 0) return val;
            } catch (Exception ignored) { }
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        return val;
    }

    /**
     * Verifies that the calendar's selected day badges include the day numbers
     * that match the values currently set in the start and end inputs.
     * Returns true only if both days are present among the badges.
     */
    public boolean verifyCalendarDaysMatchInputs() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        WebElement startInput = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
        WebElement endInput = wait.until(ExpectedConditions.visibilityOfElementLocated(endDateInput));

        String startVal = startInput.getAttribute("value");
        String endVal = endInput.getAttribute("value");
        if (startVal == null || endVal == null || startVal.isEmpty() || endVal.isEmpty()) {
            return false;
        }
        int startDay, endDay;
        try {
            startDay = LocalDate.parse(startVal, fmt).getDayOfMonth();
            endDay = LocalDate.parse(endVal, fmt).getDayOfMonth();
        } catch (Exception e) {
            return false;
        }

        // Ensure calendar is visible by focusing and scrolling to the inputs (some UIs render selections only while open)
        try { startInput.click(); } catch (Exception ignored) {}
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); window.scrollBy(0, 200);", startInput);
        } catch (Exception ignored) {}
        // Try to gather using multiple strategies, in order
        java.util.List<WebElement> badges = driver.findElements(selectedDayBadges);
        if (badges == null || badges.isEmpty()) badges = driver.findElements(muiSelectedBadges);
        if (badges == null || badges.isEmpty()) badges = driver.findElements(ariaSelectedBadges);
        if (badges == null || badges.isEmpty()) badges = driver.findElements(genericSelectedBadges);
        if (badges == null || badges.isEmpty()) {
            try { endInput.click(); } catch (Exception ignored) {}
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); window.scrollBy(0, 200);", endInput);
            } catch (Exception ignored) {}
            badges = driver.findElements(selectedDayBadges);
            if (badges == null || badges.isEmpty()) badges = driver.findElements(muiSelectedBadges);
            if (badges == null || badges.isEmpty()) badges = driver.findElements(ariaSelectedBadges);
            if (badges == null || badges.isEmpty()) badges = driver.findElements(genericSelectedBadges);
        }
        if (badges == null || badges.isEmpty()) {
            System.out.println("No calendar badges found with available locators (jss219/Mui-selected/aria-selected/generic)");
            return false;
        }
        java.util.Set<String> texts = new java.util.HashSet<>();
        java.util.Set<String> numericTexts = new java.util.HashSet<>();
        for (WebElement b : badges) {
            try {
                String t = b.getText();
                if (t != null) {
                    t = t.trim();
                    if (!t.isEmpty()) {
                        texts.add(t);
                        if (t.matches("\\d{1,2}")) {
                            numericTexts.add(t);
                        }
                    }
                }
            } catch (Exception ignored) { }
        }

        boolean hasStart = numericTexts.contains(Integer.toString(startDay));
        boolean hasEnd = numericTexts.contains(Integer.toString(endDay));
        System.out.println("Calendar badges (all): " + texts + "; numeric-only: " + numericTexts + ", expected start=" + startDay + ", end=" + endDay);
        return hasStart && hasEnd;
    }

    /**
     * Compares the days count written on the Next button (e.g., "סה"+"כ: 33 ימים")
     * with the difference between start and end input dates. Some sites count inclusively
     * or add buffer days; provide a tolerance to allow small differences.
     */
    public boolean verifyTripDaysOnNextMatchesInputs(int toleranceDays) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
        try {
            WebElement startInput = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
            WebElement endInput = wait.until(ExpectedConditions.visibilityOfElementLocated(endDateInput));
            String startVal = startInput.getAttribute("value");
            String endVal = endInput.getAttribute("value");
            LocalDate start = DateUtils.parseUi(startVal);
            LocalDate end = DateUtils.parseUi(endVal);
            long inclusive = DateUtils.inclusiveDays(start, end); // Site counts both start and end days
            int shown = getTripDaysFromUI();
            System.out.println("Computed inclusive days=" + inclusive + ", UI shows=" + shown);
            return shown >= 0 && Math.abs(shown - inclusive) <= Math.max(0, toleranceDays);
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_LONG_SEC));
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
                return new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC))
                        .until(ExpectedConditions.urlContains(NEXT_PATH));
            } catch (Exception retry) {
                System.out.println("URL did not change after click attempt " + (i+1) + ", retrying...");
            }
        }
        // Final wait for URL change with overall timeout
        return wait.until(ExpectedConditions.urlContains(NEXT_PATH));
    }

    public boolean clickNextOnDateAndWaitForScreenTitle() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); window.scrollBy(0, 150);", btn);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(btn));
                new Actions(driver).moveToElement(btn).click().build().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
            WebDriverWait titleWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement title = titleWait.until(ExpectedConditions.visibilityOfElementLocated(screenTitle));
            return title != null && title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns whether the Next button on the current screen appears enabled (generic check by id). */
    public boolean isNextEnabledOnCurrentStep() {
        try {
            WebElement b = driver.findElement(nextButtonById);
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

    /** Clicks a specific calendar day by ISO date (yyyy-MM-dd) using data-hrl-bo attribute. */
    public boolean clickCalendarDayIso(String isoDate) {
        try {
            By day = By.xpath("//button[@data-hrl-bo='" + isoDate + "']");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_MEDIUM_SEC));
            // Try up to 6 months ahead by clicking next-month arrow until the day becomes visible
            for (int i = 0; i < 6; i++) {
                try {
                    WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(day));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
                    try {
                        wait.until(ExpectedConditions.elementToBeClickable(el));
                        new Actions(driver).moveToElement(el).click().build().perform();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                    }
                    return true;
                } catch (Exception notVisible) {
                    // Move to next month and retry
                    try {
                        WebElement arrow = wait.until(ExpectedConditions.elementToBeClickable(nextMonthArrow));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", arrow);
                        arrow.click();
                        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
                    } catch (Exception arrowFail) {
                        // Cannot advance months; break
                        break;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Selects dates via date picker by clicking day buttons (today+offset and +length). */
    public boolean selectDatesViaPickerRelative(int startOffsetDays, int tripLengthDays) {
        LocalDate start = DateUtils.todayPlusDays(Math.max(0, startOffsetDays));
        LocalDate end = start.plusDays(Math.max(1, tripLengthDays));
        String startIso = start.toString(); // yyyy-MM-dd
        String endIso = end.toString();
        // Focus inputs to ensure calendar is open/visible
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
            WebElement startInputEl = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
            startInputEl.click();
        } catch (Exception ignored) {}
        boolean s = clickCalendarDayIso(startIso);
        boolean e = clickCalendarDayIso(endIso);
        // Verify inputs updated
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
            WebElement startInputEl = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
            WebElement endInputEl = wait.until(ExpectedConditions.visibilityOfElementLocated(endDateInput));
            String sv = startInputEl.getAttribute("value");
            String ev = endInputEl.getAttribute("value");
            String expectedS = DateUtils.formatUi(start);
            String expectedE = DateUtils.formatUi(end);
            System.out.println("Picker-selected dates -> start: " + sv + ", end: " + ev);
            return s && e && expectedS.equals(sv) && expectedE.equals(ev);
        } catch (Exception ex) {
            return false;
        }
    }

    /** Clicks Next on the Date step without waiting for the next component. */
    public boolean clickNextOnDate() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(nextButtonById));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(btn));
                new Actions(driver).moveToElement(btn).click().build().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Checks if current URL indicates we are still on the Date step. */
    public boolean isOnDateStep() {
        try {
            String url = driver.getCurrentUrl();
            return url != null && url.contains(NEXT_PATH);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Selects start and end dates using the inputs on the Date step.
     * - Start: today + offsetDays (minimum 7 days recommended)
     * - End: start + tripLengthDays (e.g., 30 days)
     * Dates are set in dd/MM/yyyy format to match the UI placeholder.
     */
    public boolean selectDatesRelative(int offsetDaysFromToday, int tripLengthDays) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_MEDIUM_SEC));
        LocalDate start = DateUtils.todayPlusDays(Math.max(0, offsetDaysFromToday));
        LocalDate end = start.plusDays(Math.max(1, tripLengthDays));

        String startStr = DateUtils.formatUi(start);
        String endStr = DateUtils.formatUi(end);

        WebElement startInput = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", startInput);
        setInputValue(startInput, startStr);

        WebElement endInput = wait.until(ExpectedConditions.visibilityOfElementLocated(endDateInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", endInput);
        setInputValue(endInput, endStr);

        // Basic validation: ensure the inputs now reflect the desired values
        String startValue = startInput.getAttribute("value");
        String endValue = endInput.getAttribute("value");
        System.out.println("Selected dates -> start: " + startValue + ", end: " + endValue);
        return startStr.equals(startValue) && endStr.equals(endValue);
    }

    /** Convenience: start in 7 days, return 30 days later. */
    public boolean selectDefaultDates() {
        return selectDatesRelative(7, 30);
    }

    /** Set specific dates in dd/MM/yyyy format. */
    public boolean selectDatesAbsolute(String startStr, String endStr) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_MEDIUM_SEC));
        WebElement startInput = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", startInput);
        setInputValue(startInput, startStr);

        WebElement endInput = wait.until(ExpectedConditions.visibilityOfElementLocated(endDateInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", endInput);
        setInputValue(endInput, endStr);

        String startValue = startInput.getAttribute("value");
        String endValue = endInput.getAttribute("value");
        System.out.println("Selected dates -> start: " + startValue + ", end: " + endValue);
        return startStr.equals(startValue) && endStr.equals(endValue);
    }

    private void setInputValue(WebElement input, String value) {
        try {
            // Try standard clear+send
            input.click();
            input.clear();
            input.sendKeys(value);
            input.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
            // Fallback: remove readonly and set via JS, then dispatch events
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try { js.executeScript("arguments[0].removeAttribute('readonly');", input); } catch (Exception e) { /* ignore */ }
            js.executeScript(
                    "arguments[0].value=arguments[1];" +
                    "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                    "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));" +
                    "arguments[0].dispatchEvent(new Event('blur',{bubbles:true}));",
                    input, value
            );
        }
    }
}

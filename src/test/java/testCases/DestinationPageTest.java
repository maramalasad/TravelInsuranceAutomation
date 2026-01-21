package testCases;

import Base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pagesObject.DestinationPage;
import pagesObject.TravelPolicyPage;

public class DestinationPageTest extends BaseClass {

    private DestinationPage openDestination() {
        TravelPolicyPage policyPage = new TravelPolicyPage(driver).open();
        DestinationPage destinationPage = policyPage.clickPurchaseForNewCustomer();
        Assert.assertTrue(destinationPage.isAt(), "Not on destination step");
        return destinationPage;
    }

    @Test
    public void selectEurope_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickEurope();
        Assert.assertTrue(clicked, "Failed to click Europe tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (Europe) -> Date URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectAsia_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickAsia();
        Assert.assertTrue(clicked, "Failed to click Asia tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (Asia) -> Date URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectAfrica_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickAfrica();
        Assert.assertTrue(clicked, "Failed to click Africa tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (Africa) -> Date URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectSouthAmerica_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickSouthAmerica();
        Assert.assertTrue(clicked, "Failed to click South America tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (South America) -> Date URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectAustralia_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickAustralia();
        Assert.assertTrue(clicked, "Failed to click Australia tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (Australia) -> Date URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectAntarctica_warningReturnsToDestination() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickAntarctica();
        Assert.assertTrue(clicked, "Failed to click Antarctica tile");
        boolean acknowledged = destinationPage.acknowledgeAntarcticaWarning();
        Assert.assertTrue(acknowledged, "Warning acknowledgement failed or URL did not return to destination");
        Assert.assertTrue(destinationPage.isAt(), "Not on destination after acknowledging Antarctica warning");
        System.out.println("After Antarctica warning URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectCanada_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickCanada();
        Assert.assertTrue(clicked, "Failed to click Canada tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (Canada) -> Date URL: " + driver.getCurrentUrl());
    }

    @Test
    public void selectUSA_andProceedToDate() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickUSA();
        Assert.assertTrue(clicked, "Failed to click USA tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        System.out.println("Destination (USA) -> Date URL: " + driver.getCurrentUrl());
    }

    //setDefaultDates

    @Test
    public void selectEurope_setDefaultDates() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickEurope();
        Assert.assertTrue(clicked, "Failed to click Europe tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        boolean datesSet = destinationPage.selectDefaultDates();
        Assert.assertTrue(datesSet, "Failed to set default dates (start in 7 days, end 30 days later)");
        System.out.println("Selected default dates on Date step: " + driver.getCurrentUrl());
    }

    @Test
    public void selectAsia_setRelativeDates() {
        DestinationPage destinationPage = openDestination();
        boolean clicked = destinationPage.clickAsia();
        Assert.assertTrue(clicked, "Failed to click Asia tile");
        boolean movedToDate = destinationPage.clickNextAndWaitForDate();
        Assert.assertTrue(movedToDate, "Did not navigate to date step");
        boolean datesSet = destinationPage.selectDatesRelative(7, 30);
        Assert.assertTrue(datesSet, "Failed to set dates: start in 7 days, return 30 days later");
        System.out.println("Selected Asia trip dates (7/30) on Date step: " + driver.getCurrentUrl());
    }
    

    @Test
    public void selectEurope_verifyAbsoluteDatesAndTripDays() {
        DestinationPage destinationPage = openDestination();
        Assert.assertTrue(destinationPage.clickEurope(), "Failed to click Europe tile");
        Assert.assertTrue(destinationPage.clickNextAndWaitForDate(), "Did not navigate to date step");

        // Set specific dates per screenshot: start 27/01/2026, end 28/02/2026 (expected total ~33 days)
        Assert.assertTrue(destinationPage.selectDatesAbsolute("27/01/2026", "28/02/2026"),
                "Failed to set absolute dates");

        // Prefer robust verification: check the Next button's days matches inclusive difference (33)
        int shownDays = destinationPage.waitForTripDaysOnNext(10);
        System.out.println("Trip days shown on Next button: " + shownDays);
        Assert.assertTrue(shownDays >= 0, "Next button did not show a days count");
        Assert.assertTrue(Math.abs(shownDays - 33) <= 0,
                "Trip days shown (" + shownDays + ") do not match expected 33");
    }

    @Test
    public void selectEurope_verifyAbsoluteDatesAndTripDays_31Days() {
        DestinationPage destinationPage = openDestination();
        Assert.assertTrue(destinationPage.clickEurope(), "Failed to click Europe tile");
        Assert.assertTrue(destinationPage.clickNextAndWaitForDate(), "Did not navigate to date step");

        // Start: 28/01/2026, End: 27/02/2026 -> inclusive days should be 31
        Assert.assertTrue(destinationPage.selectDatesAbsolute("28/01/2026", "27/02/2026"),
                "Failed to set absolute dates");

        int shownDays = destinationPage.waitForTripDaysOnNext(12);
        System.out.println("Trip days shown on Next button (28/01 - 27/02): " + shownDays);
        Assert.assertTrue(shownDays >= 0, "Next button did not show a days count");
        Assert.assertEquals(shownDays, 31, "Trip days shown does not equal expected 31");

        // Optional double-check against inputs (inclusive)
        Assert.assertTrue(destinationPage.verifyTripDaysOnNextMatchesInputs(0),
                "Inclusive trip days derived from inputs do not match UI days");
    }

    @Test
    public void selectEurope_setDates_andProceedToNextComponent() {
        DestinationPage destinationPage = openDestination();
        Assert.assertTrue(destinationPage.clickEurope(), "Failed to click Europe tile");
        Assert.assertTrue(destinationPage.clickNextAndWaitForDate(), "Did not navigate to date step");
        Assert.assertTrue(destinationPage.selectDatesAbsolute("27/01/2026", "28/02/2026"),
                "Failed to set dates");
        Assert.assertTrue(destinationPage.clickNextOnDateAndWaitForScreenTitle(),
                "Next component screen title was not visible after clicking Next");
    }
}

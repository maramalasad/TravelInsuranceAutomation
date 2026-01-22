package testCases;

import Base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pagesObject.DestinationPage;
import pagesObject.TravelPolicyPage;
import utils.DateUtils;

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
    public void destination_withoutRegion_showsGeneralErrorOnNext() {
        DestinationPage destinationPage = openDestination();
        // Do not select any region; click Next and capture error
        String error = destinationPage.clickNextWithoutRegionAndGetError();
        Assert.assertNotNull(error, "Expected a validation message when no region is selected");
        // Expected message: ".שכחת לבחור יעד מרוב התרגשות? נא לסמן אחד"
        Assert.assertTrue(error.contains("שכחת לבחור יעד") && error.contains("נא לסמן אחד"),
                "Unexpected error text when no region selected. Actual: " + error);
        System.out.println("Validation (no region): " + error);
    }

    

    @Test
    public void datePicker_selectStartPlus7_andEndPlus30_thenProceed() {
        DestinationPage destinationPage = openDestination();
        Assert.assertTrue(destinationPage.clickEurope(), "Failed to click Europe tile");
        Assert.assertTrue(destinationPage.clickNextAndWaitForDate(), "Did not navigate to date step");

        // Select via date picker (today+7, then +30)
        Assert.assertTrue(destinationPage.selectDatesViaPickerRelative(7, 30),
                "Failed to select dates via date picker (7/30)");

        // Verify inclusive days (30 + 1 = 31) and proceed
        int shownDays = destinationPage.waitForTripDaysOnNext(12);
        Assert.assertTrue(shownDays >= 0, "Next button did not show a days count");
        Assert.assertEquals(shownDays, 31, "Expected 31 inclusive days for 7/30 selection");
        Assert.assertTrue(destinationPage.clickNextOnDateAndWaitForScreenTitle(),
                "Next component screen title was not visible after clicking Next");
    }
    

    @Test
    public void dateValidation_endBeforeStart_showsValidationOrDisablesNext() {
        DestinationPage destinationPage = openDestination();
        Assert.assertTrue(destinationPage.clickEurope(), "Failed to click Europe tile");
        Assert.assertTrue(destinationPage.clickNextAndWaitForDate(), "Did not navigate to date step");

        // Pick a valid start (today+14), then click an end that is before start (start-1)
        java.time.LocalDate start = DateUtils.todayPlusDays(14);
        java.time.LocalDate invalidEnd = start.minusDays(1);

        // Open calendar and click specific ISO dates
        Assert.assertTrue(destinationPage.clickCalendarDayIso(start.toString()),
                "Failed to click start day via picker");
        Assert.assertTrue(destinationPage.clickCalendarDayIso(invalidEnd.toString()),
                "Failed to click end day via picker");

        // Behavior-based validation: clicking Next should keep us on the Date step
        Assert.assertTrue(destinationPage.clickNextOnDate(), "Failed to click Next on Date step");
        Assert.assertTrue(destinationPage.isOnDateStep(),
                "Should remain on the Date step when end date is before start date");
    }
}

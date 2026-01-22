package testCases;

import Base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pagesObject.DestinationPage;
import pagesObject.TravelPolicyPage;
import pagesObject.components.DateSection;
import utils.DateUtils;

import java.time.LocalDate;

public class DateSectionTest extends BaseClass {

    private DateSection openDateSectionWithEurope() {
        TravelPolicyPage policyPage = new TravelPolicyPage(driver).open();
        DestinationPage destinationPage = policyPage.clickPurchaseForNewCustomer();
        Assert.assertTrue(destinationPage.isAt(), "Not on destination step");
        Assert.assertTrue(destinationPage.clickEurope(), "Failed to click Europe tile");
        Assert.assertTrue(destinationPage.clickNextAndWaitForDate(), "Did not navigate to date step");
        return destinationPage.getDateSection();
    }

    @Test
    public void datePicker_selectStartPlus7_andEndPlus30_thenProceed() {
        DateSection ds = openDateSectionWithEurope();
        Assert.assertTrue(ds.selectDatesViaPickerRelative(7, 30), "Failed to select dates via date picker (7/30)");
        int shownDays = ds.waitForTripDaysOnNext(12);
        Assert.assertTrue(shownDays >= 0, "Next button did not show a days count");
        Assert.assertEquals(shownDays, 31, "Expected 31 inclusive days for 7/30 selection");
        Assert.assertTrue(ds.clickNextAndWaitForScreenTitle(),
                "Next component screen title was not visible after clicking Next");
    }

    @Test
    public void absoluteDates_27Jan_to_28Feb_show33Days() {
        DateSection ds = openDateSectionWithEurope();
        Assert.assertTrue(ds.selectDatesAbsolute("27/01/2026", "28/02/2026"), "Failed to set absolute dates");
        int shown = ds.waitForTripDaysOnNext(12);
        Assert.assertTrue(shown >= 0, "Next button did not show a days count");
        Assert.assertEquals(shown, 33, "Expected 33 inclusive days");
    }

    @Test
    public void absoluteDates_28Jan_to_27Feb_show31Days() {
        DateSection ds = openDateSectionWithEurope();
        Assert.assertTrue(ds.selectDatesAbsolute("28/01/2026", "27/02/2026"), "Failed to set absolute dates");
        int shown = ds.waitForTripDaysOnNext(12);
        Assert.assertTrue(shown >= 0, "Next button did not show a days count");
        Assert.assertEquals(shown, 31, "Expected 31 inclusive days");
        Assert.assertTrue(ds.verifyTripDaysOnNextMatchesInputs(0),
                "Inclusive trip days derived from inputs do not match UI days");
    }

    @Test
    public void missingDates_clickNext_showsGeneralError() {
        DateSection ds = openDateSectionWithEurope();
        Assert.assertTrue(ds.clickNextAndWaitForGeneralError("סליחה, אבל כדי שנתקדם צריך למלא פרטים"),
                "Expected general error message was not shown");
    }
}

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
}

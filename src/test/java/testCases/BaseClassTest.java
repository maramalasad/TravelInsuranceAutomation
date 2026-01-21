package testCases;

import Base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;


public class BaseClassTest extends BaseClass {
    @Test
    public void openTravelPolicyPage() {
        driver.get("https://digital.harel-group.co.il/travel-policy");
        String currentURL = driver.getCurrentUrl();
        Assert.assertTrue(currentURL.contains("digital.harel-group.co.il/travel-policy"));
    }

}

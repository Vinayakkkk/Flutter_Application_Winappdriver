package base;

import config.ConfigManager;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.Duration;

import utils.TestUtils;

public class TestBase {
    protected static WindowsDriver driver;
    protected static WebDriverWait wait;
    protected static final ConfigManager config = ConfigManager.getInstance();
    
    @BeforeSuite
    public void beforeSuite() {
        // Initialize test properties and environment
        TestUtils.log("Test Suite Started");
    }
    
    @BeforeClass
    @Step("Initialize Windows Driver")
    public void setup() throws Exception {
        // Get configuration values
        String appPath = config.getAppPath();
        String appiumUrl = config.getAppiumUrl();
        
        TestUtils.log("Initializing Windows Driver");
        TestUtils.log("Environment: " + config.getEnvironment());
        TestUtils.log("App Path: " + appPath);
        TestUtils.log("Appium URL: " + appiumUrl);
        
        // Initialize Windows options
        WindowsOptions options = new WindowsOptions();
        options.setPlatformName("Windows");
        options.setCapability("app", appPath);
        options.setCapability("deviceName", "WindowsPC");
        
        // Initialize the driver
        driver = new WindowsDriver(new URL(appiumUrl), options);
        
        // Set timeouts
        int implicitWait = config.getImplicitWait();
        int explicitWait = config.getExplicitWait();
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        
        // Maximize the window
        try {
            driver.manage().window().maximize();
            TestUtils.log("Application window maximized");
        } catch (Exception e) {
            TestUtils.log("Warning: Could not maximize window - " + e.getMessage(), "WARN");
        }
        
        Allure.addAttachment("App Launched", "Application started successfully");
        TestUtils.log("Windows Driver initialized successfully with timeouts - Implicit: " + 
            implicitWait + "s, Explicit: " + explicitWait + "s");
    }
    
    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String testName = result.getName();
            String errorMessage = result.getThrowable().getMessage();
            
            TestUtils.log("Test Failed: " + testName, "ERROR");
            TestUtils.log("Failure Reason: " + errorMessage, "ERROR");
            
            takeScreenshot("Test_Failed_" + testName);
            Allure.addAttachment("Failure Screenshot - " + testName, "image/png", 
                new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)), 
                ".png");
            Allure.addAttachment("Failure Reason", "text/plain", errorMessage);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            TestUtils.log("Test Passed: " + result.getName(), "PASS");
        } else {
            TestUtils.log("Test Status: " + result.getStatus() + " for " + result.getName(), "INFO");
        }
    }
    
    @AfterClass(alwaysRun = true)
    @Step("Tear down test environment")
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                TestUtils.log("Application closed successfully");
                Allure.addAttachment("App Closed", "Application closed successfully");
            } catch (Exception e) {
                TestUtils.log("Error while closing application: " + e.getMessage(), "ERROR");
            }
        }
    }
    
    /**
     * Takes a screenshot and attaches it to the Allure report
     * @param screenshotName Name for the screenshot
     * @return Byte array of the screenshot
     */
    @Attachment(value = "Screenshot - {0}", type = "image/png")
    public byte[] takeScreenshot(String screenshotName) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            TestUtils.log("Screenshot taken: " + screenshotName);
            return screenshot;
        } catch (Exception e) {
            TestUtils.log("Failed to take screenshot: " + e.getMessage(), "ERROR");
            return new byte[0];
        }
    }
    
    /**
     * Helper method to wait for an element to be visible
     * @param by Locator of the element to wait for
     * @return WebElement that is visible
     */
    protected WebElement waitForElementVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
    
    /**
     * Helper method to wait for an element to be clickable
     * @param by Locator of the element to wait for
     * @return WebElement that is clickable
     */
    protected WebElement waitForElementClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }
    
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }
    
    public static WindowsDriver getDriver() {
        return driver;
    }
}

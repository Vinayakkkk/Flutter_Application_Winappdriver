package pages;

import base.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.TestUtils;

public class LoginPage extends TestBase {
    
    // Page Factory - Object Repository
    @FindBy(xpath = "//*[contains(@Name, 'Email address')]/Edit")
    private WebElement emailField;
    
    @FindBy(xpath = "//*[contains(@Name, 'Password')]/Edit")
    private WebElement passwordField;
    
    @FindBy(xpath = "//Button[@Name='Log in']")
    private WebElement loginButton;
    
    @FindBy(xpath = "//Button[@Name='Skip' or @Name='SKIP']")
    private WebElement skipButton;
    
    // Initializing the Page Objects
    public LoginPage() {
        PageFactory.initElements(driver, this);
    }
    
    // Page Actions
    public void skipWelcomeScreen() {
        try {
            // First, try to find and swipe on the image element if it exists
            try {
                WebElement imageElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(@Name, 'been looking for')]")
                ));
                
                // Perform the swipe action
                TestUtils.swipeElement(imageElement, driver, 200, -200);
                TestUtils.log("Performed swipe action on welcome screen");
                
                // Small delay to allow any animation to complete
                Thread.sleep(1000);
            } catch (Exception e) {
                TestUtils.log("No swipe action needed or element not found: " + e.getMessage(), "DEBUG");
            }
            
            // Now click the skip button
            wait.until(ExpectedConditions.elementToBeClickable(skipButton)).click();
            TestUtils.log("Clicked on Skip button");
            
            // Small delay after clicking skip
            Thread.sleep(1000);
            
            try {
                // Click on Welcome back! element
                WebElement welcome = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(@Name, 'Welcome back!')]")
                ));
                welcome.click();
                TestUtils.log("Window focused by clicking 'Welcome back!'", "INFO");
                
                // Get window size for swipe coordinates
                org.openqa.selenium.Dimension windowSize = driver.manage().window().getSize();
                int swipeX = windowSize.getWidth() / 2;
                int startY = (int) (windowSize.getHeight() * 0.95);
                int endY = (int) (windowSize.getHeight() * 0.05);
                
                // Create and perform swipe action
                org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(
                    org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger1");
                org.openqa.selenium.interactions.Sequence swipe = new org.openqa.selenium.interactions.Sequence(finger, 1);
                
                swipe.addAction(finger.createPointerMove(java.time.Duration.ofMillis(0),
                    org.openqa.selenium.interactions.PointerInput.Origin.viewport(),
                    swipeX,
                    startY));
                
                swipe.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                
                swipe.addAction(finger.createPointerMove(java.time.Duration.ofMillis(100),
                    org.openqa.selenium.interactions.PointerInput.Origin.viewport(),
                    swipeX,
                    endY));
                
                swipe.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                
                ((org.openqa.selenium.remote.RemoteWebDriver) driver).perform(java.util.Arrays.asList(swipe));
                TestUtils.log("Performed swipe action on welcome screen", "INFO");
                
                // Small delay after swipe
                Thread.sleep(1000);
                
            } catch (Exception e) {
                TestUtils.log("Welcome back element not found or swipe action failed: " + e.getMessage(), "WARN");
            }
        } catch (Exception e) {
            TestUtils.log("Error in skipWelcomeScreen: " + e.getMessage(), "ERROR");
        }
    }
    
    
    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        TestUtils.log("Clicked on Login button");
    }
    
    public void login(String email, String password) {
        try {
            // Wait for the email field and enter email
            WebElement emailElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(@Name, 'Email address')]/Edit")
            ));
            
            // Click and clear using backspace
            emailElement.click();
            String emailLength = emailElement.getAttribute("Value.Value");
            if (emailLength != null && !emailLength.isEmpty()) {
                emailElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                emailElement.sendKeys(Keys.DELETE);
            }
            
            // Enter email
            emailElement.sendKeys(email);
            TestUtils.log("Entered email: " + email);
            
            // Small delay
            Thread.sleep(1000);
            
            // Find and enter password
            WebElement passwordElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(@Name, 'Password')]/Edit")
            ));
            
            // Click and clear using backspace
            passwordElement.click();
            String passLength = passwordElement.getAttribute("Value.Value");
            if (passLength != null && !passLength.isEmpty()) {
                passwordElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                passwordElement.sendKeys(Keys.DELETE);
            }
            
            // Enter password
            passwordElement.sendKeys(password);
            TestUtils.log("Entered password");
            
            // Small delay before clicking login
            Thread.sleep(1000);
            
            // Click login button
            clickLogin();
            
        } catch (Exception e) {
            TestUtils.log("Login failed: " + e.getMessage(), "ERROR");
            throw new RuntimeException("Login failed", e);
        }
    }
    
    // Verification Methods
    public boolean isLoginPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(emailField)).isDisplayed() &&
                   wait.until(ExpectedConditions.visibilityOf(passwordField)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

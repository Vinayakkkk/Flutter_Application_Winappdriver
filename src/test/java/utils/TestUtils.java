package utils;

import io.appium.java_client.windows.WindowsDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Utility class for common test operations
 */
public class TestUtils {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Log a message with timestamp and log level
     * @param message The message to log
     * @param level Log level (INFO, DEBUG, WARN, ERROR, PASS, FAIL)
     */
    public static void log(String message, String level) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logMessage = String.format("[%s] [%s] %s", timestamp, level.toUpperCase(), message);
        System.out.println(logMessage);
        
        // Attach to Allure report
        Allure.addAttachment("Log: " + level, "text/plain", logMessage);
    }
    
    /**
     * Log an informational message
     * @param message The message to log
     */
    public static void log(String message) {
        log(message, "INFO");
    }
    
    /**
     * Take a screenshot and attach it to the Allure report
     * @param driver The WindowsDriver instance
     * @param screenshotName Name for the screenshot
     */
    public static void takeScreenshot(WindowsDriver driver, String screenshotName) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(screenshotName, "image/png", new ByteArrayInputStream(screenshot), ".png");
            log("Screenshot taken: " + screenshotName);
        } catch (Exception e) {
            log("Failed to take screenshot: " + e.getMessage(), "ERROR");
        }
    }
    
    /**
     * Swipe an element horizontally
     * @param element The element to swipe
     * @param driver The WindowsDriver instance
     * @param startXOffset Starting X offset from element center
     * @param endXOffset Ending X offset from element center
     */
    public static void swipeElement(WebElement element, WindowsDriver driver, int startXOffset, int endXOffset) {
        log(String.format("Swiping element from X:%d to X:%d", startXOffset, endXOffset));
        
        int centerX = element.getLocation().getX() + (element.getSize().getWidth() / 2);
        int centerY = element.getLocation().getY() + (element.getSize().getHeight() / 2);
        
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(),
                centerX + startXOffset,
                centerY));
                
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600),
                PointerInput.Origin.viewport(),
                centerX + endXOffset,
                centerY));
                
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Arrays.asList(swipe));
    }
    
    /**
     * Wait for an element to be visible
     * @param element The element to wait for
     * @param timeoutInSeconds Maximum time to wait in seconds
     * @return true if element is visible within timeout, false otherwise
     */
    /**
     * Wait for an element to be visible
     * @param element The element to wait for
     * @param timeoutInSeconds Maximum time to wait in seconds
     * @return true if element is visible within timeout, false otherwise
     */
    public static boolean waitForElement(WebElement element, long timeoutInSeconds) {
        log(String.format("Waiting for element (timeout: %ds)", timeoutInSeconds));
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeoutInSeconds * 1000) {
            try {
                if (element.isDisplayed()) {
                    log("Element is now visible");
                    return true;
                }
            } catch (Exception e) {
                // Element not found, continue waiting
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("Wait for element was interrupted", "WARN");
                return false;
            }
        }
        log("Element not found within timeout", "WARN");
        return false;
    }
    
    /**
     * Scroll an element in the specified direction
     * @param element The element to scroll
     * @param driver The WindowsDriver instance
     * @param xOffset Horizontal scroll offset (positive for right, negative for left)
     * @param yOffset Vertical scroll offset (positive for down, negative for up)
     */
    public static void scrollElement(WebElement element, WindowsDriver driver, int xOffset, int yOffset) {
        try {
            // Get element location and size
            int startX = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int startY = element.getLocation().getY() + element.getSize().getHeight() / 2;
            
            // Create pointer input
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            
            // Create sequence of actions
            Sequence scroll = new Sequence(finger, 1);
            
            // Move finger into start position
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(0), 
                PointerInput.Origin.viewport(), startX, startY));
                
            // Finger comes down into contact with screen
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            
            // Finger moves to end position
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(600), 
                PointerInput.Origin.viewport(), 
                startX - xOffset, 
                startY - yOffset));
                
            // Finger goes up
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            // Perform the scroll
            driver.perform(Arrays.asList(scroll));
            
            log(String.format("Scrolled element by X: %d, Y: %d", xOffset, yOffset));
        } catch (Exception e) {
            log(String.format("Failed to scroll element: %s", e.getMessage()), "WARN");
        }
    }
}

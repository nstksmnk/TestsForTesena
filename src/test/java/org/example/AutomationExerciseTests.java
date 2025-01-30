package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class AutomationExerciseTests {

    WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(AutomationExerciseTests.class);
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        logger.info("Setting up the WebDriver...");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://automationexercise.com/");
        logger.info("Navigated to the website: https://automationexercise.com/");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing the browser...");
            driver.quit();
        }
    }

    public void clickOnConsentCookies() {
        logger.info("Clicking on 'Consent' button...");
        clickOnElementByXPath("//p[text()='Consent']");
        waitForElementVisibilityByXPath("//div[@class='logo pull-left']");
    }

    public WebElement waitForElementVisibilityByXPath(String elementXPath) {
        WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXPath)));
        return webElement;
    }

    public void clickOnElementByXPath(String elementXPath) {
        driver.findElement(By.xpath(elementXPath)).click();
    }

    public void clickOnElementByLinkText(String linkText) {
        driver.findElement(By.linkText(linkText)).click();
    }

    public void findElementByNameAndSendKeys(String name, String keys) {
        driver.findElement(By.name(name)).sendKeys(keys);
    }

    public void findElementByXPathAndSendKeys(String xpath, String keys) {
        driver.findElement(By.xpath(xpath)).sendKeys(keys);
    }

    @Test
    public void TC0_SubscribeValidEmail() {
        logger.info("Starting TC0_SubscribeValidEmail test...");

        try {
            clickOnConsentCookies();

            logger.info("Entering valid email...");
            WebElement emailField = waitForElementVisibilityByXPath("//input[@type='email']");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", emailField);
            emailField.sendKeys("userSubscription@gmail.com");
            clickOnElementByXPath("//button[@type='submit']");

            logger.info("Verifying alert-success...");
            waitForElementVisibilityByXPath("//div[text()='You have been successfully subscribed!']");

            logger.info("Alert-success was successfully verified.");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            throw e;
        }
    }

    @Test
    public void TC00_SubscribeInvalidEmail() {
        logger.info("Starting TC00_SubscribeInvalidEmail test...");

        try {
            clickOnConsentCookies();

            logger.info("Entering invalid email...");
            WebElement emailField = waitForElementVisibilityByXPath("//input[@type='email']");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", emailField);
            emailField.sendKeys("userSubscription");

            logger.info("Verifying error message...");
            String validationMessage = emailField.getAttribute("validationMessage");
            Assert.assertTrue(validationMessage.contains("@"));

            logger.info("Error message was successfully verified.");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            throw e;
        }
    }

    @Test
    public void TC3_LoginWithInvalidCredentials() {
        logger.info("Starting TC3_LoginWithInvalidCredentials test...");

        try {
            clickOnConsentCookies();

            logger.info("Clicking on 'Signup / Login' link...");
            clickOnElementByLinkText("Signup / Login");
            waitForElementVisibilityByXPath("//h2[text()='Login to your account']");

            logger.info("Entering invalid email and password...");
            findElementByNameAndSendKeys("email", "wrongUser@gmail.com");
            findElementByNameAndSendKeys("password", "wrongPassword");

            logger.info("Clicking on Login button...");
            clickOnElementByXPath("//button[text()='Login']");

            logger.info("Verifying error message...");
            waitForElementVisibilityByXPath("//p[text()='Your email or password is incorrect!']");

            logger.info("Error message was successfully verified.");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            throw e;
        }
    }

    @Test
    public void TC12_AddProductsToCart() {
        logger.info("Starting TC12_AddProductsToCart test...");

        try {
            clickOnConsentCookies();

            logger.info("Clicking on 'Products' button...");
            clickOnElementByXPath("//a[text()=' Products']");

            logger.info("Adding of the 1st product...");
            WebElement addToCartButton = waitForElementVisibilityByXPath("(//a[@data-product-id='1'])[1]");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
            addToCartButton.click();

            logger.info("Clicking on 'Continue Shopping' button");
            clickOnElementByXPath("//button[text()='Continue Shopping']");

            logger.info("Adding of the 2nd product...");
            addToCartButton = waitForElementVisibilityByXPath("(//a[@data-product-id='2'])[1]");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
            addToCartButton.click();

            logger.info("Clicking on 'View Cart' button...");
            clickOnElementByLinkText("View Cart");

            logger.info("Verifying the products was added to the cart...");
            driver.findElement(By.id("product-1")).isDisplayed();
            driver.findElement(By.id("product-2")).isDisplayed();

            String firstPrice = driver.findElement(By.xpath("(//td[@class='cart_price'])[1]")).getText();
            String secondPrice = driver.findElement(By.xpath("(//td[@class='cart_price'])[2]")).getText();
            Assert.assertEquals(firstPrice, "Rs. 500");
            Assert.assertEquals(secondPrice, "Rs. 400");

            String firstCount = driver.findElement(By.xpath("(//td[@class='cart_quantity'])[1]")).getText();
            String secondCount = driver.findElement(By.xpath("(//td[@class='cart_quantity'])[2]")).getText();
            Assert.assertEquals(firstCount, "1");
            Assert.assertEquals(secondCount, "1");

            String firstTotalPrice = driver.findElement(By.xpath("(//td[@class='cart_total'])[1]")).getText();
            String secondTotalPrice = driver.findElement(By.xpath("(//td[@class='cart_total'])[2]")).getText();
            Assert.assertEquals(firstTotalPrice, "Rs. 500");
            Assert.assertEquals(secondTotalPrice, "Rs. 400");

            logger.info("The 1st and the 2nd products was successfully added to the cart. "
                    + "\nPrices, counts and total prices are correct.");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            throw e;
        }
    }
}

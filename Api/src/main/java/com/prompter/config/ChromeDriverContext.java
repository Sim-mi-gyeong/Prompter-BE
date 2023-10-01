package com.prompter.config;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

// @Configuration
public class ChromeDriverContext {

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(ChromeDriverContext.class);
    private static final String CHROME_DRIVER_PATH = "./usr/bin/chromedriver";
    //	private static final String CHROME_DRIVER_PATH =
    // "/Users/smegyeong/Documents/project/Prompter-BE/chromedriver";
    //	private static final String CHROME_DRIVER_PATH =
    // "/Users/simmigyeong/Documents/GitHub/Prompter-BE/chromedriver";

    @Bean
    public WebDriver driver() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);

        ChromeOptions options = new ChromeOptions();

        // options.addArguments("--window-size=1366,768");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-gpu");
        options.addArguments("lang=ko_KR");
        options.addArguments("window-size=1920x1080");
        // options.setProxy(null);   // Factory method 'driver' threw exception with message: Proxy
        // must be set

        DesiredCapabilities capabilities = new DesiredCapabilities();
        // DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability("chromeOption.someOption", "value");
        capabilities.setCapability("pageLoadStrategy", "none");

        options.merge(capabilities);

        try {
            /*
             *
             * @ params
             * option : headless
             *
             */
            // driver = new ChromeDriver(capabilities);
            driver = new ChromeDriver(options);
        } catch (Exception e) {
            logger.error("### [driver error] msg: {}, cause: {}", e.getMessage(), e.getCause());
        }

        return driver;
    }

    // @Bean
    // public WebDriver setupChromeDriver() throws Exception {
    // 	System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
    //
    // 	ChromeOptions options = new ChromeOptions();
    //
    // 	// options.addArguments("--window-size=1366,768");
    // 	options.addArguments("--headless");
    // 	options.addArguments("--no-sandbox");
    // 	options.addArguments("disable-gpu");
    // 	options.addArguments("--disable-gpu");
    // 	options.addArguments("lang=ko_KR");
    // 	options.addArguments("window-size=1920x1080");
    // 	options.setProxy(null);
    //
    // 	DesiredCapabilities capabilities = new DesiredCapabilities();
    // 	// DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    // 	capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    // 	capabilities.setCapability("chromeOption.someOption", "value");
    // 	capabilities.setCapability("pageLoadStrategy", "none");
    //
    // 	options.merge(capabilities);
    //
    // 	try {
    // 		/*
    // 		 *
    // 		 * @ params
    // 		 * option : headless
    // 		 *
    // 		 */
    // 		// driver = new ChromeDriver(capabilities);
    // 		driver = new ChromeDriver(options);
    // 	} catch (Exception e) {
    // 		logger.error("### [driver error] msg: {}, cause: {}", e.getMessage(), e.getCause());
    // 	}
    //
    // 	return driver;
    // }
}

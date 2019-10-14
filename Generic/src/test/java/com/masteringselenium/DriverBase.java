package com.masteringselenium;

import com.masteringselenium.config.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
         DriverBase will deal with the marshaling of the threads. It will hold a pool of
   driver objects. We are using a ThreadLocal object to instantiate  our WebDriverThread
   objects in separate threads. Used a synchronized list where we can store all our
   instances of WebDriverThread. We have done this to enable us to keep track of our threads.

      We have also created a getDriver() method that uses the getDriver() method on the
    DriverFactory object to pass each test a WebDriver instance that it can use. We are
    doing this to isolate each instance of WebDriver to make sure that there is no cross
    contamination between tests. When our tests start running in parallel, we don't want
    different tests to start firing commands to the same browser window. Each instance
    of WebDriver is now safely locked away in its own thread.

      Since we are using this factory class to start up all our browser instances, we
    need to make sure that we close them down as well. To do this, we have created a
    method with an @AfterMethod annotation that will destroy the driver after our test
    has run. This also has the added advantage of cleaning up if our test fails to reach the line where it would normally call driver.quit(), for
    example, if there was an error in the test that caused it to fail and finish early.
    Note that our @AfterMethod and @BeforeSuite annotations have a parameter of
    alwaysRun = true set on them. This makes sure that these functions are always run.
    For example, with our @AfterMethod annotation this makes sure that, even if a test
    fails, we will call the driver.quit() method. This ensures that we shut down our
    driver instance which will in turn close the browser. This should reduce the
    chance of you having some open browser windows left over after your test run
    if some of your tests fail.
*/
public class DriverBase {

    private static List<DriverFactory> webDriverThreadPool =
            Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverThread;

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverThread = new ThreadLocal<DriverFactory>() {
            @Override
            protected DriverFactory initialValue() {
                DriverFactory webDriverThread = new DriverFactory();
                webDriverThreadPool.add(webDriverThread);
                return webDriverThread;
            }
        };

    }

    static WebDriver getDriver() {

        return driverThread.get().getDriver();
    }

    /*
         We have added @AfterMethod called clearCookies() that will clear down the
        browser's cookies after each test. This should reset the browser to a neutral
        state without closing it so that we can start another test safely.

        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            Sometimes, you may have a site that sets server-side cookies that
        Selenium is unaware of. In this case, clearing out your cookies may have no
        effect and you may find that closing down the browser is the only way to ensure
         a clean environment for each test.

             This is not to say that you can't use this method; you may not see any issues
        with the application that you are testing. You can of course use a mix of
        strategies; you can have multiple phases of testing. You can put tests that are
        able to reuse the browser in the first phase. You can then put tests that need a
        browser restart in your second phase.
        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   */
    @AfterMethod(alwaysRun = true)
    public static void clearCookies(){

        getDriver().manage().deleteAllCookies();
    }
    /*
            We don't actually know how many threads we are going to have run since this
        will be controlled by Maven. This is not an issue though, as this code has been
        written to make sure that we don't have to know. What we do know is that
        when our tests are finished, each WebDriver instance will be closed down cleanly
        and without errors, all thanks to the use of the webDriverThreadPool list.
    */
    @AfterSuite(alwaysRun = true)
    public static void quitDriver() {

        for(DriverFactory driver: webDriverThreadPool) {
            driver.quitDriver();
        }
    }
}
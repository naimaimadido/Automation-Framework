package com.masteringselenium.config;

import com.masteringselenium.config.DriverType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.masteringselenium.config.DriverType.CHROME;
import static com.masteringselenium.config.DriverType.valueOf;

/*
           This class holds a reference to a WebDriver object, and ensures that every time you
        call getDriver() you get a valid instance of WebDriver back. If one has been
        started up, you will get the existing one. If one hasn't been started up, it will
        start one for you.

          It also provides a quitDriver() method that will perform quit() on your WebDriver
        object. It also nullifies the WebDriver object held in the class. This prevents errors
        that would be caused by attempting to interact with a WebDriver object that has been closed.

          Note that we are using driver.quit() and not driver.close(). As a general rule of thumb,
        you should not use driver.close() to clean up. It will throw an error if something happened
        during your test that caused the WebDriver instance to close early. The close-and-cleanup
        command in the WebDriver API is driver.quit(). You would normally use driver.close() if
        your test opens multiple windows and you want to shut some of them.

*/

/* By using the following code:
            mvn clean verify -Dthreads=2 -Dwebdriver.gecko.driver=<PATH_TO_GECKODRIVER_BINARY>

       This time, you should have seen no difference to the last time you ran it. Let's

        check the error handling next:
                  mvn clean verify -Dthreads=2 -Dbrowser=iJustMadeThisUp -Dwebdriver.gecko.driver=
            <PATH_TO_GECKODRIVER_BINARY>
        Again, it should have looked exactly the same as the previous run. We couldn't
        find an enum entry called IJUSTMADETHISUP, so we defaulted to the FirefoxDriver.
        Finally, let's try a new browser:
                mvn clean verify -Dthreads=2 -Dbrowser=chrome*/
public class DriverFactory {
    /*
      First, we have added a new variable called selectedDriverType. We are going to use
   this to store the type of driver that we want to use to run tests. We have then
   added a constructor that will determine what selectedDriverType should be when we
   instantiate the class.
       The constructor looks for a system property called browser to work out what sort of
    DriverType is desired. There is some error handling that will make sure that if
    we can't identify the requested driver type we always fall back to a default, in
            this case FirefoxDriver. You can remove this error handling if you would prefer
    to error every time an invalid driver string is passed in.
       We have then added a new method called instantiateWebDriver(), which is very
    similar to the code that was previously inside getDriver(). The only real
    difference is that we can now pass a DriverType object to specify which sort of
    WebDriver object we want. We also now create a DesiredCapabilities object inside
    this new method because that needs to be passed into the getWebDriverObject() method.
            Finally, the getDriver() method has been tweaked to call the new
    instantiateDriver() method. One other thing that is important to note is that we
    are no longer passing around a WebDriver object; we are instead passing around
    a RemoteWebDriver object. This is because all the drivers now extend
    RemoteWebDriver by default.
*/
    private RemoteWebDriver webDriver;
    private DriverType selectedDriverType;

    private final String operatingSystem = System.getProperty("os.name").toUpperCase();
    private final String systemArchitecture = System.getProperty("os.arch");

    public DriverFactory(){
        DriverType driverType = CHROME;
        String browser = System.getProperty("browser", driverType.toString().toUpperCase());
        try{
            driverType = valueOf(browser);
        } catch (IllegalArgumentException ignored){
            System.err.println("Unknown driver specified," +
                    "defaulting to '" + driverType + "'...");
        } catch(NullPointerException ignored){
            System.err.println("No driver specified," +
                    "defaulting to '" + driverType + "'...");
        }
        selectedDriverType = driverType;
    }


    public RemoteWebDriver getDriver() {
        if (null == webDriver) {
            instantiateWebDriver(selectedDriverType);
        }

        return webDriver;
    }

    private void instantiateWebDriver(DriverType selectedDriverType) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");
        System.out.println("Local Operating System: " + operatingSystem);
        System.out.println("Local Architecture: " + systemArchitecture);
        System.out.println("Selected Browser: " + selectedDriverType);
        System.out.println("Current thread: " + Thread.currentThread().getId());
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        webDriver = selectedDriverType.getRemoteWebDriver(desiredCapabilities);
    }

    public void quitDriver() {
        if (null != webDriver) {
            webDriver.quit();
            webDriver = null;
        }
    }
}
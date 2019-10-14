package com.masteringselenium.config;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.util.HashMap;

public enum DriverType implements DriverSetup {


    FIREFOX {
        public RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {

            FirefoxOptions option = new FirefoxOptions();
            option.merge(capabilities);
            option.setHeadless(HEADLESS);
            return new FirefoxDriver(option);
        }
    },
    /*We have a couple of options here to try and keep things running smoothly. Chrome has various command-line switches
     that can be used when starting Chrome up with ChromeDriver. When we load up Chrome to run our tests, we don't want
     it asking us whether it can be made the default browser every time it starts, so we have disabled that check. We
     have also turned off the password manager so that it does not ask if you would like to save your login details
     every time you have a test that performs a login action.*/
    CHROME {
        public RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {

            HashMap<String, Object> chromePreferences = new HashMap<>();
            ChromeOptions options = new ChromeOptions();
            //Chrome has various command-line switches that can be used
            //when starting Chrome up with ChromeDriver
            chromePreferences.put("profile.password_manager_enabled", false);
            options.merge(capabilities);
            options.addArguments("--no-default-browser-check");
            options.setHeadless(HEADLESS);
            options.setExperimentalOption("prefs", chromePreferences);

            return new ChromeDriver(options);
        }
    },
    /*InternetExplorerDriver has a lot of challenges; it attempts to work with many different versions of Internet
    Explorer and generally does a very good job. These options are used to try to ensure that sessions are properly
    cleaned out when reloading the browser (IE8 is particularly bad at clearing its cache), and then trying to fix some
    issues with hovering. If you have ever tested an application that needs you to hover over an element to trigger some
     sort of popup, you have probably seen the popup flickering lots, and had intermittent failures when trying to interact
    with it. Setting ENABLE_PERSISTENT_HOVERING and requireWindowFocus should work around these issues. */
    IE {
        public RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {

            InternetExplorerOptions options = new InternetExplorerOptions();

            options.merge(capabilities);
            options.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
            options.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
            options.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);

            return new InternetExplorerDriver(options);
        }
    },
    EDGE {
        public RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {
            EdgeOptions options = new EdgeOptions();
            options.merge(capabilities);
            return new EdgeDriver(options);
        }
    },
    SAFARI {
        public RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {
            SafariOptions options = new SafariOptions();
            options.merge(capabilities);
            return new SafariDriver(options);
        }
    },
    OPERA {
        public RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) {
            OperaOptions options = new OperaOptions();
            options.merge(capabilities);
            return new OperaDriver(options);
        }

    };
    //The method getBoolean takes a System Property name as an argument, not the String value of the boolean
    public final static Boolean HEADLESS = Boolean.getBoolean("headless");

}

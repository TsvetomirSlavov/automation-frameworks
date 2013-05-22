package automation.tests;


import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import sun.plugin2.util.BrowserType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static automation.tests.ScreenCaptureHook.embedSnapshot;

public class SharedDriver extends EventFiringWebDriver {
    public static RemoteWebDriver WEB_DRIVER;
    private static DesiredCapabilities capabilities;
    private static DriverService service;

    private static String profile = System.getProperty("profile");
    private static String bit = System.getProperty("os.arch");

    private static String directory = System.getProperty("user.dir");
    private static String drivers = directory + File.separator + "src"
            + File.separator + "test" + File.separator + "resources" + File.separator + "drivers";
    private static String chrome_driver = "chromedriver";
    private static String ie_driver = "IEDriverServer";
    private static String headless_driver = "phantomjs";
    private static String firefox_bin_directory = System.getProperty("webdriver.firefox.bin");

    private static final Thread CLOSE_THREAD = new Thread() {
        @Override
        public void run() {
            WEB_DRIVER.close();
        }
    };

    private static String isPlatform() {
        String platform;
        Platform current = Platform.getCurrent();
        if(Platform.MAC.is(current)) {
            platform = "MAC";
        } else if(Platform.LINUX.is(current) || Platform.UNIX.is(current)) {
            platform = "LINUX";
        } else if(Platform.ANDROID.is(current)) {
            platform = "ANDROID";
        } else {
            platform = "WINDOWS";
        }
        return platform;
    }

    /*
        constructor
     */
    public SharedDriver() {
        super(WEB_DRIVER);
        manage().window().maximize();
        manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    static {
        if(profile.equalsIgnoreCase("Chrome")){
            /*
                dynamically assigns chrome driver by identifying operating system
                and bit architecture.
             */
            if(isPlatform().equalsIgnoreCase("MAC")) {
                chrome_driver = chrome_driver + "_mac" + File.separator + chrome_driver;
            } else if(isPlatform().equalsIgnoreCase("LINUX")) {
                if(bit.contains("64")) {
                    chrome_driver = chrome_driver + "_linux64" + File.separator + chrome_driver;
                }  else {
                    chrome_driver = chrome_driver + "_linux32" + File.separator + chrome_driver;
                }
            } else if (isPlatform().equalsIgnoreCase("WINDOWS")) {
                chrome_driver = chrome_driver + "_win" + File.separator + chrome_driver + ".exe";
            }
            try {
                setWebDriverToChrome();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (profile.equalsIgnoreCase("IE")) {
            /*
                dynamically assigns ie driver by identifying operating system
                and bit architecture.
                Prints out exception if operating system or platform is not Windows.
             */
            if(isPlatform().equalsIgnoreCase("WINDOWS")) {
                if(bit.contains("64")) {
                    ie_driver = ie_driver + "_x64" + File.separator + ie_driver + ".exe";
                } else {
                    ie_driver = ie_driver + "_Win32" + File.separator + ie_driver + ".exe";
                }
            } else {
                System.err.println(profile + "is not supported on " + isPlatform());
            }
            try {
                setWebDriverToIE();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }  else if(profile.equalsIgnoreCase("Firefox")) {
            /*

             */
            try {
                setWebDriverToFirefox();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if(profile.equalsIgnoreCase("PhantomJS") || profile.equalsIgnoreCase("Headless")) {
            /*

             */
            if(isPlatform().equalsIgnoreCase("WINDOWS")) {
                headless_driver = headless_driver + "_win" + File.separator + headless_driver + ".exe";
            } else if (isPlatform().equalsIgnoreCase("MAC")) {
                headless_driver = headless_driver + "_mac" + File.separator + headless_driver;
            }  else if(isPlatform().equalsIgnoreCase("LINUX")) {
                if(bit.contains("64")) {
                    headless_driver = headless_driver + "_linux64" + File.separator + headless_driver;
                } else {
                    headless_driver = headless_driver + "_linux32" + File.separator + headless_driver;
                }
            }
            try {
                setWebDriverToHeadless();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }  else if(profile.equalsIgnoreCase("Android")) {
            /*

             */
            try {
                setWebDriverToAndroid();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        Runtime.getRuntime().addShutdownHook(CLOSE_THREAD);
    }

    private static void setWebDriverToAndroid() throws MalformedURLException {

    }

    private static void setWebDriverToHeadless() throws MalformedURLException {
        String headless_driver_path = drivers + File.separator + "phantomjs" + File.separator + headless_driver;
        service = new PhantomJSDriverService.Builder()
                .usingPhantomJSExecutable(new File(headless_driver_path))
                .usingAnyFreePort()
                .build();

        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        capabilities = DesiredCapabilities.phantomjs();
        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        if(isPlatform().equalsIgnoreCase("WINDOWS")) {
            capabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.INTERNET_EXPLORER);
            capabilities.setCapability(CapabilityType.VERSION, "8");
        }

        WEB_DRIVER = new RemoteWebDriver(service.getUrl(), capabilities);
    }

    private static void setWebDriverToFirefox() throws MalformedURLException {
        capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.BINARY, firefox_bin_directory);
        capabilities.setCapability(String.valueOf(FirefoxDriver.DEFAULT_ENABLE_NATIVE_EVENTS), true);

        WEB_DRIVER = new FirefoxDriver(capabilities);
    }

    private static void setWebDriverToIE() throws MalformedURLException {
        String ie_driver_path = drivers + File.separator + "ie" + File.separator + headless_driver;
        service = new InternetExplorerDriverService.Builder()
                .usingDriverExecutable(new File(ie_driver_path))
                .usingAnyFreePort()
                .build();

        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);

        WEB_DRIVER = new RemoteWebDriver(service.getUrl(), capabilities);
    }

    private static void setWebDriverToChrome() throws MalformedURLException {
        String chrome_driver_path = drivers + File.separator + "chrome" + File.separator + chrome_driver;
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chrome_driver_path))
                .usingAnyFreePort()
                .build();

        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        capabilities = DesiredCapabilities.chrome();

        WEB_DRIVER = new RemoteWebDriver(service.getUrl(), capabilities);
    }

    @Override
    public void close() {
        if(Thread.currentThread() != CLOSE_THREAD) {
            throw new UnsupportedOperationException("You shouldn't close this WebDriver. It's shared and will close when the JVM exits.");
        }
        super.close();
    }

    @Before
    public void deleteAllCookies() {
        manage().deleteAllCookies();
    }

    @After
    public void registerWebDriverEventListener(final Scenario scenario) {
        register(new AbstractWebDriverEventListener() {
            @Override
            public void onException(Throwable throwable, WebDriver driver) {
                embedSnapshot(scenario);
            }
        });
    }
}
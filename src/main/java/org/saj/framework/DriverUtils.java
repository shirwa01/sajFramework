package org.saj.framework;

import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverUtils 
{
	Properties props;
	EventFiringWebDriver driver;
	private String userPath;
    private final Logger LOGGER = LoggerFactory.getLogger(DriverUtils.class);
    private String dockerRemoteDriverUrl = null;
    private boolean isWindows;
	
	public DriverUtils(Properties properties) 
	{
		props = properties;
		userPath = System.getProperty("user.dir");
		LOGGER.info("OS Name: " + System.getProperty("os.name"));
		if(System.getProperty("os.name").toLowerCase().indexOf("windows")>=0)
		{
			isWindows = true;
		}	
	}
	
	
	public EventFiringWebDriver getDriver()
    {
		setDriver();
        return driver;
    }


	private void setDriver() 
	{
		String browserName = props.getProperty("browserName").toLowerCase();
		if(isWindows)
		{	
			switch (browserName) 
			{
				case "firefox":
					System.setProperty("webdriver.gecko.driver", userPath + "/src/test/resources/drivers/Windows/geckodriver.exe");
					driver = new EventFiringWebDriver(new FirefoxDriver());
					break;
		
				case "ie":
					System.setProperty("webdriver.chrome.driver", userPath + "/src/test/resources/drivers/Windows/IEDriverServer.exe");
					driver = new EventFiringWebDriver(new InternetExplorerDriver());
					break;
					
				case "chrome":
					System.setProperty("webdriver.chrome.driver", userPath + "/src/test/resources/drivers/Windows/chromedriver.exe");
					driver = new EventFiringWebDriver(new ChromeDriver());
					break;
			}
		}
		else
		{
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			switch (browserName) 
			{
				case "firefox":
					capabilities = DesiredCapabilities.firefox();
					break;
		
				case "ie":
					capabilities = DesiredCapabilities.internetExplorer();
					break;
			}
			
            try
            {
                URL url = new URL(dockerRemoteDriverUrl);
                driver = new EventFiringWebDriver(new RemoteWebDriver(url, capabilities));
            }
            catch (Exception e)
            {
                LOGGER.error("Received following error while setting driver: " + browserName);
                LOGGER.error(e.getMessage());
            }
		}	
		
	}

}

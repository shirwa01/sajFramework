package org.saj.framework;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericUtils 
{
	private DriverUtils driverUtil;
	private String envName = "test";
	private Properties props = null;
	private String woringDirectory = System.getProperty("user.dir");
	private EventFiringWebDriver driver;
	private WebDriverWait driverWait;
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericUtils.class);
	
	
	public GenericUtils() 
	{
		if(System.getProperty("env") != null)
		{	
			envName = System.getProperty("env");
		}
		
		loadProperties(envName);
		String maxWaitTime = getValueFromProperties("MaxWaitTime");
		if(maxWaitTime == null)
		{	
			maxWaitTime = "120";
		}
		driverWait = new WebDriverWait(getDriver(), Integer.parseInt(maxWaitTime));
	}

	private void loadProperties(String env) 
	{
		props = new Properties();
        try 
        {
        	LOGGER.info("Loading properties from env: " + env);
        	File propFile = new File(woringDirectory+"/src/test/resources/" + env + ".properties");
            FileInputStream fis = new FileInputStream(propFile);
            props.load(fis);
            LOGGER.info("Props: " + props);
            driverUtil = new DriverUtils(props);
        } 
        catch (Exception e) 
        {
            LOGGER.info("Failed to read properties file for env: " + env);
        }		
	}

	public void visitPage(String url) 
	{
		getDriver().get(url);
		
	}
	
	public String getValueFromProperties(String key) 
	{
		return props.getProperty(key);
	}
	
	public EventFiringWebDriver getDriver() 
	{
		
		if (driver == null) 
		{
			driver = driverUtil.getDriver();
			LOGGER.info("In side getDriver and driver is: " + driver);
		}
		return driver;
	}

	public WebDriverWait waitFor() 
	{
		return (WebDriverWait) driverWait;
	}
	
	public void followLink(String linkText) 
	{
		try
		{
			WebElement ele = getDriver().findElement(By.linkText(linkText));
			if(ele != null)
			{
				clickButton(ele);
			}
			else
			{
				LOGGER.error("Link: " + linkText + " not found on page: " + getDriver().getCurrentUrl());
			}	
		}
		catch(Exception ex)
		{
			LOGGER.error("Received following error while clicking on link: " + linkText + "\n" + ex.getMessage());
		}
		
	}
	
	public void clickButton(WebElement element) 
	{
		if (element != null) 
		{
			Actions actions = new Actions(getDriver());
			actions.moveToElement(element);
			waitUntilElementIsClickable(element);
			actions.click().perform();
		}
		else {
			LOGGER.info(" clickButton() - Element is null");
		}
	}

	public WebElement waitUntilElementIsClickable(WebElement element) 
	{
			return waitFor().until(ExpectedConditions.elementToBeClickable(element));
	}

	public WebElement waitUntilElementIsVisible(WebElement element) 
	{
			return waitFor().until(ExpectedConditions.visibilityOf(element));
	}

	public String getCurrentUrl() 
	{
		return getDriver().getCurrentUrl();
	}
}

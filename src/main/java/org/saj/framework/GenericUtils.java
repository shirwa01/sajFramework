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
	public final int ARIA_LABEL = 0;
	public final int CSS = 1;
	public final int ID = 2;
	public final int XPATH = 3;
	public final int LINK_TEXT = 4;
	public final int NAME = 5;	
		
	
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
	
	public WebElement getElement(String locator) 
	{
		WebElement ele = null;
		
		for(int i=0; i<5; i++)
		{
			ele = getElementUsingIndex(i,locator);
			
			if(ele != null)
			{
				LOGGER.debug("Element " + locator + " found using index: " + i);
				break;
			}	
		}	
		
		if(ele == null)
		{
			LOGGER.debug("Element " + locator + " not found on page: " + getCurrentUrl());
		}	
		return ele;
	}
	
	public WebElement getElementUsingIndex(int type, String locator) 
	{
		WebElement element = null;
		
		switch (type) 
		{
			case ARIA_LABEL:
				element = getElementUsingCSS("*[aria-label='" + locator + "']");
				break;

			case ID:
				element = getElementUsingID(locator);
				break;
				
			case CSS:
				element = getElementUsingCSS(locator);
				break;
				
			case NAME:
				element = getElementUsingName(locator);
				break;
				
			case LINK_TEXT:
				element = getElementUsingLinkText(locator);
				break;
				
			case XPATH:
				element = getElementUsingXPATH(locator);
				break;	
		}
		
		return element;
	}

	public WebElement getElementUsingCSS(String cssLocator)
	{
		WebElement ele = null;
		if(cssLocator != null)
		{	
			try
			{
				ele = getDriver().findElement(By.cssSelector(cssLocator));
			}
			catch(Exception ex)
			{
				if(LOGGER.isDebugEnabled())
				{	
					LOGGER.info("getElementUsingCSS() - Can not find element with CSS locator: " + cssLocator + " on page: " + getDriver().getCurrentUrl());
				}
			}
		}
		return ele;
	}

	/**
	 * @param xpathExpression
	 * @return WebElement if found by xpath expression else return null
	 */
	
	public WebElement getElementUsingXPATH(String xpathExpression)
	{
		WebElement element = null;
		
		try
		{
			element =  getDriver().findElement(By.xpath(xpathExpression));
		}
		catch(Exception e)
		{
			//handleException(e, "Element not found using xpath: " + xpathExpression);
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.info("getElementUsingXPATH() - Element not found using xpath: " + xpathExpression);
			}	
		}
		return element;
	}

	public WebElement getElementUsingID(String elementId) 
	{
		WebElement ele = null;
		
		try
		{
			if(elementId != null)
			{	
				ele = getDriver().findElement(By.id(elementId));
			}
			
		}
		catch (Exception ex)
		{
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.info("getElementUsingID() - Element not found using element id: " + elementId);
			}	
		}
		
		return ele;
	}

	public WebElement getElementUsingName(String name) 
	{
		WebElement ele = null;
		try
		{
			ele = driver.findElement(By.name(name));
		}
		catch(Exception ex)
		{
			if(LOGGER.isDebugEnabled())
			{	
				LOGGER.info("getElementUsingName() - Can not find element with Name locator: " + name + " on page: " + getDriver().getCurrentUrl());
			}
		}
		return ele;
	}

	public WebElement getElementUsingLinkText(String entityName) 
	{
		WebElement ele = null;
		if(entityName != null)
		{	try
			{
				ele = getDriver().findElement(By.linkText(entityName));
			}
			catch(Exception ex)
			{
				if(LOGGER.isDebugEnabled())
				{	
					LOGGER.info("Link " + entityName + " not found on page " + getDriver().getCurrentUrl());
				}
			}
		}
		return ele;
	}
	
	public void clickOnLink(String linkName) throws Exception 
	{
		WebElement ele = getElementUsingLinkText(linkName);
		if (ele != null) 
		{
			ele.click();
		} 
		else 
		{
			throw new Exception(linkName + " link not found");
		}
	}
	public void waitForElement(String element) 
	{
		try 
		{
			//System.out.println("Waiting for: " + element);
			WebElement ele = null;
			int maxWaitinSeconds = Integer.parseInt(getValueFromProperties("MaxWaitTime"));
			boolean repeat = true;
			float secondsComplete = 0; 
			waitForSeconds(0.500);
			
			while(repeat)
			{	
				ele = getElement(element);
				LOGGER.debug("seconds complete: " + secondsComplete + " & maxWaitinSeconds: " + maxWaitinSeconds + " for: " + element);
				if (ele != null || secondsComplete > maxWaitinSeconds )
				{
					repeat = false;
					break;
				}
				else
				{	
					//System.out.println("Element: " + element + " is not yet found");
					Thread.sleep(500);
					secondsComplete+=0.5;
				}
			}
			if(ele != null)
			{
				waitUntilElementIsVisible(ele);
			}
			else
			{
				if(LOGGER.isInfoEnabled())
				{
					LOGGER.info("Element " + element + " does not exisit on page: " + getCurrentUrl());
				}	
			}
		} 
		catch (Exception e) 
		{
			LOGGER.info("Element " + element + " does not exist on page: " + getCurrentUrl());
		}
	}	
	
	public void waitForSeconds(double seconds) {
		try {
			Thread.sleep(new Double((seconds * 1000)).longValue());
		} catch (InterruptedException e) {
			handleException(e, "Wait got interrupted");
		}
	}
	
	public void handleException(Exception e, String message) 
	{
		if(LOGGER.isInfoEnabled())
		{	
			LOGGER.info(message);
			e.printStackTrace();
		}
	}
}

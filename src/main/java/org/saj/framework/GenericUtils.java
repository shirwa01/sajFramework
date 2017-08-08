package org.saj.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.DataTable;

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
	private String randomStringForCurrentTest = null;
	private Integer randomNumberForCurrentTest = null;		

	@FindBy(css="span[class='siebui-popup-button'] input[aria-label='Find']")
	private WebElement pickerFind;
	
	@FindBy(css="span[class='siebui-popup-button'] [aria-label='Starting with']")
	private WebElement pickerStartingWith;
	
	@FindBy(css="td[class='siebui-popup-filter'] span[class='siebui-popup-button'] button[aria-label*=':Go']")
	private WebElement pickerSearchBtn;
	
	
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
		
		PageFactory.initElements(getDriver(), this);
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
	
	public String getRandomStringForCurrentTest() 
	{
		if(randomStringForCurrentTest == null)
		{	
			 randomStringForCurrentTest = getRandomAlphaString(5).toLowerCase();
		}
		return randomStringForCurrentTest;
	}
	
	public int getRandomNumberForCurrentTest() 
	{
		if(randomNumberForCurrentTest == null)
		{	
			randomNumberForCurrentTest = new Integer (getAnyRandomIntegerIntheRange(100000000, 999999999));
			LOGGER.info("Random number: " + randomNumberForCurrentTest.intValue());
		}
		return randomNumberForCurrentTest.intValue();
	}

	public String getRandomAlphaString(int len) 
	{
		String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public int getAnyRandomIntegerIntheRange(int low, int high) 
	{
		Random random = new Random();
		int randomInt = random.nextInt(high) + low;
		return randomInt;
	}

	public void fillFieldWith(String locator, String value) 
	{
		WebElement ele = getElement(locator);
		if(ele != null)
		{
			fillElementWithValue(value, ele);
		}
		else
		{
			if(LOGGER.isInfoEnabled())
			{
				LOGGER.info("Field name " + locator + " does not exisit on page: " + getCurrentUrl());
			}	
		}
		
	}
	
	public void fillElementWithValue(String value, WebElement ele) 
	{
		waitUntilElementIsVisible(ele);
		ele.clear();
		ele.sendKeys(getCorrectValue(value));
	}

	private String getCorrectValue(String value) 
	{
		if(value.contains("<randstring>"))
		{
			value = value.replace("<randstring>", getRandomStringForCurrentTest());
		}
		else if(value.contains("<randnumber>"))
		{
			value = value.replace("<randnumber>", String.valueOf(getRandomNumberForCurrentTest()));
		}
		LOGGER.info("value: " + value);
		return value;
	}

	public boolean verifyTextExistsOnPage(String value) 
	{
		boolean isValueExisit = false;
		
		if(getDriver().getPageSource().contentEquals(getCorrectValue(value)))
		{		
			isValueExisit = true;
		}
		else
		{
			LOGGER.error( value + " not found on the page: " + getCurrentUrl());
		}	
		return isValueExisit;
	}
	
	public boolean verifyLinkExistsOnPage(String linkText) 
	{
		boolean isLinkPresent = false;
		if(getElementUsingLinkText(getCorrectValue(linkText)) != null)
		{
			isLinkPresent = true;
		}	
		return isLinkPresent;
	}

	public void fillInFieldUsingXpath(String xpathExpression, String value) 
	{
		WebElement ele = getElementUsingXPATH(xpathExpression);
		
		if(ele != null)
		{
			fillElementWithValue(value, ele);
		}	
	}
	
	public boolean verifyAllFieldsHasExpectedValues(List<Map<String, String>> inputData) 
	{
		boolean isDataCorrect = true;
		
		LOGGER.info("Data size: " + inputData.size());
		
		for(Map<String,String> eachrow:inputData)
		{
			Iterator iterator = eachrow.entrySet().iterator();
			while (iterator.hasNext()) 
			{
				Map.Entry pair = (Map.Entry) iterator.next();
				String key = (String) pair.getKey();
				String value = (String) pair.getValue();
				if(value.contains("<randstring>"))
				{
					value = value.replace("<randstring>", getRandomStringForCurrentTest().toLowerCase());
					LOGGER.info("value: " + value);
				} 
				LOGGER.debug("key: " + key + " value: " + value);
				String fieldLocator="td[id='1"+ key + "']";
				LOGGER.debug(fieldLocator);
				WebElement element = getElementUsingIndex(CSS, fieldLocator);
				if(element == null || !element.getAttribute("innerText").equals(value))
				{
					isDataCorrect = false;
					LOGGER.info("Expected value: " + value + " not found for element: " + key);
					break;
				}
			}
		}	
		
		return isDataCorrect;
	}
	
	public void selectAppletFromMenu(String menuLevel, String appletName) 
	{
		boolean isAppletFound = false;
		waitForSeconds(3);
		//Check if the applet is present in the tabs displayed
		String locator = "div[title='" + menuLevel + "'] li a";
		List<WebElement> allAppletsFromThirdLevelView = getDriver().findElements(By.cssSelector(locator));
		//System.out.println("No of tabs found: " + allAppletsFromThirdLevelView.size());
		for(WebElement currElement:allAppletsFromThirdLevelView)
		{
			LOGGER.debug("Tab name: " + currElement.getText());
			if(currElement.getText().equals(appletName))
			{
				isAppletFound = true;
				clickButton(currElement);
				break;
			}	
		}
		
		//If applet is not in the tab then chose it from select drop down
		if(!isAppletFound)
		{
			LOGGER.debug("Selecting the applet from drop down");
			
			String selectLocator = "div[title='" + menuLevel + "'] li select";
			WebElement selectTab = getElementUsingCSS(selectLocator);
			if(selectTab != null)
			{
				selectValueFromDropdown(selectTab, appletName);
				//System.out.println("selected tab: " + appletName);
			}	
		}	
		waitForSeconds(2);
	}
	
	public void selectValueFromDropdown(WebElement element, String value) 
	{
		try 
		{
			if (element != null) 
			{
				Actions actions = new Actions(getDriver());
				actions.moveToElement(element);
				Select selElement = new Select(element);
				selElement.selectByVisibleText(value);
			} 
			else 
			{
				LOGGER.info("selectValueFromDropdown() - Found Null element");
			}
		} 
		catch (Exception e) 
		{
			handleException(e, "Received error while setting value: " + value
					+ " for element: " + element.getText());
		}
	}

	public void writeToFile(String inputText, String fileName) 
	{
		String fullFileName = fileName +".txt";
		if(inputText.contains("<randstring>"))                                                             
		{                                                                                              
			inputText = inputText.substring(0,inputText.indexOf("<randstring>")) + getRandomStringForCurrentTest();
			LOGGER.debug("Link Text: " + inputText);                                                            
		}
		try
		{
			PrintWriter writer = new PrintWriter( fullFileName, "UTF-8");
		    writer.println(inputText);
		    writer.close();
		}
		catch(Exception ex)
		{
			LOGGER.info("Exception encountered while writing the file: " + fullFileName);
			ex.printStackTrace();
		}
	}

	public void clickOnEelmentUsingXpath(String xpathLocator) 
	{
		WebElement ele = getElementUsingIndex(XPATH, xpathLocator);
		if(ele != null)
		{
			clickButton(ele);
		}
		else
		{
			LOGGER.info("Element not found using xpath: " + xpathLocator);
		}	
	}

	public void saveTheAppletRecord(String appletName) 
	{
		WebElement menuBtn = getAppletMenuButtons(appletName);
		
		if(menuBtn != null)
		{
			clickButton(menuBtn);
			String cssLocator = "button[aria-label*='" + appletName + "'] + ul li[data-caption='Save Record                [Ctrl+S]'] a";
			//waitForElement(cssLocator);
			waitForSeconds(2);
			//WebElement saveRecord = getElementUsingIndex(CSS, "ul[class*='siebui-appletmenu'] li[data-caption='Save Record                [Ctrl+S]'] a");
			LOGGER.debug("CSSLocator: " + cssLocator);
			WebElement saveRecord = getElementUsingIndex(CSS, cssLocator);
			if(saveRecord != null)
			{	
				clickButton(saveRecord);
				LOGGER.info("Saved the form: " + appletName);
			}
			else
			{
				LOGGER.error("No save record found for: " + appletName);
			}	
		}	
	}
	
	private WebElement getAppletMenuButtons(String appletName) 
	{
		WebElement currMenu = null;
		
		List<WebElement> allAppletMenuBtns = getDriver().findElements(By.className("siebui-appletmenu-btn"));
		
		LOGGER.debug("total applets: " + allAppletMenuBtns.size());
		
		for(WebElement ele:allAppletMenuBtns)
		{
			String menuTitle = ele.getAttribute("title");
			LOGGER.debug("menu title: " + menuTitle);
			if(menuTitle.contains(appletName))
			{
				currMenu = ele;
				break;
			}
		}	
		
		if(currMenu == null)
		{
			LOGGER.error("No menu found for: " + appletName + " on page: " + getCurrentUrl());
		}	
		
		return currMenu;
	}
	
	public boolean verifyPopupTextIsCorrectAs(String popupText) 
	{
		boolean isTextCorrect = false;
		try 
		{
			waitFor().until(ExpectedConditions.alertIsPresent());
			Alert alert = getDriver().switchTo().alert();
			LOGGER.debug("Alert Message: " + alert.getText().trim());
			if(alert.getText().trim().equals(popupText))
			{
				isTextCorrect = true;
				alert.accept();
			}
			else
			{
				LOGGER.error("Expected Alert Message: " + popupText );
				LOGGER.error("Found Alert Message: " + alert.getText().trim());
			}	
		} 
		catch (Exception e) 
		{
			LOGGER.info("No Alert present");
		}
		return isTextCorrect;
	}

	public void performActionOnMenu(String menuOption, String action) 
	{
		String locator = "//div[@class='applicationMenu']/span/li/a/span[contains(text(),'" + menuOption + "')]";
		
		WebElement menu = getElementUsingIndex(XPATH, locator);
		
		if(menu != null)
		{
			clickButton(menu);
			String actionLocator = "li[data-caption*='" + action + "']";
			WebElement actionElement = getElementUsingIndex(CSS, actionLocator);
			if(actionElement != null)
			{
				clickButton(actionElement);
			}	
		}
		else
		{
			LOGGER.info("Menu: " + menuOption + " not found");
		}	
		
	}
	
	public void drilldownHyperLink() 
	{
		waitForElement("1Name");
		WebElement link = getElementUsingIndex(XPATH, "//td[@id='1Name']/a");
		if(link != null)
		{
			clickButton(link);
		}	
	}
	
	public void selectValueFromSelectionPickerUsingFilter(String fieldLocator, String filterType, String value) 
	{
		String locator = "input[aria-label='" + fieldLocator +"']+span";
		WebElement element = getElementUsingIndex(CSS, locator);
		if(element != null)
		{
			clickButton(element);
			waitForSeconds(2); 
			selectFromPopupMenuUsing(filterType, value);
		}
		else
		{
			LOGGER.error("Pick applet not found using locator: " + locator);
		}	
	}
	
	public void selectFromPopupMenuUsing(String findBy, String filterValue) 
	{
		fillElementWithValue(findBy, pickerFind);
		//fillTextField(pickerFind, findBy);
		//fillTextField(pickerStartingWith, filterValue);
		fillElementWithValue(filterValue, pickerStartingWith);
		clickButton(pickerSearchBtn);
		String popupLocator = "tr[class*='jqgrow']";
		waitForSeconds(0.125);
		selectFromPopupMenuAs(popupLocator, filterValue);
	}
	
	public void selectFromPopupMenuAs(String popupLocator, String value) 
	{
		
		List<WebElement> allResults = getDriver().findElements(By.cssSelector(popupLocator));
		LOGGER.info("Total results found: " + allResults.size());
		if(value != null)
		{
			for(WebElement currSup:allResults)
			{
				if(currSup.getText().contains(value))
				{
					clickButton(currSup);
					break;
				}	
			}
			LOGGER.info("Expected result: " + value + " not found. Selecting first result");
			allResults.get(0).click();
		}
		else
		{	
			//Surname is not given then select third date from the selection
			if(allResults.size() > 1)
			{	
				allResults.get(1).click();
			}
			else
			{
				allResults.get(0).click();
			}	
		}

		//Check if Selection button is present
		WebElement addSelection = getElementUsingIndex(CSS, "span[class='siebui-popup-button'] button[aria-label*=':Add >']");
		if(addSelection != null)
		{
			clickButton(addSelection);
		}	
		
		//Click ok button
		WebElement popupOkBtn = getElementUsingIndex(CSS, "span[class='siebui-popup-button'] button[aria-label*=':OK']");
		clickButton(popupOkBtn);

	}
	
	public void selectValueFromSelectionPickerUsingFilterAndAddVauleToSelectedOption(String locator, String findBy,
			String filterValue, String option) 
	{
		String fieldLocator = "input[aria-label='" + locator +"']+span";
		WebElement element = getElementUsingIndex(CSS, fieldLocator);
		if(element != null)
		{
			clickButton(element);
			waitForSeconds(2); 
			fillElementWithValue(findBy, pickerFind);
			fillElementWithValue(filterValue, pickerStartingWith);
			clickButton(pickerSearchBtn);
			waitForSeconds(0.125);
			WebElement addSelection = getElementUsingIndex(CSS, "span[class='siebui-popup-button'] button[aria-label*=':Add >']");
			if(addSelection != null)
			{
				clickButton(addSelection);
				String[] optionsValue = option.split(",");
				WebElement field = getElement("1"+optionsValue[0]);
				if(field != null)
				{	
					clickButton(field);
					WebElement inputField = getElement("1_" + optionsValue[0]);
					if(inputField != null)
					{
						fillElementWithValue(optionsValue[1], inputField);
					}
					
					WebElement okBtn = getElementUsingIndex(CSS, "span[class='siebui-popup-button'] button[aria-label*=':OK']");
					if(okBtn != null)
					{
						clickButton(okBtn);
					}	
				}
				else
				{
					LOGGER.error("No element found using locator: " + optionsValue[0]);
				}	
			}	
		}
		else
		{
			LOGGER.error("Pick applet not found using locator: " + locator);
		}	
	}
	
	public void selectValueFromSelectionPickerWithoutUsingFilter(String fieldLocator, String field, String value) 
	{
		String locator = "input[aria-label='" + fieldLocator +"']+span";
		LOGGER.info("locator: " + locator);

		if(value.contains("STORED:"))
		{
			value = getValueFromFile(value.substring(value.indexOf(":")+1));
		}	
		
		WebElement element = getElementUsingIndex(CSS,locator);
		if(element == null)
		{
			//try another locator with id
			locator = "input[id='"+ fieldLocator +"']+span";
			LOGGER.info("trying another locator: " + locator);
			element = getElementUsingIndex(CSS,locator);
		}	
		
		if(element != null)
		{
			clickButton(element);
			//waitForElement("span[class='siebui-popup-button'] button[aria-label*=':Query']");
			waitForSeconds(2);
			WebElement searchBtn = getElementUsingIndex(CSS, "span[class='siebui-popup-button'] button[aria-label*=':Query']");

			if(searchBtn != null)
			{
				clickButton(searchBtn);
				waitForSeconds(0.125);

				WebElement ele = getElement("1"+field);
				if(ele != null)
				{	
					clickButton(ele);
					WebElement inputField = getElement("1_" + field);
					if(inputField != null)
					{
						
						fillElementWithValue(value, inputField);
						inputField.sendKeys(Keys.ENTER);
					}
					
					waitForSeconds(2);
					WebElement okBtn = getElementUsingIndex(CSS, "span[class='siebui-popup-button'] button[aria-label*=':OK']");
					if(okBtn != null)
					{
						clickButton(okBtn);
					}
					else
					{
						LOGGER.error("OK button not found using locator: " + "span[class='siebui-popup-button'] button[aria-label*=':OK']");
					}	
				}
				else
				{
					LOGGER.error("No element found using locator: " + field);
				}	
			}
			else
			{
				LOGGER.error("Query button not found using locator: " + "span[class='siebui-popup-button'] button[aria-label*=':Query']");
			}	
		}
		else
		{
			LOGGER.error("Pick applet not found using locator: " + locator);
		}	
		
	}
	
	public String getValueFromFile(String fileName) 
	{
		String value = null;
		String fullFileName = fileName +".txt";
		try
		{
			Path path = Paths.get(fullFileName);
			value =  Files.readAllLines(path).get(0);
		}
		catch (Exception ex)
		{
			LOGGER.info("Error received while reading the file: " + fullFileName);
			ex.printStackTrace();
		}
		LOGGER.debug("Value from file: " + fullFileName + " is: " + value);
		return value;
	}
	
	public void fillFieldWithStoredValue(String fieldName, String fileName) 
	{
		String value = getValueFromFile(fileName);
		LOGGER.info("value: " + value + " from filename: " + fieldName);
		fillFieldWith(fieldName, value);
	}
	
	public void clickElementWithinMenu(String elementName, String menuName) 
	{
		WebElement menu = getElementUsingIndex(ARIA_LABEL, menuName);
		if(menu != null)
		{
			clickButton(menu);
			
			String cssLocator = "button[aria-label*='" + menuName + "'] + ul li[data-caption='" + elementName + "'] a";
			//waitForElement(cssLocator);
			waitForSeconds(2);
			//WebElement saveRecord = getElementUsingIndex(CSS, "ul[class*='siebui-appletmenu'] li[data-caption='Save Record                [Ctrl+S]'] a");
			LOGGER.info("CSSLocator: " + cssLocator);
			WebElement option = getElementUsingIndex(CSS, cssLocator);
			if(option != null)
			{	
				clickButton(option);
				LOGGER.info("Selected option: " + elementName + " from menu: " + menuName);
			}
			else
			{
				LOGGER.error("No option found for: " + menuName);
			}
			
		}	
		
	}
	
	public boolean verifyElementIsNotVisible(String elementName) 
	{
		boolean isElementNotVisible = false;
		
		WebElement ele = getElement(elementName);
		if(ele == null)
		{
			isElementNotVisible = true;
		}	
		return isElementNotVisible;
	}
	
	public void storeAttributeValueForImmediateUse(String elementLocator, String attributeName) 
	{
		String value = null;
		WebElement ele = getElement(elementLocator);
		if(ele != null)
		{
			value = ele.getAttribute(attributeName);
			LOGGER.info("Value for element: " + elementLocator + " is: " + value);
			writeToFile(value, "temp");
		}	
		
	}
	
	public boolean verfiAllFieldsHasExpectedValueForAttribute(String attributeName, DataTable testData) 
	{
		boolean areAllValuesCorrect = true;

		List<Map<String, String>> allTestInput = testData.asMaps(String.class,String.class);
		
		
		
		LOGGER.info("Total size: " + allTestInput.size()) ;
		
		for(int i=0; i<allTestInput.size(); i++)
		{
			Iterator iterator = allTestInput.get(i).entrySet().iterator();
			while (iterator.hasNext()) 
			{
				Map.Entry pair = (Map.Entry) iterator.next();
				String key = (String) pair.getKey();
				String value = (String) pair.getValue();
				if(value.contains("<randstring>"))
				{
					value = value.replace("<randstring>", getRandomStringForCurrentTest().toLowerCase());
					LOGGER.debug("value: " + value);
				} 
				LOGGER.debug("key: " + key + " value: " + value);
				String fieldLocator="td[aria-labelledby*='_"+ key + "'][" + attributeName + "='"+value+"']";
				LOGGER.debug(fieldLocator);
				WebElement element = getElementUsingIndex(CSS, fieldLocator);
				if(element == null)
				{
					areAllValuesCorrect = false;
					LOGGER.error("Expected value: " + value + " not found for element: " + key + " using locator: " + fieldLocator);
					break;
				}	
			}
		}	
		
		
		return areAllValuesCorrect;
	}
	
	public boolean verifyPopupTextContainsMessage(String message) 
	{
		boolean isTextContains = false;
		try 
		{
			waitFor().until(ExpectedConditions.alertIsPresent());
			Alert alert = getDriver().switchTo().alert();
			LOGGER.debug("Alert Message: " + alert.getText().trim());
			if(alert.getText().trim().contains(message))
			{
				isTextContains = true;
				alert.accept();
			}
			else
			{
				LOGGER.error("Expected Alert Message: " + message );
				LOGGER.error("Found Alert Message: " + alert.getText().trim());
			}	
		} 
		catch (Exception e) 
		{
			LOGGER.info("No Alert present");
		}
		return isTextContains;
		
	}
	
	public boolean verifyElementHasSpecifiedAttributeValue(String elementLocator, String attribute, String value) 
	{
		boolean result = false;
		
		
		WebElement element = getElement(elementLocator);
		
		if(element != null)
		{
			LOGGER.debug("Found value for attribute: " + attribute + " as: " + element.getAttribute(attribute));
			if(value.contains("<randstring>"))
			{
				value = value.replace("<randstring>", getRandomStringForCurrentTest().toLowerCase());
				LOGGER.info("value: " + value);
			} 	
			
			
			if(element.getAttribute(attribute).equals(value))
			{
				result = true;
			}	
			else
			{
				LOGGER.info("Expected value for attribute: " + attribute + " was: " + value + " but found as: " + element.getAttribute(attribute));
			}	
		}
		else
		{
			LOGGER.info(elementLocator + " Element not found");
		}	
		
		return result;
	}
	
	public boolean verifyElementHasSpecifiedAttributeStoredValue(String element, String attribute, String fileName) 
	{
		boolean isValueCorrect = false;
		String value = getValueFromFile(fileName);
		isValueCorrect = verifyElementHasSpecifiedAttributeValue(element, attribute, value);
		return isValueCorrect;
	}
	
	public boolean verifyFieldHasValue(String fieldName, String expectedValue) 
	{
		boolean isValueCorrect = false;
		WebElement field = getElement(fieldName);
		
		if(expectedValue.contains("<randstring>"))
		{
			expectedValue = expectedValue.substring(0,expectedValue.indexOf("<randstring>")) + getRandomStringForCurrentTest();
			LOGGER.info("value: " + expectedValue);
		}	
		
		if(field != null)
		{
			if((field.getAttribute("value") != null) && (field.getAttribute("value").contains(expectedValue)))
			{
				isValueCorrect = true;
			}	

			if(field.getText().contains(expectedValue))
			{
				isValueCorrect = true;
			}
			
		}
		else
		{
			LOGGER.info("Element: " + fieldName + " not found.");
		}	
		return isValueCorrect;
	}
	
	public boolean verifyElementIsEnabled(String locator) 
	{
		boolean isEnabled = false;
		waitForElement(locator);
		WebElement ele = getElement(locator);
		if((ele.getAttribute("disabled") == null) ||(ele.getAttribute("disabled").equals("false")) )
		{
			isEnabled = true;
		}	
		return isEnabled;
	}
}

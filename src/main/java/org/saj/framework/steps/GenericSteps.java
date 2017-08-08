package org.saj.framework.steps;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.saj.framework.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GenericSteps 
{
	private GenericUtils utils;
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericSteps.class);
	
	public GenericSteps(GenericUtils genUtils) 
	{
		utils = genUtils;
	}
	
	@Given("^I am on \"([^\"]*)\"$")
	public void i_am_on(String url) throws Throwable 
	{
		utils.visitPage(url);
	}

	@When("^I follow \"([^\"]*)\"$")
	public void i_follow(String linkText) throws Throwable 
	{
		utils.followLink(linkText);
	}

	@Then("^The page url should be \"([^\"]*)\"$")
	public void the_page_url_should_be(String expectedUrl) throws Throwable 
	{
		Assert.assertTrue(expectedUrl.equals(utils.getCurrentUrl()));
	}

	@Then("^I wait for element \"([^\"]*)\"$")
	public void i_wait_for_element(String element) throws Throwable 
	{
		if(element.contains("<randstring>"))
		{
			element = element.substring(0,element.indexOf("<randstring>")) + utils.getRandomStringForCurrentTest();
			LOGGER.info("value: " + element);
		}
		utils.waitForElement(element);
	}

	@Then("^I fill in \"([^\"]*)\" with \"([^\"]*)\"$")
	public void i_fill_in_with(String element, String value) throws Throwable 
	{
		WebElement ele = utils.getElement(element);
		if(ele != null)
		{
			utils.fillElementWithValue(value, ele);
		}
		else
		{
			if(LOGGER.isInfoEnabled())
			{
				LOGGER.info("Element " + element + " not found to fill in value on page: " + utils.getCurrentUrl());
			}	
		}	
	}


	@Then("^I check \"([^\"]*)\"$")
	public void i_check(String element) throws Throwable 
	{
		WebElement ele = utils.getElement(element);
		if(ele != null)
		{
			utils.clickButton(ele);
		}
		else
		{
			if(LOGGER.isInfoEnabled())
			{
				LOGGER.info("Checkbox " + element + " does not exisit on page: " + utils.getCurrentUrl());
			}	
		}	
	}

	@Then("^I wait for the page to load$")
	public void i_wait_for_the_page_to_load() throws Throwable 
	{
		utils.waitForSeconds(2);
	}

	@Then("^I fill in field \"([^\"]*)\" with \"([^\"]*)\"$")
	public void i_fill_in_field_with(String locator, String value) throws Throwable 
	{
		
		utils.fillFieldWith(locator,value);
	}

	@Then("^I press button \"([^\"]*)\"$")
	public void i_press_button(String locator) throws Throwable 
	{
		WebElement ele = utils.getElement(locator);
		if(ele != null)
		{
			utils.clickButton(ele);
		}
		else
		{
			if(LOGGER.isInfoEnabled())
			{
				LOGGER.info("Button " + locator + " does not exisit on page: " + utils.getCurrentUrl());
			}	
		}	
	}

	@Then("^I should see \"([^\"]*)\" on the page$")
	public void i_should_see_on_the_page(String value) throws Throwable 
	{
		Assert.assertTrue(utils.verifyTextExistsOnPage(value));
	}
	
	@Then("^I should see \"([^\"]*)\" link on the page$")
	public void i_should_see_link_on_the_page(String linkText) throws Throwable 
	{
		Assert.assertTrue(utils.verifyLinkExistsOnPage(linkText));
	}
	
	
	@Then("^I store the the details \"([^\"]*)\" to a file \"([^\"]*)\"$")
	public void i_store_the_the_details_to_a_file(String inputText, String fileName) throws Throwable 
	{
		utils.writeToFile(inputText,fileName);
	}
	

	@When("^I click on the element with xpath \"([^\"]*)\"$")
	public void i_click_on_the_element_with_xpath(String xpathLocator) throws Throwable 
	{
		utils.clickOnEelmentUsingXpath(xpathLocator);
	}

	@When("^I save the record for \"([^\"]*)\" applet$")
	public void i_save_the_record_for_applet(String appletName) throws Throwable 
	{
		utils.saveTheAppletRecord(appletName);
	}
	
	@Then("^Field \"([^\"]*)\" attribute \"([^\"]*)\" should contain \"([^\"]*)\"$")
	public void field_attribute_should_contain(String element, String attribute, String value) throws Throwable 
	{
		Assert.assertTrue(utils.verifyElementHasSpecifiedAttributeValue(element,attribute,value));
	}

	/*@Then("^I Should not be able to delete \"([^\"]*)\"$")
	public void i_Should_not_be_able_to_delete(String appletName) throws Throwable 
	{
		Assert.assertTrue(utils.verifyDeleteOptionIsDisabledFor(appletName));
	}*/

	@When("^I select \"([^\"]*)\" from list \"([^\"]*)\"$")
	public void i_select_from_list(String appletName, String menuTitle) throws Throwable 
	{
		utils.selectAppletFromMenu(menuTitle, appletName);
	}

	
	@When("^I click link \"([^\"]*)\" from menu \"([^\"]*)\"$")
	public void i_click_link_from_menu(String action, String menuOption) throws Throwable 
	{
		utils.performActionOnMenu(menuOption,action);
	}

	@When("^the \"([^\"]*)\" field should contain \"([^\"]*)\"$")
	public void the_field_should_contain(String fieldName, String expectedValue) throws Throwable 
	{
		Assert.assertTrue(utils.verifyFieldHasValue(fieldName,expectedValue));
	}
	
/*	@Then("^verify delete menu option is enabled for \"([^\"]*)\"$")
	public void verify_delete_menu_option_is_enabled_for(String appletName) throws Throwable 
	{
		Assert.assertFalse(utils.verifyDeleteButtonIsDisabledFor(appletName));
	}*/

	@Then("^I validate multiple record attribute \"([^\"]*)\" value:$")
	public void i_validate_multiple_record_attribute_value(String attributeName, DataTable testData) throws Throwable 
	{
		Assert.assertTrue(utils.verfiAllFieldsHasExpectedValueForAttribute(attributeName,testData));
	}
	
	@When("^I fill in field \"([^\"]*)\" with stored \"([^\"]*)\"$")
	public void i_fill_in_field_with_stored(String fieldName, String fileName) throws Throwable 
	{
		utils.fillFieldWithStoredValue(fieldName,fileName);
	}
	
	
	@Then("^I validate the popup text \"([^\"]*)\"$")
	public void i_validate_the_popup_text(String popupText) throws Throwable 
	{
		Assert.assertTrue(utils.verifyPopupTextIsCorrectAs(popupText));
	}
	
	@Then("^I store the field \"([^\"]*)\" attribute \"([^\"]*)\" for immediate use$")
	public void i_store_the_field_attribute_for_immediate_use(String elementLocator, String attributeName) throws Throwable 
	{
		utils.storeAttributeValueForImmediateUse(elementLocator,attributeName);
	}

	/*
	@When("^I fill in date field \"([^\"]*)\" with System date plus (\\d+) days without time$")
	public void i_fill_in_date_field_with_System_date_plus_days_without_time(String fieldName, int noOfDays) throws Throwable 
	{
		utils.fillInDateFieldWithDelaysFromSysdate(fieldName,noOfDays);
	}
	*/
	
/*	@Then("^I Should not able to Create \"([^\"]*)\"$")
	public void i_Should_not_able_to_Create(String appletName) throws Throwable 
	{
		Assert.assertTrue(utils.newRecordCreationIsDissabled(appletName));
	}*/
	
	/*
	@When("^I fill in date field \"([^\"]*)\" with System date plus (\\d+) days with time$")
	public void i_fill_in_date_field_with_System_date_plus_days_with_time(String fieldName, int noOfDays) throws Throwable 
	{
		utils.fillInDateFieldWithDelaysFromSysdateWithTime(fieldName,noOfDays);
	}
	*/
	
	@Then("^Field \"([^\"]*)\" attribute \"([^\"]*)\" should contain stored \"([^\"]*)\"$")
	public void field_attribute_should_contain_stored(String element, String attribute, String fileName) throws Throwable 
	{
		Assert.assertTrue(utils.verifyElementHasSpecifiedAttributeStoredValue(element,attribute,fileName));
	}
	
	@Then("^I should not see \"([^\"]*)\"$")
	public void i_should_not_see(String elementName) throws Throwable 
	{
		Assert.assertTrue("Element: " + elementName + " is visible", utils.verifyElementIsNotVisible(elementName));
	}
	
	/*@When("^I should see following values \"([^\"]*)\" in available columns$")
	public void i_should_see_following_values_in_available_columns(String options) throws Throwable 
	{
		Assert.assertTrue(utils.verifyOptionsInAvailableCoulmns(options));
	}*/

	
	/*@When("^I move these values to Selected Columns \"([^\"]*)\" from available columns$")
	public void i_move_these_values_to_Selected_Columns_from_available_columns(String options) throws Throwable 
	{
		utils.moveOptionsToSelectedColumns(options);
	}*/

	@Then("^I should click element \"([^\"]*)\" within menu \"([^\"]*)\"$")
	public void i_should_click_element_within_menu(String elementName, String menuName) throws Throwable 
	{
		utils.clickElementWithinMenu(elementName,menuName);
		utils.waitForSeconds(2);
	}

	@Then("^I drilldown on the hyperlink$")
	public void i_drilldown_on_the_hyperlink() throws Throwable 
	{
		utils.drilldownHyperLink();
	}

	@Then("^verify element \"([^\"]*)\" is enabled$")
	public void verify_element_is_enabled(String locator) throws Throwable 
	{
		Assert.assertTrue(utils.verifyElementIsEnabled(locator));
	}
	
	@Then("^I validate multiple record value:$")
	public void i_validate_multiple_record_value(List<Map<String,String>> inputData) throws Throwable 
	{
		Assert.assertTrue(utils.verifyAllFieldsHasExpectedValues(inputData));
	}
	
	/*
	@Then("^date field \"([^\"]*)\" should contain System date minus (\\d+) days without time$")
	public void date_field_should_contain_System_date_minus_days_without_time(String locator, int delay) throws Throwable 
	{
		Assert.assertTrue(utils.dateDifferenceShouldBe(locator,delay));
	}
	
	
	@Then("^I reset to defaults$")
	public void i_reset_to_defaults() throws Throwable 
	{
		utils.resetDisplayColumnSelectionOption();
	}
	*/
	
	@When("^I fill in field using xpath \"([^\"]*)\" with \"([^\"]*)\"$")
	public void i_fill_in_field_using_xpath_with(String xPath, String value) throws Throwable 
	{
		utils.fillInFieldUsingXpath(xPath,value);
	}

	@When("^I enter multiple selection field \"([^\"]*)\" filter \"([^\"]*)\" with \"([^\"]*)\"$")
	public void i_enter_multiple_selection_field_filter_with(String fieldLocator, String filterType, String value) throws Throwable 
	{
		utils.selectValueFromSelectionPickerUsingFilter(fieldLocator,filterType,value);
	}
	
	/*
	@Then("^I fill in date field \"([^\"]*)\" with System date minus (\\d+) days without time$")
	public void i_fill_in_date_field_with_System_date_minus_days_without_time(String locator, int dateDiff) throws Throwable 
	{
		utils.fillInDateFieldWithDelaysFromSysdate(locator, -dateDiff);
	}
	*/
	
	@When("^I enter multiple selection field \"([^\"]*)\" filter \"([^\"]*)\" with \"([^\"]*)\" and add \"([^\"]*)\"$")
	public void i_enter_multiple_selection_field_filter_with_and_add(String locator, String filterType, String value, String option) throws Throwable 
	{
		utils.selectValueFromSelectionPickerUsingFilterAndAddVauleToSelectedOption(locator, filterType, value, option);
	}
	
	@When("^I enter multiple selection field \"([^\"]*)\" without filter \"([^\"]*)\" with \"([^\"]*)\"$")
	public void i_enter_multiple_selection_field_without_filter_with(String fieldLocator, String field, String value) throws Throwable 
	{
		utils.selectValueFromSelectionPickerWithoutUsingFilter(fieldLocator,field,value);
	}
	
	/*
	@Then("^I fill in date field \"([^\"]*)\" with System date minus (\\d+) days with time$")
	public void i_fill_in_date_field_with_System_date_minus_days_with_time(String locator, int delay) throws Throwable 
	{
		utils.fillInDateFieldWithDelaysFromSysdateWithTime(locator, -delay);
	}
*/
	@Then("^I validate the popup test contains \"([^\"]*)\"$")
	public void i_validate_the_popup_test_contains(String message) throws Throwable 
	{
		 Assert.assertTrue(utils.verifyPopupTextContainsMessage(message));
	}	
	
}

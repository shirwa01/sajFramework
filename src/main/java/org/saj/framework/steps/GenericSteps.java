package org.saj.framework.steps;

import org.junit.Assert;
import org.saj.framework.GenericUtils;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GenericSteps 
{
	private GenericUtils utils;
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

	
	
}

Feature: Generic web steps
	As a tester I want to write BDD scenarios so that I can run them automatically.

@sanity
Scenario: To find all active links on page
Given I am on "https://www.google.com"



@wip
Scenario Outline: Verify all links on the page are active
Given I am on "http://www.seleniumhq.org/"
When I follow "<link name>"
Then The page url should be "<url>"


Examples:
    | link name  |url |
    | Selenium WebDriver |  http://www.seleniumhq.org/projects/webdriver/  |
    |  Selenium Remote Control |  http://www.seleniumhq.org/projects/remote-control/   |

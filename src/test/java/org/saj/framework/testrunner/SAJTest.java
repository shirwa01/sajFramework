package org.saj.framework.testrunner;

import com.github.mkolisnyk.cucumber.runner.ExtendedCucumberOptions;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@ExtendedCucumberOptions(jsonReport = "target/cucumber.json",
retryCount = 0,
detailedReport = true,
detailedAggregatedReport = true,
overviewReport = true,
coverageReport = true,
jsonUsageReport = "target/cucumber-usage.json",
usageReport = true,
toPDF = false,
includeCoverageTags = {"@passed" },
outputFolder = "target")
@CucumberOptions(plugin = { "html:target/cucumber-html-reports",
"json:target/cucumber.json", "pretty:target/cucumber-pretty.txt",
"usage:target/cucumber-usage.json", "junit:target/cucumber-results.xml" },
features="classpath:features",
glue = "classpath:",
tags={"@wip"}
)


public class SAJTest extends AbstractTestNGCucumberTests 
{

}

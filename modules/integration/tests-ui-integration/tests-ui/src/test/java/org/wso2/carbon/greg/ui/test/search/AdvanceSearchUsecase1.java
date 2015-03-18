/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.greg.ui.test.search;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.greg.integration.common.ui.page.LoginPage;
import org.wso2.greg.integration.common.ui.page.main.HomePage;
import org.wso2.greg.integration.common.utils.GREGIntegrationUIBaseTest;

/**
 * Use-case 1
 * This test case Search for Registry resource having a media type and a pre-defined Indexer.
 * Add a Service called myService.
 *      media type	= application/vnd.wso2-service+xml
 *      Indexer	= RXTIndexer
 * Search for the service
 */
public class AdvanceSearchUsecase1 extends GREGIntegrationUIBaseTest {

    private static final Log log = LogFactory.getLog(AdvanceSearchUsecase1.class);
    private WebDriver driver;
    private User userInfo;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        userInfo = automationContext.getContextTenant().getContextUser();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
    }

    @Test(groups = "wso2.greg",
            description = "Verify Solr indexing and advance search of resources having having a media type and a " +
                    "pre-defined Indexer")
    public void test() throws Exception {
        // Login to server
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(userInfo.getUserName(), userInfo.getPassword());
        // Add service
        driver.findElement(By.linkText("Service")).click();
        driver.findElement(By.id("id_Overview_Name")).sendKeys("myService");
        driver.findElement(By.id("id_Overview_Namespace")).sendKeys("org.wso2.greg");
        driver.findElement(By.id("id_Overview_Version")).sendKeys("1.0.0");
        driver.findElement(By.xpath("//input[contains(@class,'button registryWriteOperation')]")).click();
        // This is because indexing process executes in every 5 secs times. Sleep for few secs to run the indexing
        // process as advance search is based on solr based indexing and we need this service to be indexed in-order
        // to search.
        Thread.sleep(40000);
        // Search for service added
        driver.findElement(By.linkText("Search")).click();
        driver.findElement(By.id("#_resourceName")).sendKeys("myService");
        driver.findElement(By.xpath("//input[contains(@value,'Search')]")).click();
        Thread.sleep(10000);
        driver.findElement(By.xpath("//a[contains(.,'/_system/governance/trunk/services/greg/wso2/org/1.0.0/myService\n"
                + "                ')]")).click();
        driver.close();
    }

    @AfterClass(alwaysRun = true, groups = { "wso2.greg" })
    public void tearDown() throws RegistryException, AxisFault {
        driver.quit();
    }
}

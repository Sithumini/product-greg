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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
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
 * Use-case 3
 * This test case Search for Registry resource by content.
 * Add a second resource to the collection myCollection.
 *      name		= myResource
 *      media type  = text/plain
 *      Indexer 	= PlainTextIndexer
 * Add content to the resources.
 * Search for the content.
 */
public class AdvanceSearchUsecase3 extends GREGIntegrationUIBaseTest {

    private static final Log log = LogFactory.getLog(AdvanceSearchUsecase2.class);
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
            description = "Verify Solr indexing and advance search of resources having no media type")
    public void test() throws Exception {
        // Login to server
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.loginAs(userInfo.getUserName(), userInfo.getPassword());

        // Browse till governance/trunk
        driver.findElement(By.linkText("Browse")).click();
        driver.findElement(By.xpath("//img[contains(@id,'plus_treeViewRoot_0')]")).click();
        driver.findElement(By.xpath("//img[contains(@id,'plus_treeViewRoot_0_1')]")).click();
        driver.findElement(By.xpath("//a[contains(@title,'/_system/governance/trunk')]")).click();
        driver.findElement(By.xpath("//a[contains(@class,'add-collection-icon-link registryWriteOperation')]")).click();

        // Add the collection
        driver.findElement(By.id("collectionName")).sendKeys("myCollection");
        driver.findElement(By.xpath("//input[contains(@onclick,'submitCollectionAddForm()')]")).click();
        driver.findElement(By.xpath("//button[contains(.,'OK')]")).click();

        // Add a resource to the collection added.
        driver.findElement(By.xpath("//a[contains(@class,'add-resource-icon-link registryWriteOperation')]")).click();
        // Select the 'Create Text content'
        WebElement dropDown = driver.findElement(By.id("addMethodSelector"));
        Select clickThis = new Select(dropDown);
        clickThis.selectByVisibleText("Create Text content");
        // Add resource details
        driver.findElement(By.id("trFileName")).sendKeys("myResource");
        driver.findElement(By.id("trPlainContent")).sendKeys("myContentText");
        driver.findElement(By.xpath("//input[contains(@onclick,'whileUpload();submitTextContentForm();')]")).click();
        driver.findElement(By.xpath("//button[contains(.,'OK')]")).click();
        // This is because indexing process executes in every 5 secs times. Sleep for few secs to run the indexing
        // process as advance search is based on solr based indexing and we need this collection and resource to be
        // indexed in-order to search.
        Thread.sleep(40000);

        // Search for collection added
        driver.findElement(By.linkText("Search")).click();
        driver.findElement(By.id("#_content")).sendKeys("myContentText");
        Thread.sleep(10000);
        driver.findElement(By.xpath("//input[contains(@value,'Search')]")).click();
        Thread.sleep(10000);
        driver.findElement(By.xpath("//a[contains(.,'/_system/governance/trunk/myCollection/myResource\n"
                + "                ')]")).click();
        driver.close();
    }

    @AfterClass(alwaysRun = true, groups = { "wso2.greg" })
    public void tearDown() throws RegistryException, AxisFault {
        driver.quit();
    }

}

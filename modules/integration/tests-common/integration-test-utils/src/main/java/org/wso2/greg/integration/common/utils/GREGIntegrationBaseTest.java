/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.greg.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.registry.search.stub.beans.xsd.AdvancedSearchResultsBean;
import org.wso2.carbon.registry.search.stub.common.xsd.ResourceData;

import javax.xml.xpath.XPathExpressionException;

/**
 * Base class of all integration tests
 */
public class GREGIntegrationBaseTest {

    protected Log log = LogFactory.getLog(GREGIntegrationBaseTest.class);
    protected AutomationContext automationContext;
    protected String backendURL;

    protected void init(TestUserMode userMode) throws Exception {
        automationContext = new AutomationContext("GREG", userMode);
        backendURL = automationContext.getContextUrls().getBackEndUrl();
    }

    protected void initPublisher(String productGroupName, String instanceName, TestUserMode userMode, String userKey)
            throws XPathExpressionException {
        automationContext = new AutomationContext(productGroupName, instanceName, userMode);
        backendURL = automationContext.getContextUrls().getBackEndUrl();
    }

    protected String getBackendURL() throws XPathExpressionException {
        return automationContext.getContextUrls().getBackEndUrl();
    }

    protected String getSessionCookie() throws Exception {
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(automationContext);
        return loginLogoutClient.login();
    }

    protected String getServiceURL() throws XPathExpressionException {
        return automationContext.getContextUrls().getServiceUrl();
    }

    protected String getTestArtifactLocation() {
        return FrameworkPathUtil.getSystemResourceLocation();
    }

    protected boolean isEmptyResourceDataList(AdvancedSearchResultsBean result) {
        boolean resultEmpty = true;
        if ((result.getResourceDataList().length > 0)) {
            for (ResourceData data : result.getResourceDataList()) {
                if (data != null) {
                    resultEmpty = false;
                    break;
                }
            }
        }
        return resultEmpty;
    }
}



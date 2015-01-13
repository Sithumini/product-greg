/*
*​Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.​
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.registry.samples.taggingHandler;

import org.apache.axiom.om.*;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.Handler;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.registry.extensions.utils.CommonConstants;
import org.wso2.carbon.registry.extensions.utils.CommonUtil;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.StringReader;

/**
 * The idea of this sample is to apply namespace as a Tag while creating the service.
 * At the time of service creation this Handler class will get hit and will apply the
 * namespace as a Tag into the service.
 */
public class CustomTagServiceHandler extends Handler {

    public void put(RequestContext requestContext) throws RegistryException {

        if (!CommonUtil.isUpdateLockAvailable()) {
            return;
        }
        CommonUtil.acquireUpdateLock();
        try {

            String resourceContent;
            String resourcePath = requestContext.getResourcePath().getPath();
            OMElement resourceElement;

            /** derive registry resource content */
            Object resourceContentObj = requestContext.getResource().getContent();
            if (resourceContentObj instanceof String) {
                resourceContent = (String) resourceContentObj;
            } else {
                resourceContent = new String((byte[]) resourceContentObj);
            }

            try {
                /** Initialize XMLInputFactory */
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                /** Reading xml content from string resourceContent and creating XMLStreamReader */
                XMLStreamReader reader = inputFactory.
                        createXMLStreamReader(new StringReader(resourceContent));
                StAXOMBuilder builder = new StAXOMBuilder(reader);
                resourceElement = builder.getDocumentElement();
            } catch (XMLStreamException e) {
                String msg = "An error occurred " +
                        "while reading the resource at " + resourcePath + ".";
                throw new RegistryException(msg, e);
            }

            /** get the first OMElement child with name 'overview' */
            OMElement elementOverview = resourceElement
                    .getFirstChildWithName(new QName(CommonConstants.SERVICE_ELEMENT_NAMESPACE, "overview"));
            /** get the first OMElement child with name 'namespace' */
            String namespace = elementOverview
                    .getFirstChildWithName(new QName(CommonConstants.SERVICE_ELEMENT_NAMESPACE, "namespace")).getText();

            Registry registry = requestContext.getRegistry();
            /** add the resource namespace as the tag */
            registry.applyTag(resourcePath, namespace);

        } finally {
            CommonUtil.releaseUpdateLock();
        }
    }

}
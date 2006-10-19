// Copyright 2006 Google Inc.  All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.servlet;

import com.google.enterprise.connector.common.StringUtils;
import com.google.enterprise.connector.manager.Manager;
import com.google.enterprise.connector.manager.MockManager;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * Tests the Authorization servlet class
 *
 */
public class AuthorizationTest extends TestCase {
  private static final Logger LOG =
    Logger.getLogger(AuthorizationTest.class.getName());

  /**
   * Test method for
   * {@link com.google.enterprise.connector.servlet.Authorization#handleDoPost(
   * java.lang.String, com.google.enterprise.connector.manager.Manager)}.
   */
  public void testHandleDoPost1() {
    String xmlBody =
        "<AuthorizationQuery>\n" + 
        "<ConnectorQuery>\n" + 
        "  <Identity source=\"gsa\">CN=foo</Identity>\n" + 
        "  <Resource>googleconnector://connector1/?" + ServletUtil.DOCID +
        "foo1</Resource>\n" + 
        "  <Resource>googleconnector://connector2/?" + ServletUtil.DOCID +
        "foo2</Resource>\n" + 
        "</ConnectorQuery>\n" + 
        "<ConnectorQuery>\n" + 
        "  <Identity source=\"connector\">username</Identity>\n" + 
        "  <Resource>googleconnector://connector3/?" + ServletUtil.DOCID +
        "foo3</Resource>\n" + 
        "</ConnectorQuery>\n" + 
        "</AuthorizationQuery>";

    String expectedResult =
    	"<CmResponse>\n" +
    	"  <AuthorizationResponse>\n" + 
        "    <Answer>\n" + 
        "      <Resource>googleconnector://connector1/?" + ServletUtil.DOCID +
        "foo1</Resource>\n" + 
        "      <Decision>Permit</Decision>\n" + 
        "    </Answer>\n" + 
        "    <Answer>\n" + 
        "      <Resource>googleconnector://connector2/?" + ServletUtil.DOCID +
        "foo2</Resource>\n" + 
        "      <Decision>Permit</Decision>\n" + 
        "    </Answer>\n" + 
        "    <Answer>\n" + 
        "      <Resource>googleconnector://connector3/?" + ServletUtil.DOCID +
        "foo3</Resource>\n" + 
        "      <Decision>Permit</Decision>\n" + 
        "    </Answer>\n" + 
        "  </AuthorizationResponse>\n" +
        "  <StatusId>0</StatusId>\n" +
        "</CmResponse>\n";
    doTest(xmlBody, expectedResult);
  }

  /**
   * Test method for
   * {@link com.google.enterprise.connector.servlet.Authorization#handleDoPost(
   * java.lang.String, com.google.enterprise.connector.manager.Manager)}.
   * 
   * The connector name is null.
   */
  public void testHandleDoPost2() {
    String xmlBody =
        "<AuthorizationQuery>\n" + 
        "<ConnectorQuery>\n" + 
        "  <Identity source=\"gsa\">CN=foo</Identity>\n" + 
        "  <Resource>googleconnector:///?" + ServletUtil.DOCID + "foo1</Resource>\n" + 
        "</ConnectorQuery>\n" + 
        "</AuthorizationQuery>";

    String expectedResult =
        "<CmResponse>\n" + 
        "  <StatusId>Null connector name</StatusId>\n" +
        "</CmResponse>\n";
    doTest(xmlBody, expectedResult);
  }

  /**
   * Test method for
   * {@link com.google.enterprise.connector.servlet.Authorization#handleDoPost(
   * java.lang.String, com.google.enterprise.connector.manager.Manager)}.
   * 
   * docid does not exist.
   */
  public void testHandleDoPost3() {
    String xmlBody =
        "<AuthorizationQuery>\n" + 
        "<ConnectorQuery>\n" + 
        "  <Identity source=\"gsa\">CN=foo</Identity>\n" + 
        "  <Resource>googleconnector://Connector3/?DOCID=foo1</Resource>\n" + 
        "</ConnectorQuery>\n" + 
        "</AuthorizationQuery>";

    String expectedResult =
        "<CmResponse>\n" + 
        "  <StatusId>Null doc ID</StatusId>\n" +
        "</CmResponse>\n";
    doTest(xmlBody, expectedResult);
  }

  /**
   * Test method for
   * {@link com.google.enterprise.connector.servlet.Authorization#handleDoPost(
   * java.lang.String, com.google.enterprise.connector.manager.Manager)}.
   * 
   * The identity is null (empty).
   * 
   */
  public void testHandleDoPost4() {
    String xmlBody =
        "<AuthorizationQuery>\n" + 
        "<ConnectorQuery>\n" + 
        "  <Identity source=\"gsa\"></Identity>\n" + 
        "  <Resource>googleconnector://connector1/?" + ServletUtil.DOCID +
        "foo1</Resource>\n" + 
        "</ConnectorQuery>\n" + 
        "</AuthorizationQuery>";

    String expectedResult =
    	"<CmResponse>\n" +
    	"  <AuthorizationResponse>\n" + 
        "    <Answer>\n" + 
        "      <Resource>googleconnector://connector1/?" + ServletUtil.DOCID +
        "foo1</Resource>\n" + 
        "      <Decision>Permit</Decision>\n" + 
        "    </Answer>\n" + 
        "  </AuthorizationResponse>\n" +
        "  <StatusId>0</StatusId>\n" +
        "</CmResponse>\n";
    doTest(xmlBody, expectedResult);
  }

  /**
   * Test method for
   * {@link com.google.enterprise.connector.servlet.Authorization#handleDoPost(
   * java.lang.String, com.google.enterprise.connector.manager.Manager)}.
   * 
   * The resource is null.
   */
  public void testHandleDoPost5() {
	    String xmlBody =
	        "<AuthorizationQuery>\n" + 
	        "<ConnectorQuery>\n" + 
	        "  <Identity source=\"gsa\">username</Identity>\n" + 
	        "</ConnectorQuery>\n" + 
	        "</AuthorizationQuery>";

	    String expectedResult =
	        "<CmResponse>\n" + 
	        "  <StatusId>Null resource</StatusId>\n" +
	        "</CmResponse>\n";
	    doTest(xmlBody, expectedResult);
  }

  private void doTest(String xmlBody, String expectedResult) {
    LOG.info("xmlBody: " + xmlBody);
    Manager manager = MockManager.getInstance();
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);
    Authorization.handleDoPost(out, xmlBody, manager);
    out.flush();
    StringBuffer result = writer.getBuffer();
    LOG.info(result.toString());
    LOG.info(expectedResult);
    Assert.assertEquals (StringUtils.normalizeNewlines(expectedResult), 
        StringUtils.normalizeNewlines(result.toString()));
    out.close();
  }
}

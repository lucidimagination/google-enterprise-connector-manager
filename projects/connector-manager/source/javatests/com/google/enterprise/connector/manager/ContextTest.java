// Copyright (C) 2006 Google Inc.
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

package com.google.enterprise.connector.manager;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;

/**
 * 
 */
public class ContextTest extends TestCase {

  /**
   * Test method for
   * {@link com.google.enterprise.connector.manager.Context#start()}.
   */
  public final void testStart() {
    Context.getInstance().setJunitContext();
    Context.getInstance().start();
    ApplicationContext ac = Context.getInstance().getApplicationContext();
    printBeanNames(ac);
    try {
      Thread.sleep(200);
    } catch (InterruptedException ie) {
      ie.printStackTrace();
      Assert.fail(ie.toString());
    }
    Context.getInstance().shutdown(false);
  }

  private void printBeanNames(ApplicationContext ac) {
    String[] beanList = ac.getBeanDefinitionNames();
    String contextName = ac.getDisplayName();
    System.out.println("Beans application context: " + contextName);
    for (int i = 0; i < beanList.length; i++) {
      Object bean = ac.getBean(beanList[i]);
      System.out.println(beanList[i] + " " + bean.getClass().toString());
    }
  }

}

package com.google.enterprise.connector.servlet;

import com.google.enterprise.connector.instantiator.InstantiatorException;

import com.google.enterprise.connector.logging.NDC;
import com.google.enterprise.connector.manager.Manager;
import com.google.enterprise.connector.persist.ConnectorNotFoundException;
import com.google.enterprise.connector.persist.PersistentStoreException;
import com.google.enterprise.connector.scheduler.Schedule;
import com.google.enterprise.connector.util.XmlParseUtil;

import org.w3c.dom.Element;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Admin servlet for StopTraversal
 *
 */
public class StopTraversal extends ConnectorManagerGetServlet {
  private static final Logger LOGGER =
      Logger.getLogger(RestartConnectorTraversal.class.getName());

  @Override
  protected void processDoGet(String connectorName, String lang,
      Manager manager, PrintWriter out) {
    handleDoGet(connectorName, manager, out);
  }

  /**
   * Handler for doGet in order to do unit tests. Returns the connector
   * status.
   *
   * @param connectorName
   * @param manager
   * @param out PrintWriter where the response is written
   */
  public static void handleDoGet(String connectorName, Manager manager,
      PrintWriter out) {
    try {
      // Force the connector to stop the traversal.
      manager.stopTraversal(connectorName);
    } catch (ConnectorNotFoundException cnfe) {
      ServletUtil.writeResponse(out, new ConnectorMessageCode(
          ConnectorMessageCode.EXCEPTION_CONNECTOR_NOT_FOUND, connectorName));
      LOGGER.warning("Connector not found " + connectorName);
      return;
    } catch (Exception ie) {
      ServletUtil.writeResponse(out, new ConnectorMessageCode(
          ConnectorMessageCode.EXCEPTION_INSTANTIATOR, connectorName));
      LOGGER.warning(ie.getMessage());
      return;
    }

    // Write out the successful status.
    ServletUtil.writeResponse(out, 0);
  }
}

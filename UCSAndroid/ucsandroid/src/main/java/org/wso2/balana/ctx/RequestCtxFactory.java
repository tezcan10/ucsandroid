package org.wso2.balana.ctx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.xacml3.RequestCtx;

import javax.xml.parsers.DocumentBuilderFactory;

public class RequestCtxFactory {
  private static volatile RequestCtxFactory factoryInstance;
  private static Log log = LogFactory.getLog(RequestCtxFactory.class);

  public RequestCtxFactory() {
  }

  public AbstractRequestCtx getRequestCtx(Node root) throws ParsingException {
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null) {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return RequestCtx.getInstance(root);
      } else if (!"urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim()) && !"urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim())) {
        throw new ParsingException("Invalid namespace in XACML request");
      } else {
        return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
      }
    } else {
      log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
      return RequestCtx.getInstance(root);
    }
  }

  public AbstractRequestCtx getRequestCtx(String request) throws ParsingException {
    Node root = this.getXacmlRequest(request);
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null) {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return RequestCtx.getInstance(root);
      } else if (!"urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim()) && !"urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim())) {
        throw new ParsingException("Invalid namespace in XACML request");
      } else {
        return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
      }
    } else {
      log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
      return RequestCtx.getInstance(root);
    }
  }

  public AbstractRequestCtx getRequestCtx(InputStream input) throws ParsingException {
    Node root = InputParser.parseInput(input, "Request");
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null) {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return RequestCtx.getInstance(root);
      } else if (!"urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim()) && !"urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim())) {
        throw new ParsingException("Invalid namespace in XACML request");
      } else {
        return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
      }
    } else {
      log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
      return RequestCtx.getInstance(root);
    }
  }

  public static RequestCtxFactory getFactory() {
    if (factoryInstance == null) {
      Class var0 = RequestCtxFactory.class;
      synchronized(RequestCtxFactory.class) {
        if (factoryInstance == null) {
          factoryInstance = new RequestCtxFactory();
        }
      }
    }

    return factoryInstance;
  }

  public Element getXacmlRequest(String request) throws ParsingException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);

    Document doc;
    try {
      doc = dbf.newDocumentBuilder().parse(inputStream);
    } catch (Exception var13) {
      throw new ParsingException("DOM of request element can not be created from String");
    } finally {
      try {
        inputStream.close();
      } catch (IOException var12) {
        log.error("Error in closing input stream of XACML request");
      }

    }

    return doc.getDocumentElement();
  }
}


/* Location:
 * Qualified Name:     org.wso2.balana.ctx.RequestCtxFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
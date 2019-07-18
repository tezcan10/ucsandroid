package org.wso2.balana.finder.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Balana;
import org.wso2.balana.DefaultNamespaceContext;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.Utils;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.AttributeFinderModule;

public class SelectorModule
  extends AttributeFinderModule
{
  public boolean isSelectorSupported()
  {
    return true;
  }
  
  public EvaluationResult findAttribute(String contextPath, URI attributeType, String contextSelector, Node root, EvaluationCtx context, String xpathVersion)
  {
    Node contextNode = null;
    if (root == null)
    {
      contextNode = context.getRequestRoot();
    }
    else if (contextSelector != null)
    {
      String namespace = root.getNamespaceURI();
      
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      if (namespace != null)
      {
        NamedNodeMap namedNodeMap = root.getAttributes();
        String prefix = "ns";
        String nodeName = null;
        for (int i = 0; i < namedNodeMap.getLength(); i++)
        {
          Node n = namedNodeMap.item(i);
          if (n.getNodeValue().equals(namespace))
          {
            nodeName = n.getNodeName();
            
            break;
          }
        }
        if (nodeName != null)
        {
          int pos = nodeName.indexOf(':');
          if (pos != -1) {
            prefix = nodeName.substring(pos + 1);
          } else {
            contextSelector = Utils.prepareXPathForDefaultNs(contextSelector);
          }
        }
        else
        {
          contextSelector = Utils.prepareXPathForDefaultNs(contextSelector);
        }
        NamespaceContext namespaceContext = new DefaultNamespaceContext(prefix, namespace);
        
        xpath.setNamespaceContext(namespaceContext);
      }
      try
      {
        XPathExpression expression = xpath.compile(contextSelector);
        NodeList result = (NodeList)expression.evaluate(root, XPathConstants.NODESET);
        if (result == null) {
          throw new Exception("No node is found from context selector id evaluation");
        }
        if (result.getLength() == 1) {
          break label308;
        }
        throw new Exception("More than one node is found from context selector id evaluation");
      }
      catch (Exception e)
      {
        List<String> codes = new ArrayList();
        codes.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
        Status status = new Status(codes, e.getMessage());
        return new EvaluationResult(status);
      }
    }
    else
    {
      contextNode = root;
    }
    label308:
    String namespace = null;
    if (contextNode != null) {
      namespace = contextNode.getNamespaceURI();
    }
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    if (namespace != null)
    {
      NamedNodeMap namedNodeMap = contextNode.getAttributes();
      String prefix = "ns";
      String nodeName = null;
      for (int i = 0; i < namedNodeMap.getLength(); i++)
      {
        Node n = namedNodeMap.item(i);
        if (n.getNodeValue().equals(namespace))
        {
          nodeName = n.getNodeName();
          break;
        }
      }
      if (nodeName != null)
      {
        int pos = nodeName.indexOf(':');
        if (pos != -1) {
          prefix = nodeName.substring(pos + 1);
        } else {
          contextPath = Utils.prepareXPathForDefaultNs(contextPath);
        }
      }
      else
      {
        contextPath = Utils.prepareXPathForDefaultNs(contextPath);
      }
      NamespaceContext namespaceContext = new DefaultNamespaceContext(prefix, namespace);
      
      xpath.setNamespaceContext(namespaceContext);
    }
    try
    {
      XPathExpression expression = xpath.compile(contextPath);
      NodeList matches = (NodeList)expression.evaluate(contextNode, XPathConstants.NODESET);
      if ((matches == null) || (matches.getLength() < 1)) {
        throw new Exception("No node is found from xpath evaluation");
      }
    }
    catch (Exception e)
    {
      List<String> codes = new ArrayList();
      codes.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
      Status status = new Status(codes, e.getMessage());
      return new EvaluationResult(status);
    }
    NodeList matches;
    if (matches.getLength() == 0) {
      return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
    }
    try
    {
      ArrayList<AttributeValue> list = new ArrayList();
      AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
      for (int i = 0; i < matches.getLength(); i++)
      {
        String text = null;
        Node node = matches.item(i);
        short nodeType = node.getNodeType();
        if ((nodeType == 4) || (nodeType == 8) || 
          (nodeType == 3) || (nodeType == 2)) {
          text = node.getNodeValue();
        } else {
          text = node.getFirstChild().getNodeValue();
        }
        list.add(attrFactory.createValue(attributeType, text));
      }
      return new EvaluationResult(new BagAttribute(attributeType, list));
    }
    catch (ParsingException pe)
    {
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      return new EvaluationResult(new Status(code, pe.getMessage()));
    }
    catch (UnknownIdentifierException uie)
    {
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      return new EvaluationResult(new Status(code, "Unknown attribute type : " + attributeType));
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.impl.SelectorModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
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
    public SelectorModule() {
    }

    public boolean isSelectorSupported() {
      return true;
    }

    public EvaluationResult findAttribute(String contextPath, URI attributeType, String contextSelector, Node root, EvaluationCtx context, String xpathVersion) {
      Node contextNode = null;
      DefaultNamespaceContext namespaceContext;
      String namespace;
      XPathFactory factory;
      XPath xpath;
      NamedNodeMap namedNodeMap;
      String prefix;
      String nodeName;
      int i;
      Node text;
      ArrayList list;
      if (root == null) {
        contextNode = context.getRequestRoot();
      } else if (contextSelector != null) {
        namespace = root.getNamespaceURI();
        factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        if (namespace != null) {
          namedNodeMap = root.getAttributes();
          prefix = "ns";
          nodeName = null;

          for(i = 0; i < namedNodeMap.getLength(); ++i) {
            text = namedNodeMap.item(i);
            if (text.getNodeValue().equals(namespace)) {
              nodeName = text.getNodeName();
              break;
            }
          }

          if (nodeName != null) {
            i = nodeName.indexOf(58);
            if (i != -1) {
              prefix = nodeName.substring(i + 1);
            } else {
              contextSelector = Utils.prepareXPathForDefaultNs(contextSelector);
            }
          } else {
            contextSelector = Utils.prepareXPathForDefaultNs(contextSelector);
          }

          namespaceContext = new DefaultNamespaceContext(prefix, namespace);
          xpath.setNamespaceContext(namespaceContext);
        }

        try {
          XPathExpression expression = xpath.compile(contextSelector);
          NodeList result = (NodeList)expression.evaluate(root, XPathConstants.NODESET);
          if (result == null) {
            throw new Exception("No node is found from context selector id evaluation");
          }

          if (result.getLength() != 1) {
            throw new Exception("More than one node is found from context selector id evaluation");
          }
        } catch (Exception var19) {
          list = new ArrayList();
          list.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
          Status status = new Status(list, var19.getMessage());
          return new EvaluationResult(status);
        }
      } else {
        contextNode = root;
      }

      namespace = null;
      if (contextNode != null) {
        namespace = contextNode.getNamespaceURI();
      }

      factory = XPathFactory.newInstance();
      xpath = factory.newXPath();
      if (namespace != null) {
        namedNodeMap = contextNode.getAttributes();
        prefix = "ns";
        nodeName = null;

        for(i = 0; i < namedNodeMap.getLength(); ++i) {
          text = namedNodeMap.item(i);
          if (text.getNodeValue().equals(namespace)) {
            nodeName = text.getNodeName();
            break;
          }
        }

        if (nodeName != null) {
          i = nodeName.indexOf(58);
          if (i != -1) {
            prefix = nodeName.substring(i + 1);
          } else {
            contextPath = Utils.prepareXPathForDefaultNs(contextPath);
          }
        } else {
          contextPath = Utils.prepareXPathForDefaultNs(contextPath);
        }

        namespaceContext = new DefaultNamespaceContext(prefix, namespace);
        xpath.setNamespaceContext(namespaceContext);
      }

      NodeList matches;
      ArrayList code;
      try {
        XPathExpression expression = xpath.compile(contextPath);
        matches = (NodeList)expression.evaluate(contextNode, XPathConstants.NODESET);
        if (matches == null || matches.getLength() < 1) {
          throw new Exception("No node is found from xpath evaluation");
        }
      } catch (Exception var22) {
        code = new ArrayList();
        code.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
        Status status = new Status(code, var22.getMessage());
        return new EvaluationResult(status);
      }

      if (matches.getLength() == 0) {
        return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
      } else {
        try {
          list = new ArrayList();
          AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();

          for(i = 0; i < matches.getLength(); ++i) {
            text = null;
            Node node = matches.item(i);
            short nodeType = node.getNodeType();
            String text1;
            if (nodeType != 4 && nodeType != 8 && nodeType != 3 && nodeType != 2) {
              text1 = node.getFirstChild().getNodeValue();
            } else {
              text1 = node.getNodeValue();
            }

            list.add(attrFactory.createValue(attributeType, text1));
          }

          return new EvaluationResult(new BagAttribute(attributeType, list));
        } catch (ParsingException var20) {
          code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          return new EvaluationResult(new Status(code, var20.getMessage()));
        } catch (UnknownIdentifierException var21) {
          code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          return new EvaluationResult(new Status(code, "Unknown attribute type : " + attributeType));
        }
      }
    }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.impl.SelectorModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
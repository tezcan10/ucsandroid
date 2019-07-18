package org.wso2.balana.attr;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AttributeSelector
  extends AbstractAttributeSelector
{
  private URI type;
  private String contextPath;
  private boolean mustBePresent;
  private String xpathVersion;
  private Node policyRoot;
  private static Log logger = LogFactory.getLog(AttributeSelector.class);
  
  public AttributeSelector(URI type, String contextPath, boolean mustBePresent, String xpathVersion)
  {
    this(type, contextPath, null, mustBePresent, xpathVersion);
  }
  
  public AttributeSelector(URI type, String contextPath, Node policyRoot, boolean mustBePresent, String xpathVersion)
  {
    this.type = type;
    this.contextPath = contextPath;
    this.mustBePresent = mustBePresent;
    this.xpathVersion = xpathVersion;
    this.policyRoot = policyRoot;
  }
  
  /**
   * @deprecated
   */
  public static AttributeSelector getInstance(Node root, String xpathVersion)
    throws ParsingException
  {
    return getInstance(root, new PolicyMetaData("urn:oasis:names:tc:xacml:1.0:policy", 
      xpathVersion));
  }
  
  public static AttributeSelector getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    URI type = null;
    String contextPath = null;
    boolean mustBePresent = false;
    String xpathVersion = metaData.getXPathIdentifier();
    if (xpathVersion == null) {
      throw new ParsingException("An XPathVersion is required for any policies that use selectors");
    }
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      type = new URI(attrs.getNamedItem("DataType").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required DataType attribute in AttributeSelector", 
        e);
    }
    try
    {
      contextPath = attrs.getNamedItem("RequestContextPath").getNodeValue();
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required RequestContextPath attribute in AttributeSelector", 
        e);
    }
    try
    {
      Node node = attrs.getNamedItem("MustBePresent");
      if ((node != null) && 
        (node.getNodeValue().equals("true"))) {
        mustBePresent = true;
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing optional attributes in AttributeSelector", 
        e);
    }
    Node policyRoot = null;
    Node node = root.getParentNode();
    while ((node != null) && (node.getNodeType() == 1))
    {
      policyRoot = node;
      node = node.getParentNode();
    }
    return new AttributeSelector(type, contextPath, policyRoot, mustBePresent, xpathVersion);
  }
  
  public String getContextPath()
  {
    return contextPath;
  }
  
  public boolean returnsBag()
  {
    return true;
  }
  
  /**
   * @deprecated
   */
  public boolean evaluatesToBag()
  {
    return true;
  }
  
  public List getChildren()
  {
    return Collections.EMPTY_LIST;
  }
  
  public EvaluationResult evaluate(EvaluationCtx context)
  {
    EvaluationResult result = context.getAttribute(contextPath, type, null, null, xpathVersion);
    if (!result.indeterminate())
    {
      BagAttribute bag = (BagAttribute)result.getAttributeValue();
      if (bag.isEmpty())
      {
        if (mustBePresent)
        {
          if (logger.isDebugEnabled()) {
            logger.debug("AttributeSelector failed to resolve a value for a required attribute: " + 
              contextPath);
          }
          ArrayList code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:missing-attribute");
          String message = "couldn't resolve XPath expression " + contextPath + 
            " for type " + type.toString();
          return new EvaluationResult(new Status(code, message));
        }
        return result;
      }
      return result;
    }
    return result;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    String tag = "<AttributeSelector RequestContextPath=\"" + contextPath + "\" DataType=\"" + 
      type.toString() + "\"";
    if (mustBePresent) {
      tag = tag + " MustBePresent=\"true\"";
    }
    tag = tag + "/>";
    
    out.println(indent + tag);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeSelector
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
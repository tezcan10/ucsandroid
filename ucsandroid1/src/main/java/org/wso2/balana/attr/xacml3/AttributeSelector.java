package org.wso2.balana.attr.xacml3;

import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.attr.AbstractAttributeSelector;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AttributeSelector
  extends AbstractAttributeSelector
{
  private URI category;
  private URI contextSelectorId;
  private String path;
  private static Log logger = LogFactory.getLog(AttributeSelector.class);
  
  public AttributeSelector(URI category, URI type, URI contextSelectorId, String path, boolean mustBePresent, String xpathVersion)
  {
    this.category = category;
    this.type = type;
    this.contextSelectorId = contextSelectorId;
    this.mustBePresent = mustBePresent;
    this.xpathVersion = xpathVersion;
    this.path = path;
  }
  
  public static AttributeSelector getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    URI category = null;
    URI type = null;
    URI contextSelectorId = null;
    String path = null;
    boolean mustBePresent = false;
    String xpathVersion = metaData.getXPathIdentifier();
    if (xpathVersion == null) {
      throw new ParsingException("An XPathVersion is required for any policies that use selectors");
    }
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      category = new URI(attrs.getNamedItem("Category").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required Category attribute in AttributeSelector", 
        e);
    }
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
      path = attrs.getNamedItem("Path").getNodeValue();
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required Path attribute in AttributeSelector", 
        e);
    }
    try
    {
      String stringValue = attrs.getNamedItem("MustBePresent").getNodeValue();
      mustBePresent = Boolean.parseBoolean(stringValue);
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required MustBePresent attribute in AttributeSelector", 
        e);
    }
    try
    {
      Node node = attrs.getNamedItem("ContextSelectorId");
      if (node != null) {
        contextSelectorId = new URI(node.getNodeValue());
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required MustBePresent attribute in AttributeSelector", 
        e);
    }
    return new AttributeSelector(category, type, contextSelectorId, path, mustBePresent, 
      xpathVersion);
  }
  
  public EvaluationResult evaluate(EvaluationCtx context)
  {
    EvaluationResult result = context.getAttribute(path, type, category, 
      contextSelectorId, xpathVersion);
    if (!result.indeterminate())
    {
      BagAttribute bag = (BagAttribute)result.getAttributeValue();
      if (bag.isEmpty())
      {
        if (mustBePresent)
        {
          if (logger.isDebugEnabled()) {
            logger.debug("AttributeSelector failed to resolve a value for a required attribute: " + 
              path);
          }
          ArrayList code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:missing-attribute");
          String message = "couldn't resolve XPath expression " + path + 
            " for type " + type.toString();
          return new EvaluationResult(new Status(code, message));
        }
        return result;
      }
      return result;
    }
    return result;
  }
  
  public boolean evaluatesToBag()
  {
    return true;
  }
  
  public List getChildren()
  {
    return null;
  }
  
  public boolean returnsBag()
  {
    return true;
  }
  
  public void encode(OutputStream output) {}
  
  public void encode(OutputStream output, Indenter indenter) {}
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.xacml3.AttributeSelector
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
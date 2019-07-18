package org.wso2.balana.attr.xacml3;

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
import org.wso2.balana.attr.AbstractDesignator;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AttributeDesignator
  extends AbstractDesignator
{
  private URI type;
  private URI id;
  private String issuer;
  private boolean mustBePresent;
  private URI category;
  private static Log logger = LogFactory.getLog(AttributeDesignator.class);
  
  public AttributeDesignator(URI type, URI id, boolean mustBePresent, URI category)
  {
    this(type, id, mustBePresent, null, category);
  }
  
  public AttributeDesignator(URI type, URI id, boolean mustBePresent, String issuer, URI category)
    throws IllegalArgumentException
  {
    this.type = type;
    this.id = id;
    this.mustBePresent = mustBePresent;
    this.issuer = issuer;
    this.category = category;
  }
  
  public static AttributeDesignator getInstance(Node root)
    throws ParsingException
  {
    URI type = null;
    URI id = null;
    String issuer = null;
    URI category = null;
    boolean mustBePresent = false;
    
    String tagName = root.getNodeName();
    if (!tagName.equals("AttributeDesignator")) {
      throw new ParsingException("AttributeDesignator cannot be constructed using type: " + 
        root.getNodeName());
    }
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Required AttributeId missing in AttributeDesignator", e);
    }
    try
    {
      category = new URI(attrs.getNamedItem("Category").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Required Category missing in AttributeDesignator", e);
    }
    try
    {
      String nodeValue = attrs.getNamedItem("MustBePresent").getNodeValue();
      if ("true".equals(nodeValue)) {
        mustBePresent = true;
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Required MustBePresent missing in AttributeDesignator", e);
    }
    try
    {
      type = new URI(attrs.getNamedItem("DataType").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Required DataType missing in AttributeDesignator", e);
    }
    try
    {
      Node node = attrs.getNamedItem("Issuer");
      if (node != null) {
        issuer = node.getNodeValue();
      }
    }
    catch (Exception e)
    {
      throw new ParsingException(
        "Error parsing AttributeDesignator optional attributes", e);
    }
    return new AttributeDesignator(type, id, mustBePresent, issuer, category);
  }
  
  public URI getType()
  {
    return type;
  }
  
  public URI getId()
  {
    return id;
  }
  
  public URI getCategory()
  {
    return category;
  }
  
  public String getIssuer()
  {
    return issuer;
  }
  
  public boolean mustBePresent()
  {
    return mustBePresent;
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
    EvaluationResult result = null;
    
    result = context.getAttribute(type, id, issuer, category);
    if (result.indeterminate()) {
      return result;
    }
    BagAttribute bag = (BagAttribute)result.getAttributeValue();
    if (bag.isEmpty()) {
      if (mustBePresent)
      {
        if (logger.isDebugEnabled()) {
          logger.debug("AttributeDesignator failed to resolve a value for a required attribute: " + 
            id.toString());
        }
        ArrayList<String> code = new ArrayList();
        code.add("urn:oasis:names:tc:xacml:1.0:status:missing-attribute");
        
        String message = "Couldn't find AttributeDesignator attribute";
        
        return new EvaluationResult(new Status(code, message));
      }
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
    
    String tag = "<AttributeDesignator";
    
    tag = tag + " AttributeId=\"" + id.toString() + "\"";
    tag = tag + " DataType=\"" + type.toString() + "\"";
    tag = tag + " Category=\"" + category.toString() + "\"";
    if (issuer != null) {
      tag = tag + " Issuer=\"" + issuer + "\"";
    }
    if (mustBePresent) {
      tag = tag + " MustBePresent=\"true\"";
    } else {
      tag = tag + " MustBePresent=\"false\"";
    }
    tag = tag + "/>";
    
    out.println(indent + tag);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.xacml3.AttributeDesignator
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
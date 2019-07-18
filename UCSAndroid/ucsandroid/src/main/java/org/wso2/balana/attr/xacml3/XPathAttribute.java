package org.wso2.balana.attr.xacml3;

import java.net.URI;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;

public class XPathAttribute
  extends AttributeValue
{
  public static final String identifier = "urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private String value;
  private String xPathCategory;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public XPathAttribute(String value, String xPathCategory)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    if (value == null) {
      this.value = "";
    } else {
      this.value = value;
    }
    if (xPathCategory == null) {
      this.xPathCategory = "";
    } else {
      this.xPathCategory = xPathCategory;
    }
  }
  
  public static XPathAttribute getInstance(Node root)
  {
    String xPathCategory = null;
    
    NamedNodeMap nodeMap = root.getAttributes();
    if (nodeMap != null)
    {
      Node categoryNode = nodeMap.getNamedItem("XPathCategory");
      xPathCategory = categoryNode.getNodeValue();
    }
    return getInstance(root.getFirstChild().getNodeValue(), xPathCategory);
  }
  
  public static XPathAttribute getInstance(String value, String xPathCategory)
  {
    return new XPathAttribute(value, xPathCategory);
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String getXPathCategory()
  {
    return xPathCategory;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof XPathAttribute)) {
      return false;
    }
    XPathAttribute other = (XPathAttribute)o;
    
    return (value.equals(value)) && (xPathCategory.equals(xPathCategory));
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String encode()
  {
    return value;
  }
  
  public String encodeWithTags(boolean includeType)
  {
    if ((includeType) && (getType() != null)) {
      return 
      
        "<AttributeValue DataType=\"" + getType().toString() + "XPathCategory=\"" + getXPathCategory() + "\">" + encode() + "</AttributeValue>";
    }
    return "<AttributeValue>" + encode() + "</AttributeValue>";
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.xacml3.XPathAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr;

import java.net.URI;
import org.w3c.dom.Node;

public class StringAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#string";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private String value;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#string");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public StringAttribute(String value)
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
  }
  
  public static StringAttribute getInstance(Node root)
  {
    Node node = root.getFirstChild();
    if (node == null) {
      return new StringAttribute("");
    }
    short type = node.getNodeType();
    if ((type == 3) || (type == 4) || 
      (type == 8)) {
      return getInstance(node.getNodeValue());
    }
    return null;
  }
  
  public static StringAttribute getInstance(String value)
  {
    return new StringAttribute(value);
  }
  
  public String getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof StringAttribute)) {
      return false;
    }
    StringAttribute other = (StringAttribute)o;
    
    return value.equals(value);
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String toString()
  {
    return "StringAttribute: \"" + value + "\"";
  }
  
  public String encode()
  {
    return value;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.StringAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
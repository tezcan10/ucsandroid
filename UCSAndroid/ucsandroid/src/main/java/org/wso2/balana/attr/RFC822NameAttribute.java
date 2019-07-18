package org.wso2.balana.attr;

import java.net.URI;
import org.w3c.dom.Node;

public class RFC822NameAttribute
  extends AttributeValue
{
  public static final String identifier = "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private String value;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public RFC822NameAttribute(String value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    String[] parts = value.split("@");
    if (parts.length != 2) {
      throw new IllegalArgumentException("invalid RFC822Name: " + value);
    }
    this.value = (parts[0] + "@" + parts[1].toLowerCase());
  }
  
  public static RFC822NameAttribute getInstance(Node root)
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static RFC822NameAttribute getInstance(String value)
  {
    return new RFC822NameAttribute(value);
  }
  
  public String getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof RFC822NameAttribute)) {
      return false;
    }
    RFC822NameAttribute other = (RFC822NameAttribute)o;
    
    return value.equals(value);
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String encode()
  {
    return value;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.RFC822NameAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
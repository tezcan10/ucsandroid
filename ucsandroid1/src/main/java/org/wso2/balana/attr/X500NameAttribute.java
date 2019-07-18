package org.wso2.balana.attr;

import java.net.URI;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Node;

public class X500NameAttribute
  extends AttributeValue
{
  public static final String identifier = "urn:oasis:names:tc:xacml:1.0:data-type:x500Name";
  private X500Principal value;
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.0:data-type:x500Name");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public X500NameAttribute(X500Principal value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.value = value;
  }
  
  public static X500NameAttribute getInstance(Node root)
    throws IllegalArgumentException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static X500NameAttribute getInstance(String value)
    throws IllegalArgumentException
  {
    return new X500NameAttribute(new X500Principal(value));
  }
  
  public X500Principal getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof X500NameAttribute)) {
      return false;
    }
    X500NameAttribute other = (X500NameAttribute)o;
    
    return value.equals(value);
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String encode()
  {
    return value.getName();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.X500NameAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
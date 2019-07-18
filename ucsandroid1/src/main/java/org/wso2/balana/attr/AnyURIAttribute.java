package org.wso2.balana.attr;

import java.net.URI;
import java.net.URISyntaxException;
import org.w3c.dom.Node;

public class AnyURIAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#anyURI";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private URI value;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#anyURI");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public AnyURIAttribute(URI value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.value = value;
  }
  
  public static AnyURIAttribute getInstance(Node root)
    throws URISyntaxException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static AnyURIAttribute getInstance(String value)
    throws URISyntaxException
  {
    return new AnyURIAttribute(new URI(value));
  }
  
  public URI getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof AnyURIAttribute)) {
      return false;
    }
    AnyURIAttribute other = (AnyURIAttribute)o;
    
    return value.equals(value);
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String toString()
  {
    return "AnyURIAttribute: \"" + value.toString() + "\"";
  }
  
  public String encode()
  {
    return value.toString();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AnyURIAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
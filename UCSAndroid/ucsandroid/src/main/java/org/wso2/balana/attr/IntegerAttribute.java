package org.wso2.balana.attr;

import java.net.URI;
import org.w3c.dom.Node;

public class IntegerAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#integer";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private long value;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#integer");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public IntegerAttribute(long value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.value = value;
  }
  
  public static IntegerAttribute getInstance(Node root)
    throws NumberFormatException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static IntegerAttribute getInstance(String value)
    throws NumberFormatException
  {
    if ((value.length() >= 1) && (value.charAt(0) == '+')) {
      value = value.substring(1);
    }
    return new IntegerAttribute(Long.parseLong(value));
  }
  
  public long getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof IntegerAttribute)) {
      return false;
    }
    IntegerAttribute other = (IntegerAttribute)o;
    
    return value == value;
  }
  
  public int hashCode()
  {
    return (int)value;
  }
  
  public String encode()
  {
    return String.valueOf(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.IntegerAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
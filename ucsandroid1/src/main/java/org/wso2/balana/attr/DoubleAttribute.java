package org.wso2.balana.attr;

import java.net.URI;
import org.w3c.dom.Node;

public class DoubleAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#double";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private double value;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#double");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public DoubleAttribute(double value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.value = value;
  }
  
  public static DoubleAttribute getInstance(Node root)
    throws NumberFormatException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static DoubleAttribute getInstance(String value)
  {
    if (value.endsWith("INF"))
    {
      int infIndex = value.lastIndexOf("INF");
      value = value.substring(0, infIndex) + "Infinity";
    }
    return new DoubleAttribute(Double.parseDouble(value));
  }
  
  public double getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof DoubleAttribute)) {
      return false;
    }
    DoubleAttribute other = (DoubleAttribute)o;
    if (Double.isNaN(value))
    {
      if (Double.isNaN(value)) {
        return true;
      }
      return false;
    }
    return value == value;
  }
  
  public int hashCode()
  {
    long v = Double.doubleToLongBits(value);
    return (int)(v ^ v >>> 32);
  }
  
  public String encode()
  {
    return String.valueOf(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.DoubleAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr;

import java.net.URI;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class BooleanAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#boolean";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private static BooleanAttribute trueInstance;
  private static BooleanAttribute falseInstance;
  private boolean value;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#boolean");
      trueInstance = new BooleanAttribute(true);
      falseInstance = new BooleanAttribute(false);
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  private BooleanAttribute(boolean value)
  {
    super(identifierURI);
    
    this.value = value;
  }
  
  public static BooleanAttribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static BooleanAttribute getInstance(String value)
    throws ParsingException
  {
    if (earlyException != null) {
      throw earlyException;
    }
    if (value.equals("true")) {
      return trueInstance;
    }
    if (value.equals("false")) {
      return falseInstance;
    }
    throw new ParsingException("Boolean string must be true or false");
  }
  
  public static BooleanAttribute getInstance(boolean value)
  {
    if (earlyException != null) {
      throw earlyException;
    }
    if (value) {
      return trueInstance;
    }
    return falseInstance;
  }
  
  public static BooleanAttribute getTrueInstance()
  {
    if (earlyException != null) {
      throw earlyException;
    }
    return trueInstance;
  }
  
  public static BooleanAttribute getFalseInstance()
  {
    if (earlyException != null) {
      throw earlyException;
    }
    return falseInstance;
  }
  
  public boolean getValue()
  {
    return value;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof BooleanAttribute)) {
      return false;
    }
    BooleanAttribute other = (BooleanAttribute)o;
    
    return value == value;
  }
  
  public int hashCode()
  {
    return value ? 1231 : 1237;
  }
  
  public String encode()
  {
    return value ? "true" : "false";
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.BooleanAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
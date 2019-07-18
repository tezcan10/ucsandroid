package org.wso2.balana.attr;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class Base64BinaryAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#base64Binary";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private byte[] value;
  private String strValue;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#base64Binary");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public Base64BinaryAttribute(byte[] value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.value = ((byte[])value.clone());
  }
  
  public static Base64BinaryAttribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static Base64BinaryAttribute getInstance(String value)
    throws ParsingException
  {
    byte[] bytes = null;
    try
    {
      bytes = Base64.decode(value, false);
    }
    catch (IOException e)
    {
      throw new ParsingException("Couldn't parse purported Base64 string: " + value, e);
    }
    return new Base64BinaryAttribute(bytes);
  }
  
  public byte[] getValue()
  {
    return (byte[])value.clone();
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof Base64BinaryAttribute)) {
      return false;
    }
    Base64BinaryAttribute other = (Base64BinaryAttribute)o;
    
    return Arrays.equals(value, value);
  }
  
  public int hashCode()
  {
    int code = value[0];
    for (int i = 1; i < value.length; i++)
    {
      code *= 31;
      code += value[i];
    }
    return code;
  }
  
  private String makeStringRep()
  {
    return Base64.encode(value);
  }
  
  public String toString()
  {
    if (strValue == null) {
      strValue = makeStringRep();
    }
    return "Base64BinaryAttribute: [\n" + strValue + "]\n";
  }
  
  public String encode()
  {
    if (strValue == null) {
      strValue = makeStringRep();
    }
    return strValue;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.Base64BinaryAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
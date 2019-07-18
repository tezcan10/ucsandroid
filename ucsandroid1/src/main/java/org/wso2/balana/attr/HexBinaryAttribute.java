package org.wso2.balana.attr;

import java.net.URI;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class HexBinaryAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#hexBinary";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private byte[] value;
  private String strValue;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#hexBinary");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public HexBinaryAttribute(byte[] value)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.value = ((byte[])value.clone());
  }
  
  public static HexBinaryAttribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static HexBinaryAttribute getInstance(String value)
    throws ParsingException
  {
    byte[] bytes = hexToBin(value);
    if (bytes == null) {
      throw new ParsingException("Couldn't parse purported hex string: " + value);
    }
    return new HexBinaryAttribute(bytes);
  }
  
  public byte[] getValue()
  {
    return (byte[])value.clone();
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
  
  public boolean equals(Object o)
  {
    if (!(o instanceof HexBinaryAttribute)) {
      return false;
    }
    HexBinaryAttribute other = (HexBinaryAttribute)o;
    
    return Arrays.equals(value, value);
  }
  
  private static int hexToBinNibble(char c)
  {
    int result = -1;
    if ((c >= '0') && (c <= '9')) {
      result = c - '0';
    } else if ((c >= 'a') && (c <= 'f')) {
      result = c - 'a' + 10;
    } else if ((c >= 'A') && (c <= 'F')) {
      result = c - 'A' + 10;
    }
    return result;
  }
  
  private static byte[] hexToBin(String hex)
  {
    int len = hex.length();
    if (len % 2 != 0) {
      return null;
    }
    int byteCount = len / 2;
    byte[] bytes = new byte[byteCount];
    
    int charIndex = 0;
    for (int byteIndex = 0; byteIndex < byteCount; byteIndex++)
    {
      int hiNibble = hexToBinNibble(hex.charAt(charIndex++));
      int loNibble = hexToBinNibble(hex.charAt(charIndex++));
      if ((hiNibble < 0) || (loNibble < 0)) {
        return null;
      }
      bytes[byteIndex] = ((byte)(hiNibble * 16 + loNibble));
    }
    return bytes;
  }
  
  private static char binToHexNibble(int nibble)
  {
    char result = '\000';
    if (nibble < 10) {
      result = (char)(nibble + 48);
    } else {
      result = (char)(nibble - 10 + 65);
    }
    return result;
  }
  
  private static String binToHex(byte[] bytes)
  {
    int byteLength = bytes.length;
    char[] chars = new char[byteLength * 2];
    int charIndex = 0;
    for (int byteIndex = 0; byteIndex < byteLength; byteIndex++)
    {
      byte b = bytes[byteIndex];
      chars[(charIndex++)] = binToHexNibble(b >> 4 & 0xF);
      chars[(charIndex++)] = binToHexNibble(b & 0xF);
    }
    return new String(chars);
  }
  
  public String toString()
  {
    if (strValue == null) {
      strValue = binToHex(value);
    }
    return "HexBinaryAttribute: [\n" + strValue + "]\n";
  }
  
  public String encode()
  {
    if (strValue == null) {
      strValue = binToHex(value);
    }
    return strValue;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.HexBinaryAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
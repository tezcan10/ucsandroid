package org.wso2.balana.attr;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public abstract class IPAddressAttribute
  extends AttributeValue
{
  public static final String identifier = "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private InetAddress address;
  private InetAddress mask;
  private PortRange range;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:2.0:data-type:ipAddress");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  protected IPAddressAttribute(InetAddress address, InetAddress mask, PortRange range)
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.address = address;
    this.mask = mask;
    this.range = range;
  }
  
  public static IPAddressAttribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static IPAddressAttribute getInstance(String value)
    throws ParsingException
  {
    try
    {
      if (value.indexOf('[') == 0) {
        return IPv6AddressAttribute.getV6Instance(value);
      }
      return IPv4AddressAttribute.getV4Instance(value);
    }
    catch (UnknownHostException uhe)
    {
      throw new ParsingException("Failed to parse an IPAddress", uhe);
    }
  }
  
  public InetAddress getAddress()
  {
    return address;
  }
  
  public InetAddress getMask()
  {
    return mask;
  }
  
  public PortRange getRange()
  {
    return range;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof IPAddressAttribute)) {
      return false;
    }
    IPAddressAttribute other = (IPAddressAttribute)o;
    if (!address.equals(address)) {
      return false;
    }
    if (mask != null)
    {
      if (mask == null) {
        return false;
      }
      if (!mask.equals(mask)) {
        return false;
      }
    }
    else if (mask != null)
    {
      return false;
    }
    if (!range.equals(range)) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    return 0;
  }
  
  public String toString()
  {
    return "IPAddressAttribute: \"" + encode() + "\"";
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.IPAddressAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
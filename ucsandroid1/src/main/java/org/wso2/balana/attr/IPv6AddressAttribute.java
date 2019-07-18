package org.wso2.balana.attr;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv6AddressAttribute
  extends IPAddressAttribute
{
  public IPv6AddressAttribute(InetAddress address)
  {
    this(address, null, new PortRange());
  }
  
  public IPv6AddressAttribute(InetAddress address, InetAddress mask)
  {
    this(address, mask, new PortRange());
  }
  
  public IPv6AddressAttribute(InetAddress address, PortRange range)
  {
    this(address, null, range);
  }
  
  public IPv6AddressAttribute(InetAddress address, InetAddress mask, PortRange range)
  {
    super(address, mask, range);
  }
  
  protected static IPAddressAttribute getV6Instance(String value)
    throws UnknownHostException
  {
    InetAddress address = null;
    InetAddress mask = null;
    PortRange range = null;
    int len = value.length();
    
    int endIndex = value.indexOf(']');
    address = InetAddress.getByName(value.substring(1, endIndex));
    if (endIndex != len - 1)
    {
      if (value.charAt(endIndex + 1) == '/')
      {
        int startIndex = endIndex + 3;
        endIndex = value.indexOf(']', startIndex);
        mask = InetAddress.getByName(value.substring(startIndex, endIndex));
      }
      if ((endIndex != len - 1) && (value.charAt(endIndex + 1) == ':')) {
        range = PortRange.getInstance(value.substring(endIndex + 2, len));
      }
    }
    range = new PortRange();
    
    return new IPv6AddressAttribute(address, mask, range);
  }
  
  public String encode()
  {
    String str = "[" + getAddress().getHostAddress() + "]";
    if (getMask() != null) {
      str = str + "/[" + getMask().getHostAddress() + "]";
    }
    if (!getRange().isUnbound()) {
      str = str + ":" + getRange().encode();
    }
    return str;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.IPv6AddressAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
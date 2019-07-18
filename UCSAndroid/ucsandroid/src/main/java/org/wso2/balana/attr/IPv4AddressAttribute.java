package org.wso2.balana.attr;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv4AddressAttribute
  extends IPAddressAttribute
{
  public IPv4AddressAttribute(InetAddress address)
  {
    this(address, null, new PortRange());
  }
  
  public IPv4AddressAttribute(InetAddress address, InetAddress mask)
  {
    this(address, mask, new PortRange());
  }
  
  public IPv4AddressAttribute(InetAddress address, PortRange range)
  {
    this(address, null, range);
  }
  
  public IPv4AddressAttribute(InetAddress address, InetAddress mask, PortRange range)
  {
    super(address, mask, range);
  }
  
  protected static IPAddressAttribute getV4Instance(String value)
    throws UnknownHostException
  {
    InetAddress address = null;
    InetAddress mask = null;
    PortRange range = null;
    
    int maskPos = value.indexOf("/");
    int rangePos = value.indexOf(":");
    if (maskPos == rangePos)
    {
      address = InetAddress.getByName(value);
    }
    else if (maskPos != -1)
    {
      address = InetAddress.getByName(value.substring(0, maskPos));
      if (rangePos != -1)
      {
        mask = InetAddress.getByName(value.substring(maskPos + 1, rangePos));
        range = PortRange.getInstance(value.substring(rangePos + 1, value.length()));
      }
      else
      {
        mask = InetAddress.getByName(value.substring(maskPos + 1, value.length()));
      }
    }
    else
    {
      address = InetAddress.getByName(value.substring(0, rangePos));
      range = PortRange.getInstance(value.substring(rangePos + 1, value.length()));
    }
    range = new PortRange();
    
    return new IPv4AddressAttribute(address, mask, range);
  }
  
  public String encode()
  {
    String str = getAddress().getHostAddress();
    if (getMask() != null) {
      str = str + getMask().getHostAddress();
    }
    if (!getRange().isUnbound()) {
      str = str + ":" + getRange().encode();
    }
    return str;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.IPv4AddressAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
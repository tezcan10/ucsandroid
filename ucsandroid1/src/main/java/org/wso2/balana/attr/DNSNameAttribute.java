package org.wso2.balana.attr;

import java.io.PrintStream;
import java.net.URI;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class DNSNameAttribute
  extends AttributeValue
{
  public static final String identifier = "urn:oasis:names:tc:xacml:2.0:data-type:dnsName";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private String hostname;
  private PortRange range;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:2.0:data-type:dnsName");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  private boolean isSubdomain = false;
  
  public DNSNameAttribute(String hostname)
    throws ParsingException
  {
    this(hostname, new PortRange());
  }
  
  public DNSNameAttribute(String hostname, PortRange range)
    throws ParsingException
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    if (!isValidHostName(hostname)) {
      System.out.println("FIXME: throw error about bad hostname");
    }
    if (hostname.charAt(0) == '*') {
      isSubdomain = true;
    }
    this.hostname = hostname;
    this.range = range;
  }
  
  private boolean isValidHostName(String hostname)
  {
    String domainlabel = "\\w[[\\w|\\-]*\\w]?";
    String toplabel = "[a-zA-Z][[\\w|\\-]*\\w]?";
    String pattern = "[\\*\\.]?[" + domainlabel + "\\.]*" + toplabel + "\\.?";
    
    return hostname.matches(pattern);
  }
  
  public static DNSNameAttribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static DNSNameAttribute getInstance(String value)
    throws ParsingException
  {
    int portSep = value.indexOf(':');
    if (portSep == -1) {
      return new DNSNameAttribute(value);
    }
    String hostname = value.substring(0, portSep);
    PortRange range = PortRange.getInstance(value.substring(portSep + 1, value.length()));
    return new DNSNameAttribute(hostname, range);
  }
  
  public String getHostName()
  {
    return hostname;
  }
  
  public PortRange getPortRange()
  {
    return range;
  }
  
  public boolean isSubdomain()
  {
    return isSubdomain;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof DNSNameAttribute)) {
      return false;
    }
    DNSNameAttribute other = (DNSNameAttribute)o;
    if (!hostname.toUpperCase().equals(hostname.toUpperCase())) {
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
    return "DNSNameAttribute: \"" + encode() + "\"";
  }
  
  public String encode()
  {
    if (range.isUnbound()) {
      return hostname;
    }
    return hostname + ":" + range.encode();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.DNSNameAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DNSNameAttribute;

public class DNSNameAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws ParsingException
  {
    return DNSNameAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
    throws ParsingException
  {
    return DNSNameAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.DNSNameAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
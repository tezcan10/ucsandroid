package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.IPAddressAttribute;

public class IPAddressAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws ParsingException
  {
    return IPAddressAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
    throws ParsingException
  {
    return IPAddressAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.IPAddressAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
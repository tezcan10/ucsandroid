package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;

public class StringAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
  {
    return StringAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
  {
    return StringAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.StringAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;

public class DoubleAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws Exception
  {
    return DoubleAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
    throws Exception
  {
    return DoubleAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.DoubleAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
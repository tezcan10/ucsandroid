package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.TimeAttribute;

public class TimeAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws Exception
  {
    return TimeAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
    throws Exception
  {
    return TimeAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.TimeAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
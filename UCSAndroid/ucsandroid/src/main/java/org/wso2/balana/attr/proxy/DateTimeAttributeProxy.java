package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;

public class DateTimeAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws Exception
  {
    return DateTimeAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
    throws Exception
  {
    return DateTimeAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.DateTimeAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
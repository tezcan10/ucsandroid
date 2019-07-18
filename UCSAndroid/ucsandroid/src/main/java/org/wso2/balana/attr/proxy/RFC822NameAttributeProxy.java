package org.wso2.balana.attr.proxy;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.RFC822NameAttribute;

public class RFC822NameAttributeProxy
  extends AbstractAttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws Exception
  {
    return RFC822NameAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value)
    throws Exception
  {
    return RFC822NameAttribute.getInstance(value);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.RFC822NameAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
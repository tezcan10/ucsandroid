package org.wso2.balana.attr.proxy.xacml3;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeProxy;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.xacml3.XPathAttribute;

public class XPathAttributeProxy
  implements AttributeProxy
{
  public AttributeValue getInstance(Node root)
    throws Exception
  {
    return XPathAttribute.getInstance(root);
  }
  
  public AttributeValue getInstance(String value, String[] params)
    throws Exception
  {
    String xPathCategory = null;
    if (params != null) {
      xPathCategory = params[0];
    }
    return XPathAttribute.getInstance(value, xPathCategory);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.xacml3.XPathAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr.proxy;

import org.wso2.balana.attr.AttributeProxy;
import org.wso2.balana.attr.AttributeValue;

public abstract class AbstractAttributeProxy
  implements AttributeProxy
{
  public abstract AttributeValue getInstance(String paramString)
    throws Exception;
  
  public AttributeValue getInstance(String value, String[] params)
    throws Exception
  {
    if ((params == null) || (params.length < 1)) {
      return getInstance(value);
    }
    throw new Exception("Invalid method is called.");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.proxy.AbstractAttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr;

import org.w3c.dom.Node;

public abstract interface AttributeProxy
{
  public abstract AttributeValue getInstance(Node paramNode)
    throws Exception;
  
  public abstract AttributeValue getInstance(String paramString, String[] paramArrayOfString)
    throws Exception;
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
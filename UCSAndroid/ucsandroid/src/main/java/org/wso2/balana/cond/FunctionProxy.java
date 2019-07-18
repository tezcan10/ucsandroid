package org.wso2.balana.cond;

import org.w3c.dom.Node;

public abstract interface FunctionProxy
{
  public abstract Function getInstance(Node paramNode, String paramString)
    throws Exception;
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.FunctionProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
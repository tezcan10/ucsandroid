package org.wso2.balana.cond;

import org.w3c.dom.Node;

public class MapFunctionProxy
  implements FunctionProxy
{
  public Function getInstance(Node root, String xpathVersion)
    throws Exception
  {
    return MapFunction.getInstance(root);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.MapFunctionProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
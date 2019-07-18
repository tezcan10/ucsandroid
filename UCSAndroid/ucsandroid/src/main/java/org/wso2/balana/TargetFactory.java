package org.wso2.balana;

import org.w3c.dom.Node;

public class TargetFactory
{
  private static volatile TargetFactory factoryInstance;
  
  public AbstractTarget getTarget(Node node, PolicyMetaData metaData)
    throws ParsingException
  {
    if (3 == metaData.getXACMLVersion()) {
      return org.wso2.balana.xacml3.Target.getInstance(node, metaData);
    }
    return org.wso2.balana.xacml2.Target.getInstance(node, metaData);
  }
  
  public static TargetFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (TargetFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new TargetFactory();
        }
      }
    }
    return factoryInstance;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.TargetFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
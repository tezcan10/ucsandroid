package org.wso2.balana.attr;

import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;

public class AttributeDesignatorFactory
{
  private static volatile AttributeDesignatorFactory factoryInstance;
  
  public AbstractDesignator getAbstractDesignator(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    if (metaData.getXACMLVersion() == 3) {
      return org.wso2.balana.attr.xacml3.AttributeDesignator.getInstance(root);
    }
    return AttributeDesignator.getInstance(root);
  }
  
  public static AttributeDesignatorFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (AttributeDesignatorFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new AttributeDesignatorFactory();
        }
      }
    }
    return factoryInstance;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeDesignatorFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
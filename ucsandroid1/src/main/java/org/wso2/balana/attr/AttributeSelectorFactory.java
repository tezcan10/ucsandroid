package org.wso2.balana.attr;

import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;

public class AttributeSelectorFactory
{
  private static volatile AttributeSelectorFactory factoryInstance;
  
  public AbstractAttributeSelector getAbstractSelector(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    if (metaData.getXACMLVersion() == 3) {
      return org.wso2.balana.attr.xacml3.AttributeSelector.getInstance(root, metaData);
    }
    return AttributeSelector.getInstance(root, metaData);
  }
  
  public static AttributeSelectorFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (AttributeDesignatorFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new AttributeSelectorFactory();
        }
      }
    }
    return factoryInstance;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeSelectorFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
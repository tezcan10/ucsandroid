package org.wso2.balana;

import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeFactoryProxy;

class ConfigurationStore$AFProxy
  implements AttributeFactoryProxy
{
  private AttributeFactory factory;
  
  public ConfigurationStore$AFProxy(AttributeFactory factory)
  {
    this.factory = factory;
  }
  
  public AttributeFactory getFactory()
  {
    return factory;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ConfigurationStore.AFProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
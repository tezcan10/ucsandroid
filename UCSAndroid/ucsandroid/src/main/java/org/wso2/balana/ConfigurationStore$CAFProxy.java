package org.wso2.balana;

import org.wso2.balana.combine.CombiningAlgFactory;
import org.wso2.balana.combine.CombiningAlgFactoryProxy;

class ConfigurationStore$CAFProxy
  implements CombiningAlgFactoryProxy
{
  private CombiningAlgFactory factory;
  
  public ConfigurationStore$CAFProxy(CombiningAlgFactory factory)
  {
    this.factory = factory;
  }
  
  public CombiningAlgFactory getFactory()
  {
    return factory;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ConfigurationStore.CAFProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
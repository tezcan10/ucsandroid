package org.wso2.balana;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeFactoryProxy;
import org.wso2.balana.combine.CombiningAlgFactory;
import org.wso2.balana.combine.CombiningAlgFactoryProxy;
import org.wso2.balana.cond.FunctionFactory;
import org.wso2.balana.cond.FunctionFactoryProxy;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.CurrentEnvModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.finder.impl.SelectorModule;

public class Balana
{
  private PDPConfig pdpConfig;
  private AttributeFactory attributeFactory;
  private FunctionFactory functionTargetFactory;
  private FunctionFactory functionConditionFactory;
  private FunctionFactory functionGeneralFactory;
  private CombiningAlgFactory combiningAlgFactory;
  private static Balana balana;
  
  private Balana(String pdpConfigName, String attributeFactoryName, String functionFactoryName, String combiningAlgFactoryName)
  {
    ConfigurationStore store = null;
    try
    {
      if (System.getProperty("org.wso2.balana.PDPConfigFile") != null)
      {
        store = new ConfigurationStore();
      }
      else
      {
        String configFile = new File(".").getCanonicalPath() + File.separator + "src" + 
          File.separator + "main" + File.separator + "resources" + File.separator + "config.xml";
        File file = new File(configFile);
        if (file.exists()) {
          store = new ConfigurationStore(new File(configFile));
        }
      }
      if (store != null)
      {
        if (pdpConfigName != null) {
          pdpConfig = store.getPDPConfig(pdpConfigName);
        } else {
          pdpConfig = store.getDefaultPDPConfig();
        }
        if (attributeFactoryName != null) {
          attributeFactory = store.getAttributeFactory(attributeFactoryName);
        } else {
          attributeFactory = store.getDefaultAttributeFactoryProxy().getFactory();
        }
        if (functionFactoryName != null) {
          functionTargetFactory = store
            .getFunctionFactoryProxy(functionFactoryName).getTargetFactory();
        } else {
          functionTargetFactory = store
            .getDefaultFunctionFactoryProxy().getTargetFactory();
        }
        if (functionFactoryName != null) {
          functionConditionFactory = store
            .getFunctionFactoryProxy(functionFactoryName).getConditionFactory();
        } else {
          functionConditionFactory = store
            .getDefaultFunctionFactoryProxy().getConditionFactory();
        }
        if (functionFactoryName != null) {
          functionGeneralFactory = store
            .getFunctionFactoryProxy(functionFactoryName).getGeneralFactory();
        } else {
          functionGeneralFactory = store
            .getDefaultFunctionFactoryProxy().getGeneralFactory();
        }
        if (functionFactoryName != null) {
          combiningAlgFactory = store.getCombiningAlgFactory(functionFactoryName);
        } else {
          combiningAlgFactory = store.getDefaultCombiningFactoryProxy().getFactory();
        }
      }
    }
    catch (Exception localException) {}
    if (pdpConfig == null)
    {
      PolicyFinder policyFinder = new PolicyFinder();
      Set<PolicyFinderModule> policyFinderModules = new HashSet();
      FileBasedPolicyFinderModule fileBasedPolicyFinderModule = new FileBasedPolicyFinderModule();
      policyFinderModules.add(fileBasedPolicyFinderModule);
      policyFinder.setModules(policyFinderModules);
      
      AttributeFinder attributeFinder = new AttributeFinder();
      List<AttributeFinderModule> attributeFinderModules = new ArrayList();
      SelectorModule selectorModule = new SelectorModule();
      CurrentEnvModule currentEnvModule = new CurrentEnvModule();
      attributeFinderModules.add(selectorModule);
      attributeFinderModules.add(currentEnvModule);
      attributeFinder.setModules(attributeFinderModules);
      
      pdpConfig = new PDPConfig(attributeFinder, policyFinder, null, false);
    }
    if (attributeFactory == null) {
      attributeFactory = AttributeFactory.getInstance();
    }
    if (functionTargetFactory == null) {
      functionTargetFactory = FunctionFactory.getInstance().getTargetFactory();
    }
    if (functionConditionFactory == null) {
      functionConditionFactory = FunctionFactory.getInstance().getConditionFactory();
    }
    if (functionGeneralFactory == null) {
      functionGeneralFactory = FunctionFactory.getInstance().getGeneralFactory();
    }
    if (combiningAlgFactory == null) {
      combiningAlgFactory = CombiningAlgFactory.getInstance();
    }
  }
  
  public static Balana getInstance()
  {
    if (balana == null) {
      balana = new Balana(null, null, null, null);
    }
    return balana;
  }
  
  public Balana getInstance(String identifier)
  {
    if (balana == null) {
      balana = new Balana(identifier, identifier, identifier, identifier);
    }
    return balana;
  }
  
  public Balana getInstance(String pdpConfigName, String attributeFactoryName, String functionFactoryName, String combiningAlgFactoryName)
  {
    if (balana == null) {
      balana = new Balana(pdpConfigName, attributeFactoryName, functionFactoryName, 
        combiningAlgFactoryName);
    }
    return balana;
  }
  
  public PDPConfig getPdpConfig()
  {
    return pdpConfig;
  }
  
  public void setPdpConfig(PDPConfig pdpConfig)
  {
    this.pdpConfig = pdpConfig;
  }
  
  public AttributeFactory getAttributeFactory()
  {
    return attributeFactory;
  }
  
  public void setAttributeFactory(AttributeFactory attributeFactory)
  {
    this.attributeFactory = attributeFactory;
  }
  
  public FunctionFactory getFunctionTargetFactory()
  {
    return functionTargetFactory;
  }
  
  public void setFunctionTargetFactory(FunctionFactory functionTargetFactory)
  {
    this.functionTargetFactory = functionTargetFactory;
  }
  
  public FunctionFactory getFunctionConditionFactory()
  {
    return functionConditionFactory;
  }
  
  public void setFunctionConditionFactory(FunctionFactory functionConditionFactory)
  {
    this.functionConditionFactory = functionConditionFactory;
  }
  
  public FunctionFactory getFunctionGeneralFactory()
  {
    return functionGeneralFactory;
  }
  
  public void setFunctionGeneralFactory(FunctionFactory functionGeneralFactory)
  {
    this.functionGeneralFactory = functionGeneralFactory;
  }
  
  public CombiningAlgFactory getCombiningAlgFactory()
  {
    return combiningAlgFactory;
  }
  
  public void setCombiningAlgFactory(CombiningAlgFactory combiningAlgFactory)
  {
    this.combiningAlgFactory = combiningAlgFactory;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.Balana
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
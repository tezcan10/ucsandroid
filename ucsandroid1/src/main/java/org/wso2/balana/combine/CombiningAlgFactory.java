package org.wso2.balana.combine;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;
import org.wso2.balana.UnknownIdentifierException;

public abstract class CombiningAlgFactory
{
  private static CombiningAlgFactoryProxy defaultFactoryProxy;
  private static HashMap<String, CombiningAlgFactoryProxy> registeredFactories;
  
  static
  {
    CombiningAlgFactoryProxy proxy = new CombiningAlgFactoryProxy()
    {
      public CombiningAlgFactory getFactory()
      {
        return StandardCombiningAlgFactory.getFactory();
      }
    };
    registeredFactories = new HashMap();
    registeredFactories.put("urn:oasis:names:tc:xacml:1.0:policy", proxy);
    registeredFactories.put("urn:oasis:names:tc:xacml:2.0:policy:schema:os", proxy);
    registeredFactories.put("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", proxy);
    
    defaultFactoryProxy = proxy;
  }
  
  public static final CombiningAlgFactory getInstance()
  {
    return defaultFactoryProxy.getFactory();
  }
  
  public static final CombiningAlgFactory getInstance(String identifier)
    throws UnknownIdentifierException
  {
    CombiningAlgFactoryProxy proxy = (CombiningAlgFactoryProxy)registeredFactories.get(identifier);
    if (proxy == null) {
      throw new UnknownIdentifierException("Unknown CombiningAlgFactory identifier: " + 
        identifier);
    }
    return proxy.getFactory();
  }
  
  public static final void setDefaultFactory(CombiningAlgFactoryProxy proxy)
  {
    defaultFactoryProxy = proxy;
  }
  
  public static final void registerFactory(String identifier, CombiningAlgFactoryProxy proxy)
    throws IllegalArgumentException
  {
    synchronized (registeredFactories)
    {
      if (registeredFactories.containsKey(identifier)) {
        throw new IllegalArgumentException("Identifier is already registered as CombiningAlgFactory: " + 
          identifier);
      }
      registeredFactories.put(identifier, proxy);
    }
  }
  
  public abstract void addAlgorithm(CombiningAlgorithm paramCombiningAlgorithm);
  
  /**
   * @deprecated
   */
  public static void addCombiningAlg(CombiningAlgorithm alg)
  {
    getInstance().addAlgorithm(alg);
  }
  
  public abstract Set getSupportedAlgorithms();
  
  public abstract CombiningAlgorithm createAlgorithm(URI paramURI)
    throws UnknownIdentifierException;
  
  /**
   * @deprecated
   */
  public static CombiningAlgorithm createCombiningAlg(URI algId)
    throws UnknownIdentifierException
  {
    return getInstance().createAlgorithm(algId);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.CombiningAlgFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
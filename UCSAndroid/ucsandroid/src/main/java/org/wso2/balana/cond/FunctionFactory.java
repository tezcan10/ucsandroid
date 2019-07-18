package org.wso2.balana.cond;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;

public abstract class FunctionFactory
{
  private static FunctionFactoryProxy defaultFactoryProxy;
  private static HashMap registeredFactories;
  
  static
  {
    FunctionFactoryProxy proxy = new FunctionFactoryProxy()
    {
      public FunctionFactory getTargetFactory()
      {
        return StandardFunctionFactory.getTargetFactory();
      }
      
      public FunctionFactory getConditionFactory()
      {
        return StandardFunctionFactory.getConditionFactory();
      }
      
      public FunctionFactory getGeneralFactory()
      {
        return StandardFunctionFactory.getGeneralFactory();
      }
    };
    registeredFactories = new HashMap();
    registeredFactories.put("urn:oasis:names:tc:xacml:1.0:policy", proxy);
    registeredFactories.put("urn:oasis:names:tc:xacml:2.0:policy:schema:os", proxy);
    registeredFactories.put("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", proxy);
    
    defaultFactoryProxy = proxy;
  }
  
  public static final FunctionFactory getTargetInstance()
  {
    return defaultFactoryProxy.getTargetFactory();
  }
  
  public static final FunctionFactory getTargetInstance(String identifier)
    throws UnknownIdentifierException
  {
    return getRegisteredProxy(identifier).getTargetFactory();
  }
  
  public static final FunctionFactory getConditionInstance()
  {
    return defaultFactoryProxy.getConditionFactory();
  }
  
  public static final FunctionFactory getConditionInstance(String identifier)
    throws UnknownIdentifierException
  {
    return getRegisteredProxy(identifier).getConditionFactory();
  }
  
  public static final FunctionFactory getGeneralInstance()
  {
    return defaultFactoryProxy.getGeneralFactory();
  }
  
  public static final FunctionFactory getGeneralInstance(String identifier)
    throws UnknownIdentifierException
  {
    return getRegisteredProxy(identifier).getGeneralFactory();
  }
  
  public static final FunctionFactoryProxy getInstance()
  {
    return defaultFactoryProxy;
  }
  
  public static final FunctionFactoryProxy getInstance(String identifier)
    throws UnknownIdentifierException
  {
    return getRegisteredProxy(identifier);
  }
  
  private static FunctionFactoryProxy getRegisteredProxy(String identifier)
    throws UnknownIdentifierException
  {
    FunctionFactoryProxy proxy = (FunctionFactoryProxy)registeredFactories.get(identifier);
    if (proxy == null) {
      throw new UnknownIdentifierException("Uknown FunctionFactory identifier: " + 
        identifier);
    }
    return proxy;
  }
  
  public static final void setDefaultFactory(FunctionFactoryProxy proxy)
  {
    defaultFactoryProxy = proxy;
  }
  
  public static final void registerFactory(String identifier, FunctionFactoryProxy proxy)
    throws IllegalArgumentException
  {
    synchronized (registeredFactories)
    {
      if (registeredFactories.containsKey(identifier)) {
        throw new IllegalArgumentException("Identifier is already registered as FunctionFactory: " + 
          identifier);
      }
      registeredFactories.put(identifier, proxy);
    }
  }
  
  public abstract void addFunction(Function paramFunction);
  
  public abstract void addAbstractFunction(FunctionProxy paramFunctionProxy, URI paramURI);
  
  /**
   * @deprecated
   */
  public static void addTargetFunction(Function function)
  {
    getTargetInstance().addFunction(function);
  }
  
  /**
   * @deprecated
   */
  public static void addAbstractTargetFunction(FunctionProxy proxy, URI identity)
  {
    getTargetInstance().addAbstractFunction(proxy, identity);
  }
  
  /**
   * @deprecated
   */
  public static void addConditionFunction(Function function)
  {
    getConditionInstance().addFunction(function);
  }
  
  /**
   * @deprecated
   */
  public static void addAbstractConditionFunction(FunctionProxy proxy, URI identity)
  {
    getConditionInstance().addAbstractFunction(proxy, identity);
  }
  
  /**
   * @deprecated
   */
  public static void addGeneralFunction(Function function)
  {
    getGeneralInstance().addFunction(function);
  }
  
  /**
   * @deprecated
   */
  public static void addAbstractGeneralFunction(FunctionProxy proxy, URI identity)
  {
    getGeneralInstance().addAbstractFunction(proxy, identity);
  }
  
  public abstract Set getSupportedFunctions();
  
  public abstract Function createFunction(URI paramURI)
    throws UnknownIdentifierException, FunctionTypeException;
  
  public abstract Function createFunction(String paramString)
    throws UnknownIdentifierException, FunctionTypeException;
  
  public abstract Function createAbstractFunction(URI paramURI, Node paramNode)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException;
  
  public abstract Function createAbstractFunction(URI paramURI, Node paramNode, String paramString)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException;
  
  public abstract Function createAbstractFunction(String paramString, Node paramNode)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException;
  
  public abstract Function createAbstractFunction(String paramString1, Node paramNode, String paramString2)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException;
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.FunctionFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
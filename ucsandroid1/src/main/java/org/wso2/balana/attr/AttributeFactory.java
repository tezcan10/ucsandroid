package org.wso2.balana.attr;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;

public abstract class AttributeFactory
{
  private static AttributeFactoryProxy defaultFactoryProxy;
  private static HashMap registeredFactories;
  
  static
  {
    AttributeFactoryProxy proxy = new AttributeFactoryProxy()
    {
      public AttributeFactory getFactory()
      {
        return StandardAttributeFactory.getFactory();
      }
    };
    registeredFactories = new HashMap();
    registeredFactories.put("urn:oasis:names:tc:xacml:1.0:policy", proxy);
    registeredFactories.put("urn:oasis:names:tc:xacml:2.0:policy:schema:os", proxy);
    registeredFactories.put("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", proxy);
    
    defaultFactoryProxy = proxy;
  }
  
  public static final AttributeFactory getInstance()
  {
    return defaultFactoryProxy.getFactory();
  }
  
  public static final AttributeFactory getInstance(String identifier)
    throws UnknownIdentifierException
  {
    AttributeFactoryProxy proxy = (AttributeFactoryProxy)registeredFactories.get(identifier);
    if (proxy == null) {
      throw new UnknownIdentifierException("Uknown AttributeFactory identifier: " + 
        identifier);
    }
    return proxy.getFactory();
  }
  
  public static final void setDefaultFactory(AttributeFactoryProxy proxy)
  {
    defaultFactoryProxy = proxy;
  }
  
  public static final void registerFactory(String identifier, AttributeFactoryProxy proxy)
    throws IllegalArgumentException
  {
    synchronized (registeredFactories)
    {
      if (registeredFactories.containsKey(identifier)) {
        throw new IllegalArgumentException("Identifier is already registered as AttributeFactory: " + 
          identifier);
      }
      registeredFactories.put(identifier, proxy);
    }
  }
  
  public abstract void addDatatype(String paramString, AttributeProxy paramAttributeProxy);
  
  public abstract Set getSupportedDatatypes();
  
  public abstract AttributeValue createValue(Node paramNode)
    throws UnknownIdentifierException, ParsingException;
  
  public abstract AttributeValue createValue(Node paramNode, URI paramURI)
    throws UnknownIdentifierException, ParsingException;
  
  public abstract AttributeValue createValue(Node paramNode, String paramString)
    throws UnknownIdentifierException, ParsingException;
  
  public AttributeValue createValue(URI dataType, String value)
    throws UnknownIdentifierException, ParsingException
  {
    return createValue(dataType, value, null);
  }
  
  public abstract AttributeValue createValue(URI paramURI, String paramString, String[] paramArrayOfString)
    throws UnknownIdentifierException, ParsingException;
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
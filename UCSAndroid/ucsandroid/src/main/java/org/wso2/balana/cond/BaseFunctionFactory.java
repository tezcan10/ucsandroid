package org.wso2.balana.cond;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;

public class BaseFunctionFactory
  extends FunctionFactory
{
  private HashMap functionMap = null;
  private FunctionFactory superset = null;
  
  public BaseFunctionFactory()
  {
    this(null);
  }
  
  public BaseFunctionFactory(FunctionFactory superset)
  {
    functionMap = new HashMap();
    
    this.superset = superset;
  }
  
  public BaseFunctionFactory(Set supportedFunctions, Map supportedAbstractFunctions)
  {
    this(null, supportedFunctions, supportedAbstractFunctions);
  }
  
  public BaseFunctionFactory(FunctionFactory superset, Set supportedFunctions, Map supportedAbstractFunctions)
  {
    this(superset);
    
    Iterator it = supportedFunctions.iterator();
    while (it.hasNext())
    {
      Function function = (Function)it.next();
      functionMap.put(function.getIdentifier().toString(), function);
    }
    it = supportedAbstractFunctions.entrySet().iterator();
    while (it.hasNext())
    {
      URI id = (URI)((Map.Entry)it.next()).getKey();
      FunctionProxy proxy = (FunctionProxy)supportedAbstractFunctions.get(id);
      functionMap.put(id.toString(), proxy);
    }
  }
  
  public void addFunction(Function function)
    throws IllegalArgumentException
  {
    String id = function.getIdentifier().toString();
    if (functionMap.containsKey(id)) {
      throw new IllegalArgumentException("function already exists");
    }
    if (superset != null) {
      superset.addFunction(function);
    }
    functionMap.put(id, function);
  }
  
  public void addAbstractFunction(FunctionProxy proxy, URI identity)
    throws IllegalArgumentException
  {
    String id = identity.toString();
    if (functionMap.containsKey(id)) {
      throw new IllegalArgumentException("function already exists");
    }
    if (superset != null) {
      superset.addAbstractFunction(proxy, identity);
    }
    functionMap.put(id, proxy);
  }
  
  public Set getSupportedFunctions()
  {
    Set set = new HashSet(functionMap.keySet());
    if (superset != null) {
      set.addAll(superset.getSupportedFunctions());
    }
    return set;
  }
  
  public Function createFunction(URI identity)
    throws UnknownIdentifierException, FunctionTypeException
  {
    return createFunction(identity.toString());
  }
  
  public Function createFunction(String identity)
    throws UnknownIdentifierException, FunctionTypeException
  {
    Object entry = functionMap.get(identity);
    if (entry != null)
    {
      if ((entry instanceof Function)) {
        return (Function)entry;
      }
      throw new FunctionTypeException("function is abstract");
    }
    throw new UnknownIdentifierException("functions of type " + identity + " are not " + 
      "supported by this factory");
  }
  
  public Function createAbstractFunction(URI identity, Node root)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException
  {
    return createAbstractFunction(identity.toString(), root, null);
  }
  
  public Function createAbstractFunction(URI identity, Node root, String xpathVersion)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException
  {
    return createAbstractFunction(identity.toString(), root, xpathVersion);
  }
  
  public Function createAbstractFunction(String identity, Node root)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException
  {
    return createAbstractFunction(identity, root, null);
  }
  
  public Function createAbstractFunction(String identity, Node root, String xpathVersion)
    throws UnknownIdentifierException, ParsingException, FunctionTypeException
  {
    Object entry = functionMap.get(identity);
    if (entry != null)
    {
      if ((entry instanceof FunctionProxy)) {
        try
        {
          return ((FunctionProxy)entry).getInstance(root, xpathVersion);
        }
        catch (Exception e)
        {
          throw new ParsingException(
            "couldn't create abstract function " + identity, e);
        }
      }
      throw new FunctionTypeException("function is concrete");
    }
    throw new UnknownIdentifierException("abstract functions of type " + identity + 
      " are not supported by " + "this factory");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.BaseFunctionFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
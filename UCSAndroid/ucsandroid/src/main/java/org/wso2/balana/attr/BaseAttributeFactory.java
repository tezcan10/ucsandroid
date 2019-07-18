package org.wso2.balana.attr;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;

public class BaseAttributeFactory
  extends AttributeFactory
{
  private HashMap attributeMap;
  
  public BaseAttributeFactory()
  {
    attributeMap = new HashMap();
  }
  
  public BaseAttributeFactory(Map attributes)
  {
    attributeMap = new HashMap();
    
    Iterator it = attributes.keySet().iterator();
    while (it.hasNext()) {
      try
      {
        String id = it.next().toString();
        AttributeProxy proxy = (AttributeProxy)attributes.get(id);
        attributeMap.put(id, proxy);
      }
      catch (ClassCastException cce)
      {
        throw new IllegalArgumentException("an element of the map was not an instance of AttributeProxy");
      }
    }
  }
  
  public void addDatatype(String id, AttributeProxy proxy)
  {
    if (attributeMap.containsKey(id)) {
      throw new IllegalArgumentException("datatype already exists");
    }
    attributeMap.put(id, proxy);
  }
  
  public Set getSupportedDatatypes()
  {
    return Collections.unmodifiableSet(attributeMap.keySet());
  }
  
  public AttributeValue createValue(Node root)
    throws UnknownIdentifierException, ParsingException
  {
    Node node = root.getAttributes().getNamedItem("DataType");
    
    return createValue(root, node.getNodeValue());
  }
  
  public AttributeValue createValue(Node root, URI dataType)
    throws UnknownIdentifierException, ParsingException
  {
    return createValue(root, dataType.toString());
  }
  
  public AttributeValue createValue(Node root, String type)
    throws UnknownIdentifierException, ParsingException
  {
    AttributeProxy proxy = (AttributeProxy)attributeMap.get(type);
    if (proxy != null) {
      try
      {
        return proxy.getInstance(root);
      }
      catch (Exception e)
      {
        throw new ParsingException("couldn't create " + type + 
          " attribute based on DOM node");
      }
    }
    throw new UnknownIdentifierException("Attributes of type " + type + 
      " aren't supported.");
  }
  
  public AttributeValue createValue(URI dataType, String value, String[] params)
    throws UnknownIdentifierException, ParsingException
  {
    String type = dataType.toString();
    AttributeProxy proxy = (AttributeProxy)attributeMap.get(type);
    if (proxy != null) {
      try
      {
        return proxy.getInstance(value, params);
      }
      catch (Exception e)
      {
        throw new ParsingException("couldn't create " + type + " attribute from input: " + 
          value);
      }
    }
    throw new UnknownIdentifierException("Attributes of type " + type + 
      " aren't supported.");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.BaseAttributeFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
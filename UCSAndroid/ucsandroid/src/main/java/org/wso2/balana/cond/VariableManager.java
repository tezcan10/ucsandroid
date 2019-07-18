package org.wso2.balana.cond;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ProcessingException;

public class VariableManager
{
  private Map idMap;
  private PolicyMetaData metaData;
  
  public VariableManager(Map variableIds, PolicyMetaData metaData)
  {
    idMap = new HashMap();
    
    Iterator it = variableIds.entrySet().iterator();
    while (it.hasNext())
    {
      Object key = ((Map.Entry)it.next()).getKey();
      Node node = (Node)variableIds.get(key);
      idMap.put(key, new VariableState(null, node, null, false, false));
    }
    this.metaData = metaData;
  }
  
  public VariableDefinition getDefinition(String variableId)
  {
    VariableState state = (VariableState)idMap.get(variableId);
    if (state == null) {
      throw new ProcessingException("variable is unsupported: " + variableId);
    }
    if (definition != null) {
      return definition;
    }
    Node node = rootNode;
    if (node != null)
    {
      if (handled) {
        throw new ProcessingException("processing in progress");
      }
      handled = true;
      discoverApplyType(node, state);
      try
      {
        definition = VariableDefinition.getInstance(rootNode, metaData, this);
        
        return definition;
      }
      catch (ParsingException pe)
      {
        throw new ProcessingException("failed to parse the definition", pe);
      }
    }
    throw new ProcessingException("couldn't retrieve definition: " + variableId);
  }
  
  private void discoverApplyType(Node root, VariableState state)
  {
    NodeList nodes = root.getChildNodes();
    Node xprNode = nodes.item(0);
    int i = 1;
    while (xprNode.getNodeType() != 1) {
      xprNode = nodes.item(i++);
    }
    if (xprNode.getNodeName().equals("Apply")) {
      try
      {
        Function function = ExpressionHandler.getFunction(xprNode, metaData, 
          FunctionFactory.getGeneralInstance());
        
        type = function.getReturnType();
        returnsBag = function.returnsBag();
      }
      catch (ParsingException localParsingException) {}
    }
  }
  
  public URI getVariableType(String variableId)
  {
    VariableState state = (VariableState)idMap.get(variableId);
    if (state == null) {
      throw new ProcessingException("variable not supported: " + variableId);
    }
    if (type != null) {
      return type;
    }
    VariableDefinition definition = definition;
    if (definition == null) {
      definition = getDefinition(variableId);
    }
    if (definition != null) {
      return definition.getExpression().getType();
    }
    throw new ProcessingException("we couldn't establish the type: " + variableId);
  }
  
  public boolean returnsBag(String variableId)
  {
    VariableState state = (VariableState)idMap.get(variableId);
    if (state == null) {
      throw new ProcessingException("variable not supported: " + variableId);
    }
    if (type != null) {
      return returnsBag;
    }
    VariableDefinition definition = definition;
    if (definition == null) {
      definition = getDefinition(variableId);
    }
    if (definition != null) {
      return definition.getExpression().returnsBag();
    }
    throw new ProcessingException("couldn't establish bag return for " + variableId);
  }
  
  static class VariableState
  {
    public VariableDefinition definition;
    public Node rootNode;
    public URI type;
    public boolean returnsBag;
    public boolean handled;
    
    public VariableState()
    {
      definition = null;
      rootNode = null;
      type = null;
      returnsBag = false;
      handled = false;
    }
    
    public VariableState(VariableDefinition definition, Node rootNode, URI type, boolean returnsBag, boolean handled)
    {
      this.definition = definition;
      this.rootNode = rootNode;
      this.type = type;
      this.returnsBag = returnsBag;
      this.handled = handled;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.VariableManager
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
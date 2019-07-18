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

public class VariableManager {
  private Map idMap = new HashMap();
  private PolicyMetaData metaData;

  public VariableManager(Map variableIds, PolicyMetaData metaData) {
    Iterator it = variableIds.entrySet().iterator();

    while(it.hasNext()) {
      Object key = ((Entry)it.next()).getKey();
      Node node = (Node)variableIds.get(key);
      this.idMap.put(key, new VariableManager.VariableState((VariableDefinition)null, node, (URI)null, false, false));
    }

    this.metaData = metaData;
  }

  public VariableDefinition getDefinition(String variableId) {
    VariableManager.VariableState state = (VariableManager.VariableState)this.idMap.get(variableId);
    if (state == null) {
      throw new ProcessingException("variable is unsupported: " + variableId);
    } else if (state.definition != null) {
      return state.definition;
    } else {
      Node node = state.rootNode;
      if (node != null) {
        if (state.handled) {
          throw new ProcessingException("processing in progress");
        } else {
          state.handled = true;
          this.discoverApplyType(node, state);

          try {
            state.definition = VariableDefinition.getInstance(state.rootNode, this.metaData, this);
            return state.definition;
          } catch (ParsingException var5) {
            throw new ProcessingException("failed to parse the definition", var5);
          }
        }
      } else {
        throw new ProcessingException("couldn't retrieve definition: " + variableId);
      }
    }
  }

  private void discoverApplyType(Node root, VariableManager.VariableState state) {
    NodeList nodes = root.getChildNodes();
    Node xprNode = nodes.item(0);

    for(int var5 = 1; xprNode.getNodeType() != 1; xprNode = nodes.item(var5++)) {
    }

    if (xprNode.getNodeName().equals("Apply")) {
      try {
        Function function = ExpressionHandler.getFunction(xprNode, this.metaData, FunctionFactory.getGeneralInstance());
        state.type = function.getReturnType();
        state.returnsBag = function.returnsBag();
      } catch (ParsingException var7) {
      }
    }

  }

  public URI getVariableType(String variableId) {
    VariableManager.VariableState state = (VariableManager.VariableState)this.idMap.get(variableId);
    if (state == null) {
      throw new ProcessingException("variable not supported: " + variableId);
    } else if (state.type != null) {
      return state.type;
    } else {
      VariableDefinition definition = state.definition;
      if (definition == null) {
        definition = this.getDefinition(variableId);
      }

      if (definition != null) {
        return definition.getExpression().getType();
      } else {
        throw new ProcessingException("we couldn't establish the type: " + variableId);
      }
    }
  }

  public boolean returnsBag(String variableId) {
    VariableManager.VariableState state = (VariableManager.VariableState)this.idMap.get(variableId);
    if (state == null) {
      throw new ProcessingException("variable not supported: " + variableId);
    } else if (state.type != null) {
      return state.returnsBag;
    } else {
      VariableDefinition definition = state.definition;
      if (definition == null) {
        definition = this.getDefinition(variableId);
      }

      if (definition != null) {
        return definition.getExpression().returnsBag();
      } else {
        throw new ProcessingException("couldn't establish bag return for " + variableId);
      }
    }
  }

  static class VariableState {
    public VariableDefinition definition;
    public Node rootNode;
    public URI type;
    public boolean returnsBag;
    public boolean handled;

    public VariableState() {
      this.definition = null;
      this.rootNode = null;
      this.type = null;
      this.returnsBag = false;
      this.handled = false;
    }

    public VariableState(VariableDefinition definition, Node rootNode, URI type, boolean returnsBag, boolean handled) {
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
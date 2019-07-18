package org.wso2.balana.cond;

import java.net.URI;
import org.w3c.dom.Node;

class VariableManager$VariableState
{
  public VariableDefinition definition;
  public Node rootNode;
  public URI type;
  public boolean returnsBag;
  public boolean handled;
  
  public VariableManager$VariableState()
  {
    definition = null;
    rootNode = null;
    type = null;
    returnsBag = false;
    handled = false;
  }
  
  public VariableManager$VariableState(VariableDefinition definition, Node rootNode, URI type, boolean returnsBag, boolean handled)
  {
    this.definition = definition;
    this.rootNode = rootNode;
    this.type = type;
    this.returnsBag = returnsBag;
    this.handled = handled;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.VariableManager.VariableState
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
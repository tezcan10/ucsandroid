package org.wso2.balana.cond;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ProcessingException;
import org.wso2.balana.ctx.EvaluationCtx;

public class VariableReference
  implements Expression
{
  private String variableId;
  private VariableDefinition definition = null;
  private VariableManager manager = null;
  
  public VariableReference(String variableId)
  {
    this.variableId = variableId;
  }
  
  public VariableReference(VariableDefinition definition)
  {
    variableId = definition.getVariableId();
    this.definition = definition;
  }
  
  public VariableReference(String variableId, VariableManager manager)
  {
    this.variableId = variableId;
    this.manager = manager;
  }
  
  public static VariableReference getInstance(Node root, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    String variableId = root.getAttributes().getNamedItem("VariableId").getNodeValue();
    
    return new VariableReference(variableId, manager);
  }
  
  public String getVariableId()
  {
    return variableId;
  }
  
  public VariableDefinition getReferencedDefinition()
  {
    if (definition != null) {
      return definition;
    }
    if (manager != null) {
      return manager.getDefinition(variableId);
    }
    return null;
  }
  
  public EvaluationResult evaluate(EvaluationCtx context)
  {
    Expression xpr = getReferencedDefinition().getExpression();
    
    return ((Evaluatable)xpr).evaluate(context);
  }
  
  public URI getType()
  {
    if (definition != null) {
      return definition.getExpression().getType();
    }
    if (manager != null) {
      return manager.getVariableType(variableId);
    }
    throw new ProcessingException("couldn't resolve the type");
  }
  
  public boolean returnsBag()
  {
    if (definition != null) {
      return getReferencedDefinition().getExpression().returnsBag();
    }
    if (manager != null) {
      return manager.returnsBag(variableId);
    }
    throw new ProcessingException("couldn't resolve the return type");
  }
  
  /**
   * @deprecated
   */
  public boolean evaluatesToBag()
  {
    return returnsBag();
  }
  
  public List getChildren()
  {
    return Collections.EMPTY_LIST;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<VariableReference VariableId=\"" + variableId + "\"/>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.VariableReference
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import java.io.OutputStream;
import java.io.PrintStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;

public class VariableDefinition
{
  private String variableId;
  private Expression expression;
  
  public VariableDefinition(String variableId, Expression expression)
  {
    this.variableId = variableId;
    this.expression = expression;
  }
  
  public static VariableDefinition getInstance(Node root, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    String variableId = root.getAttributes().getNamedItem("VariableId").getNodeValue();
    
    NodeList nodes = root.getChildNodes();
    Node xprNode = nodes.item(0);
    int i = 1;
    while (xprNode.getNodeType() != 1) {
      xprNode = nodes.item(i++);
    }
    Expression xpr = ExpressionHandler.parseExpression(xprNode, metaData, manager);
    
    return new VariableDefinition(variableId, xpr);
  }
  
  public String getVariableId()
  {
    return variableId;
  }
  
  public Expression getExpression()
  {
    return expression;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<VariableDefinition VariableId=\"" + variableId + "\">");
    indenter.in();
    
    expression.encode(output, indenter);
    
    out.println("</VariableDefinition>");
    indenter.out();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.VariableDefinition
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
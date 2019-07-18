package org.wso2.balana.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.EvaluationCtx;

public class AdviceExpression
{
  private URI adviceId;
  private int appliesTo;
  private List<AttributeAssignmentExpression> attributeAssignmentExpressions;
  
  public AdviceExpression(URI adviceId, int appliesTo, List<AttributeAssignmentExpression> attributeAssignmentExpressions)
  {
    this.adviceId = adviceId;
    this.appliesTo = appliesTo;
    this.attributeAssignmentExpressions = attributeAssignmentExpressions;
  }
  
  public static AdviceExpression getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    List<AttributeAssignmentExpression> expressions = new ArrayList();
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      adviceId = new URI(attrs.getNamedItem("AdviceId").getNodeValue());
    }
    catch (Exception e)
    {
      URI adviceId;
      throw new ParsingException("Error parsing required attribute AdviceId in AdviceExpressionType", 
        e);
    }
    URI adviceId;
    try
    {
      effect = attrs.getNamedItem("AppliesTo").getNodeValue();
    }
    catch (Exception e)
    {
      String effect;
      throw new ParsingException("Error parsing required attribute AppliesTo in AdviceExpressionType", 
        e);
    }
    String effect;
    int appliesTo;
    if (effect.equals("Permit"))
    {
      appliesTo = 0;
    }
    else
    {
      int appliesTo;
      if (effect.equals("Deny")) {
        appliesTo = 1;
      } else {
        throw new ParsingException("Invalid Effect type: " + effect);
      }
    }
    int appliesTo;
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("AttributeAssignmentExpression")) {
        try
        {
          AttributeAssignmentExpression expression = 
            AttributeAssignmentExpression.getInstance(node, metaData);
          expressions.add(expression);
        }
        catch (Exception e)
        {
          throw new ParsingException("Error parsing attribute assignments in AdviceExpressionType", 
            e);
        }
      }
    }
    return new AdviceExpression(adviceId, appliesTo, expressions);
  }
  
  public int getAppliesTo()
  {
    return appliesTo;
  }
  
  public URI getAdviceId()
  {
    return adviceId;
  }
  
  public Advice evaluate(EvaluationCtx ctx)
  {
    List<AttributeAssignment> assignments = new ArrayList();
    for (AttributeAssignmentExpression expression : attributeAssignmentExpressions)
    {
      Set<AttributeAssignment> assignmentSet = expression.evaluate(ctx);
      if ((assignmentSet != null) && (assignmentSet.size() > 0)) {
        assignments.addAll(assignmentSet);
      }
    }
    return new Advice(adviceId, assignments);
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<AdviceExpression AdviceId=\"" + adviceId.toString() + "\" AppliesTo=\"" + 
      org.wso2.balana.ctx.xacml2.Result.DECISIONS[appliesTo] + "\">");
    
    indenter.in();
    if ((attributeAssignmentExpressions != null) && (attributeAssignmentExpressions.size() > 0)) {
      for (AttributeAssignmentExpression assignment : attributeAssignmentExpressions) {
        assignment.encode(output, indenter);
      }
    }
    indenter.out();
    
    out.println(indent + "</AdviceExpression>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.AdviceExpression
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.xacml3;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.AbstractObligation;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.EvaluationCtx;

public class ObligationExpression
  extends AbstractObligation
{
  private List<AttributeAssignmentExpression> expressions;
  
  public ObligationExpression(int fulfillOn, List<AttributeAssignmentExpression> expressions, URI obligationId)
  {
    this.fulfillOn = fulfillOn;
    this.expressions = expressions;
    this.obligationId = obligationId;
  }
  
  public static ObligationExpression getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    List<AttributeAssignmentExpression> expressions = 
      new ArrayList();
    if (!root.getNodeName().equals("ObligationExpression")) {
      throw new ParsingException("ObligationExpression object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap nodeAttributes = root.getAttributes();
    URI obligationId;
    String effect;
    try
    {
      obligationId = new URI(nodeAttributes.getNamedItem("ObligationId").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required ObligationId in ObligationExpressionType", 
        e);
    }
    try
    {
      effect = nodeAttributes.getNamedItem("FulfillOn").getNodeValue();
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required FulfillOn in ObligationExpressionType", 
        e);
    }
    int fulfillOn;
    if ("Permit".equals(effect))
    {
      fulfillOn = 0;
    }
    else
    {
      if ("Deny".equals(effect)) {
        fulfillOn = 1;
      } else {
        throw new ParsingException("Invalid FulfillOn : " + effect);
      }
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if ("AttributeAssignmentExpression".equals(child.getNodeName())) {
        expressions.add(AttributeAssignmentExpression.getInstance(child, metaData));
      }
    }
    return new ObligationExpression(fulfillOn, expressions, obligationId);
  }
  
  public ObligationResult evaluate(EvaluationCtx ctx)
  {
    List<AttributeAssignment> assignments = new ArrayList();
    for (AttributeAssignmentExpression expression : expressions)
    {
      Set<AttributeAssignment> assignmentSet = expression.evaluate(ctx);
      if ((assignmentSet != null) && (assignmentSet.size() > 0)) {
        assignments.addAll(assignmentSet);
      }
    }
    return new Obligation(assignments, obligationId);
  }
  
  public String encode()
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.ObligationExpression
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
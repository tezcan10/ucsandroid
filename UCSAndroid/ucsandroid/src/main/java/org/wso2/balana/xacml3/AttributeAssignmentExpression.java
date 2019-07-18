package org.wso2.balana.xacml3;

import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Expression;
import org.wso2.balana.cond.ExpressionHandler;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.EvaluationCtx;

public class AttributeAssignmentExpression
{
  private URI attributeId;
  private URI category;
  private String issuer;
  private Expression expression;
  
  public AttributeAssignmentExpression(URI attributeId, URI category, Expression expression, String issuer)
  {
    this.attributeId = attributeId;
    this.category = category;
    this.expression = expression;
    this.issuer = issuer;
  }
  
  public static AttributeAssignmentExpression getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    URI category = null;
    String issuer = null;
    Expression expression = null;
    if (!root.getNodeName().equals("AttributeAssignmentExpression")) {
      throw new ParsingException("ObligationExpression object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap nodeAttributes = root.getAttributes();
    URI attributeId;
    try
    {
      attributeId = new URI(nodeAttributes.getNamedItem("AttributeId").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required AttributeId in AttributeAssignmentExpressionType",
        e);
    }
    try
    {
      Node categoryNode = nodeAttributes.getNamedItem("Category");
      if (categoryNode != null) {
        category = new URI(categoryNode.getNodeValue());
      }
      Node issuerNode = nodeAttributes.getNamedItem("Issuer");
      if (issuerNode != null) {
        issuer = issuerNode.getNodeValue();
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing optional attributes in AttributeAssignmentExpressionType", 
        e);
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == 1)
      {
        expression = ExpressionHandler.parseExpression(children.item(i), metaData, null);
        break;
      }
    }
    if (expression == null) {
      throw new ParsingException("AttributeAssignmentExpression must contain at least one Expression Type");
    }
    return new AttributeAssignmentExpression(attributeId, category, expression, issuer);
  }
  
  public Set<AttributeAssignment> evaluate(EvaluationCtx ctx)
  {
    Set<AttributeAssignment> values = new HashSet();
    EvaluationResult result = ((Evaluatable)expression).evaluate(ctx);
    if ((result == null) || (result.indeterminate())) {
      return null;
    }
    AttributeValue attributeValue = result.getAttributeValue();
    if (attributeValue != null) {
      if (attributeValue.isBag())
      {
        if (((BagAttribute)attributeValue).size() > 0)
        {
          Iterator iterator = ((BagAttribute)attributeValue).iterator();
          while (iterator.hasNext())
          {
            AttributeValue bagValue = (AttributeValue)iterator.next();
            AttributeAssignment assignment = 
              new AttributeAssignment(attributeId, bagValue.getType(), category, 
              bagValue.encode(), issuer);
            
            values.add(assignment);
          }
        }
        else
        {
          return null;
        }
      }
      else
      {
        AttributeAssignment assignment = 
          new AttributeAssignment(attributeId, attributeValue.getType(), 
          category, attributeValue.encode(), issuer);
        values.add(assignment);
      }
    }
    return values;
  }
  
  public void encode(OutputStream output, Indenter indenter) {}
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.AttributeAssignmentExpression
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
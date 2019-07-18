package org.wso2.balana.xacml3;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;

public class ObligationExpressions
{
  Set<ObligationExpression> obligationExpressions;
  
  public ObligationExpressions(Set<ObligationExpression> obligationExpressions)
  {
    this.obligationExpressions = obligationExpressions;
  }
  
  public static ObligationExpressions getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    Set<ObligationExpression> obligationExpressions = new HashSet();
    
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if ("ObligationExpression".equals(child.getNodeName())) {
        obligationExpressions.add(ObligationExpression.getInstance(child, metaData));
      }
    }
    if (obligationExpressions.isEmpty()) {
      throw new ParsingException("ObligationExpressions must contain at least one ObligationExpression");
    }
    return new ObligationExpressions(obligationExpressions);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.ObligationExpressions
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
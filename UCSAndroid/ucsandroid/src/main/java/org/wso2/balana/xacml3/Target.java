package org.wso2.balana.xacml3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.AbstractTarget;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class Target
  extends AbstractTarget
{
  List<AnyOfSelection> anyOfSelections;
  
  public Target()
  {
    anyOfSelections = new ArrayList();
  }
  
  public Target(List<AnyOfSelection> anyOfSelections)
  {
    this.anyOfSelections = anyOfSelections;
  }
  
  public static Target getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    List<AnyOfSelection> anyOfSelections = new ArrayList();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if ("AnyOf".equals(child.getNodeName())) {
        anyOfSelections.add(AnyOfSelection.getInstance(child, metaData));
      }
    }
    return new Target(anyOfSelections);
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    Iterator it = anyOfSelections.iterator();
    Status firstIndeterminateStatus = null;
    while (it.hasNext())
    {
      AnyOfSelection anyOfSelection = (AnyOfSelection)it.next();
      MatchResult result = anyOfSelection.match(context);
      if (result.getResult() == 1) {
        return result;
      }
      if ((result.getResult() == 2) && 
        (firstIndeterminateStatus == null)) {
        firstIndeterminateStatus = result.getStatus();
      }
    }
    if (firstIndeterminateStatus == null) {
      return new MatchResult(0);
    }
    return new MatchResult(2, 
      firstIndeterminateStatus);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.Target
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
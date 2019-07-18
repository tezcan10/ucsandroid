package org.wso2.balana.xacml3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.TargetMatch;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AllOfSelection
{
  List<TargetMatch> matches;
  
  public AllOfSelection(List<TargetMatch> matches)
  {
    this.matches = matches;
  }
  
  public static AllOfSelection getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    List<TargetMatch> targetMatches = new ArrayList();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if ("Match".equals(child.getNodeName())) {
        targetMatches.add(TargetMatch.getInstance(child, metaData));
      }
    }
    if (targetMatches.isEmpty()) {
      throw new ParsingException("AllOf must contain at least one Match");
    }
    return new AllOfSelection(targetMatches);
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    Iterator it = matches.iterator();
    Status firstIndeterminateStatus = null;
    while (it.hasNext())
    {
      TargetMatch targetMatch = (TargetMatch)it.next();
      MatchResult result = targetMatch.match(context);
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
 * Qualified Name:     org.wso2.balana.xacml3.AllOfSelection
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
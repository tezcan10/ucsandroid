package org.wso2.balana.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.TargetMatch;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AnyOfSelection
{
  private List<AllOfSelection> allOfSelections;
  private static Log logger = LogFactory.getLog(AnyOfSelection.class);
  
  public AnyOfSelection(List<AllOfSelection> allOfSelections)
  {
    if (allOfSelections == null) {
      this.allOfSelections = new ArrayList();
    } else {
      this.allOfSelections = new ArrayList(allOfSelections);
    }
  }
  
  public static AnyOfSelection getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    List<AllOfSelection> allOfSelections = new ArrayList();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if ("AllOf".equals(child.getNodeName())) {
        allOfSelections.add(AllOfSelection.getInstance(child, metaData));
      }
    }
    if (allOfSelections.isEmpty()) {
      throw new ParsingException("AnyOf must contain at least one AllOf");
    }
    return new AnyOfSelection(allOfSelections);
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    Iterator it = allOfSelections.iterator();
    Status firstIndeterminateStatus = null;
    while (it.hasNext())
    {
      AllOfSelection group = (AllOfSelection)it.next();
      MatchResult result = group.match(context);
      if (result.getResult() == 0) {
        return result;
      }
      if ((result.getResult() == 2) && 
        (firstIndeterminateStatus == null)) {
        firstIndeterminateStatus = result.getStatus();
      }
    }
    if (firstIndeterminateStatus == null) {
      return new MatchResult(1);
    }
    return new MatchResult(2, 
      firstIndeterminateStatus);
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    Iterator it = allOfSelections.iterator();
    String name = "Match";
    
    out.println(indent + "<" + name + ">");
    indenter.in();
    while (it.hasNext())
    {
      TargetMatch tm = (TargetMatch)it.next();
      tm.encode(output, indenter);
    }
    out.println(indent + "</" + name + ">");
    indenter.out();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.AnyOfSelection
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
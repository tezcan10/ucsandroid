package org.wso2.balana.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class TargetSection
{
  private List matchGroups;
  private int matchType;
  private int xacmlVersion;
  
  public TargetSection(List matchGroups, int matchType, int xacmlVersion)
  {
    if (matchGroups == null) {
      this.matchGroups = Collections.unmodifiableList(new ArrayList());
    } else {
      this.matchGroups = 
        Collections.unmodifiableList(new ArrayList(matchGroups));
    }
    this.matchType = matchType;
    this.xacmlVersion = xacmlVersion;
  }
  
  public static TargetSection getInstance(Node root, int matchType, PolicyMetaData metaData)
    throws ParsingException
  {
    List groups = new ArrayList();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      String typeName = org.wso2.balana.TargetMatch.NAMES[matchType];
      if (name.equals(typeName)) {
        groups.add(TargetMatchGroup.getInstance(child, matchType, 
          metaData));
      } else {
        if (name.equals("Any" + typeName)) {
          break;
        }
      }
    }
    return new TargetSection(groups, matchType, 
      metaData.getXACMLVersion());
  }
  
  public List getMatchGroups()
  {
    return matchGroups;
  }
  
  public boolean matchesAny()
  {
    return matchGroups.isEmpty();
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    if (matchGroups.isEmpty()) {
      return new MatchResult(0);
    }
    Iterator it = matchGroups.iterator();
    Status firstIndeterminateStatus = null;
    while (it.hasNext())
    {
      TargetMatchGroup group = (TargetMatchGroup)it.next();
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
    String name = org.wso2.balana.TargetMatch.NAMES[matchType];
    if (matchGroups.isEmpty())
    {
      if (xacmlVersion == 0)
      {
        out.println(indent + "<" + name + "s>");
        indenter.in();
        out.println(indenter.makeString() + "<Any" + name + "/>");
        indenter.out();
        out.println(indent + "</" + name + "s>");
      }
    }
    else
    {
      out.println(indent + "<" + name + "s>");
      
      Iterator it = matchGroups.iterator();
      indenter.in();
      while (it.hasNext())
      {
        TargetMatchGroup group = (TargetMatchGroup)it.next();
        group.encode(output, indenter);
      }
      indenter.out();
      
      out.println(indent + "</" + name + "s>");
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml2.TargetSection
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
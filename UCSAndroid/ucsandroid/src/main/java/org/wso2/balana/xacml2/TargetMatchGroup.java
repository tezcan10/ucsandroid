package org.wso2.balana.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
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

public class TargetMatchGroup
{
  private List matches;
  private int matchType;
  private static Log logger = LogFactory.getLog(TargetMatchGroup.class);
  
  public TargetMatchGroup(List matchElements, int matchType)
  {
    if (matchElements == null) {
      matches = Collections.unmodifiableList(new ArrayList());
    } else {
      matches = Collections.unmodifiableList(new ArrayList(matchElements));
    }
    this.matchType = matchType;
  }
  
  public static TargetMatchGroup getInstance(Node root, int matchType, PolicyMetaData metaData)
    throws ParsingException
  {
    List matches = new ArrayList();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      String matchName = TargetMatch.NAMES[matchType] + "Match";
      if (name.equals(matchName)) {
        matches.add(TargetMatch.getInstance(child, matchType, metaData));
      }
    }
    return new TargetMatchGroup(matches, matchType);
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    Iterator it = matches.iterator();
    MatchResult result = null;
    while (it.hasNext())
    {
      TargetMatch tm = (TargetMatch)it.next();
      result = tm.match(context);
      if (result.getResult() != 0) {
        break;
      }
    }
    return result;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    Iterator it = matches.iterator();
    String name = TargetMatch.NAMES[matchType];
    
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
 * Qualified Name:     org.wso2.balana.xacml2.TargetMatchGroup
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
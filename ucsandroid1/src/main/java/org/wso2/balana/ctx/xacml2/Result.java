package org.wso2.balana.ctx.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.xacml2.Obligation;

public class Result
  extends AbstractResult
{
  private String resourceId = null;
  
  public Result(int decision, Status status)
  {
    super(decision, status);
  }
  
  public Result(int decision, Status status, List<ObligationResult> obligationResults)
    throws IllegalArgumentException
  {
    super(decision, status, obligationResults, null);
  }
  
  public Result(int decision, Status status, List<ObligationResult> obligationResults, String resourceId)
    throws IllegalArgumentException
  {
    super(decision, status, obligationResults, null);
    this.resourceId = resourceId;
  }
  
  public static AbstractResult getInstance(Node root)
    throws ParsingException
  {
    int decision = -1;
    Status status = null;
    String resource = null;
    List<ObligationResult> obligations = null;
    
    NamedNodeMap attrs = root.getAttributes();
    Node resourceAttr = attrs.getNamedItem("ResourceId");
    if (resourceAttr != null) {
      resource = resourceAttr.getNodeValue();
    }
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      String name = node.getNodeName();
      if (name.equals("Decision"))
      {
        String type = node.getFirstChild().getNodeValue();
        for (int j = 0; j < DECISIONS.length; j++) {
          if (DECISIONS[j].equals(type))
          {
            decision = j;
            break;
          }
        }
        if (decision == -1) {
          throw new ParsingException("Unknown Decision: " + type);
        }
      }
      else if (name.equals("Status"))
      {
        if (status == null) {
          status = Status.getInstance(node);
        } else {
          throw new ParsingException("More than one StatusType defined");
        }
      }
      else if (name.equals("Obligations"))
      {
        if (obligations == null) {
          obligations = parseObligations(node);
        } else {
          throw new ParsingException("More than one ObligationsType defined");
        }
      }
    }
    return new Result(decision, status, obligations, resource);
  }
  
  private static List<ObligationResult> parseObligations(Node root)
    throws ParsingException
  {
    List<ObligationResult> list = new ArrayList();
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Obligation")) {
        list.add(Obligation.getInstance(node));
      }
    }
    if (list.size() == 0) {
      throw new ParsingException("ObligationsType must not be empty");
    }
    return list;
  }
  
  public String getResourceId()
  {
    return resourceId;
  }
  
  public boolean setResource(String resource)
  {
    if (resourceId != null) {
      return false;
    }
    resourceId = resource;
    
    return true;
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    indenter.in();
    String indentNext = indenter.makeString();
    if (resourceId == null) {
      out.println(indent + "<Result>");
    } else {
      out.println(indent + "<Result ResourceId=\"" + resourceId + "\">");
    }
    out.println(indentNext + "<Decision>" + DECISIONS[decision] + "</Decision>");
    if (status != null) {
      status.encode(output, indenter);
    }
    if ((obligations != null) && (obligations.size() != 0))
    {
      out.println(indentNext + "<Obligations>");
      
      Iterator it = obligations.iterator();
      indenter.in();
      while (it.hasNext())
      {
        ObligationResult obligation = (ObligationResult)it.next();
        obligation.encode(output, indenter);
      }
      indenter.out();
      out.println(indentNext + "</Obligations>");
    }
    indenter.out();
    
    out.println(indent + "</Result>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml2.Result
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AttributeAssignment;

public class Obligation
  implements ObligationResult
{
  private URI obligationId;
  private List<AttributeAssignment> assignments;
  
  public Obligation(List<AttributeAssignment> assignments, URI obligationId)
  {
    this.assignments = assignments;
    this.obligationId = obligationId;
  }
  
  public static Obligation getInstance(Node root)
    throws ParsingException
  {
    List<AttributeAssignment> assignments = new ArrayList();
    if (!root.getNodeName().equals("Obligation")) {
      throw new ParsingException("Obligation object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap nodeAttributes = root.getAttributes();
    URI obligationId;
    try
    {
      obligationId = new URI(nodeAttributes.getNamedItem("ObligationId").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required ObligationId in ObligationType", 
        e);
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if ("AttributeAssignment".equals(child.getNodeName())) {
        assignments.add(AttributeAssignment.getInstance(child));
      }
    }
    return new Obligation(assignments, obligationId);
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    String indent = indenter.makeString();
    PrintStream out = new PrintStream(output);
    
    out.println(indent + "<Obligation ObligationId=\"" + obligationId + "\">");
    
    indenter.in();
    if ((assignments != null) && (assignments.size() > 0)) {
      for (AttributeAssignment assignment : assignments) {
        assignment.encode(output, indenter);
      }
    }
    indenter.out();
    
    out.println(indent + "</Obligation>");
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.Obligation
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
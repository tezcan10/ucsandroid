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
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AttributeAssignment;

public class Advice
{
  private URI adviceId;
  private List<AttributeAssignment> assignments;
  
  public Advice(URI adviceId, List<AttributeAssignment> assignments)
  {
    this.adviceId = adviceId;
    this.assignments = assignments;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public static Advice getInstance(Node root)
    throws ParsingException
  {
    List<AttributeAssignment> assignments = new ArrayList();
    if (!root.getNodeName().equals("Advice")) {
      throw new ParsingException("Advice object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap nodeAttributes = root.getAttributes();
    URI adviceId;
    try
    {
      adviceId = new URI(nodeAttributes.getNamedItem("AdviceId").getNodeValue());
    }
    catch (Exception e)
    {

      throw new ParsingException("Error parsing required AdviceId in AdviceType", 
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
    return new Advice(adviceId, assignments);
  }
  
  public URI getAdviceId()
  {
    return adviceId;
  }
  
  public List<AttributeAssignment> getAssignments()
  {
    return assignments;
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<Advice AdviceId=\"" + adviceId + "\" >");
    
    indenter.in();
    if ((assignments != null) && (assignments.size() > 0)) {
      for (AttributeAssignment assignment : assignments) {
        assignment.encode(output, indenter);
      }
    }
    indenter.out();
    out.println(indent + "</Advice>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.Advice
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
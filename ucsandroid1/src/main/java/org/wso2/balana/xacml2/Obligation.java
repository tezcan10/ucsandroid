package org.wso2.balana.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.AbstractObligation;
import org.wso2.balana.Balana;
import org.wso2.balana.Indenter;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class Obligation
  extends AbstractObligation
  implements ObligationResult
{
  private List<Attribute> assignments;
  
  public Obligation(URI obligationId, int fulfillOn, List<Attribute> assignments)
  {
    this.obligationId = obligationId;
    this.fulfillOn = fulfillOn;
    this.assignments = Collections.unmodifiableList(new ArrayList(assignments));
  }
  
  public static Obligation getInstance(Node root)
    throws ParsingException
  {
    List<Attribute> assignments = new ArrayList();
    
    AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      id = new URI(attrs.getNamedItem("ObligationId").getNodeValue());
    }
    catch (Exception e)
    {
      URI id;
      throw new ParsingException("Error parsing required attribute ObligationId", e);
    }
    URI id;
    try
    {
      effect = attrs.getNamedItem("FulfillOn").getNodeValue();
    }
    catch (Exception e)
    {
      String effect;
      throw new ParsingException("Error parsing required attribute FulfillOn", e);
    }
    String effect;
    int fulfillOn;
    if (effect.equals("Permit"))
    {
      fulfillOn = 0;
    }
    else
    {
      int fulfillOn;
      if (effect.equals("Deny")) {
        fulfillOn = 1;
      } else {
        throw new ParsingException("Invalid Effect type: " + effect);
      }
    }
    int fulfillOn;
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("AttributeAssignment")) {
        try
        {
          URI attrId = new URI(node.getAttributes().getNamedItem("AttributeId")
            .getNodeValue());
          AttributeValue attrValue = attrFactory.createValue(node);
          assignments.add(new Attribute(attrId, null, null, attrValue, 
            2));
        }
        catch (URISyntaxException use)
        {
          throw new ParsingException("Error parsing URI", use);
        }
        catch (UnknownIdentifierException uie)
        {
          throw new ParsingException(uie.getMessage(), uie);
        }
        catch (Exception e)
        {
          throw new ParsingException("Error parsing attribute assignments", e);
        }
      }
    }
    return new Obligation(id, fulfillOn, assignments);
  }
  
  public ObligationResult evaluate(EvaluationCtx ctx)
  {
    return new Obligation(obligationId, fulfillOn, assignments);
  }
  
  public URI getId()
  {
    return obligationId;
  }
  
  public List<Attribute> getAssignments()
  {
    return assignments;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<Obligation ObligationId=\"" + obligationId.toString() + "\" FulfillOn=\"" + 
      org.wso2.balana.ctx.xacml2.Result.DECISIONS[fulfillOn] + "\">");
    
    indenter.in();
    for (Attribute assignment : assignments) {
      out.println(indenter.makeString() + "<AttributeAssignment AttributeId=\"" + 
        assignment.getId().toString() + "\" DataType=\"" + assignment.getType().toString() + 
        "\">" + assignment.getValue().encode() + "</AttributeAssignment>");
    }
    indenter.out();
    
    out.println(indent + "</Obligation>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml2.Obligation
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
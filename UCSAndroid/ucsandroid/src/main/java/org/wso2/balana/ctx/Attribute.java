package org.wso2.balana.ctx;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Balana;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;

public class Attribute
{
  private URI id;
  private URI type;
  private boolean includeInResult;
  private String issuer = null;
  private DateTimeAttribute issueInstant = null;
  private List<AttributeValue> attributeValues;
  private int xacmlVersion;
  
  public Attribute(URI id, String issuer, DateTimeAttribute issueInstant, AttributeValue value, boolean includeInResult, int version)
  {
    this(id, value.getType(), issuer, issueInstant, Arrays.asList(new AttributeValue[] { value }), includeInResult, version);
  }
  
  public Attribute(URI id, String issuer, DateTimeAttribute issueInstant, AttributeValue value, int version)
  {
    this(id, value.getType(), issuer, issueInstant, Arrays.asList(new AttributeValue[] { value }), false, version);
  }
  
  public Attribute(URI id, URI type, String issuer, DateTimeAttribute issueInstant, List<AttributeValue> attributeValues, boolean includeInResult, int xacmlVersion)
  {
    this.id = id;
    this.type = type;
    this.issuer = issuer;
    this.issueInstant = issueInstant;
    this.attributeValues = attributeValues;
    this.includeInResult = includeInResult;
    this.xacmlVersion = xacmlVersion;
  }
  
  public static Attribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root, 3);
  }
  
  public static Attribute getInstance(Node root, int version)
    throws ParsingException
  {
    URI id = null;
    URI type = null;
    String issuer = null;
    DateTimeAttribute issueInstant = null;
    List<AttributeValue> values = new ArrayList();
    boolean includeInResult = false;
    
    AttributeFactory attributeFactory = Balana.getInstance().getAttributeFactory();
    if (!root.getNodeName().equals("Attribute")) {
      throw new ParsingException("Attribute object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute AttributeId in AttributeType", 
        e);
    }
    if (version != 3) {
      try
      {
        type = new URI(attrs.getNamedItem("DataType").getNodeValue());
      }
      catch (Exception e)
      {
        throw new ParsingException("Error parsing required attribute DataType in AttributeType", 
          e);
      }
    }
    if (version == 3) {
      try
      {
        String includeInResultString = attrs.getNamedItem("IncludeInResult").getNodeValue();
        if ("true".equals(includeInResultString)) {
          includeInResult = true;
        }
      }
      catch (Exception e)
      {
        throw new ParsingException("Error parsing required attribute IncludeInResult in AttributeType", 
          e);
      }
    }
    try
    {
      Node issuerNode = attrs.getNamedItem("Issuer");
      if (issuerNode != null) {
        issuer = issuerNode.getNodeValue();
      }
      if (version != 3)
      {
        Node instantNode = attrs.getNamedItem("IssueInstant");
        if (instantNode != null) {
          issueInstant = DateTimeAttribute.getInstance(instantNode.getNodeValue());
        }
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing optional AttributeType attribute", e);
    }
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("AttributeValue"))
      {
        if (version == 3)
        {
          NamedNodeMap dataTypeAttribute = node.getAttributes();
          try
          {
            type = new URI(dataTypeAttribute.getNamedItem("DataType").getNodeValue());
          }
          catch (Exception e)
          {
            throw new ParsingException("Error parsing required attribute DataType in AttributeType", 
              e);
          }
        }
        try
        {
          values.add(attributeFactory.createValue(node, type));
        }
        catch (UnknownIdentifierException uie)
        {
          throw new ParsingException(uie.getMessage(), uie);
        }
      }
    }
    if (values.size() < 1) {
      throw new ParsingException("Attribute must contain a value");
    }
    return new Attribute(id, type, issuer, issueInstant, values, includeInResult, version);
  }
  
  public URI getId()
  {
    return id;
  }
  
  public URI getType()
  {
    return type;
  }
  
  public String getIssuer()
  {
    return issuer;
  }
  
  public DateTimeAttribute getIssueInstant()
  {
    return issueInstant;
  }
  
  public boolean isIncludeInResult()
  {
    return includeInResult;
  }
  
  public List<AttributeValue> getValues()
  {
    return attributeValues;
  }
  
  public AttributeValue getValue()
  {
    if (attributeValues != null) {
      return (AttributeValue)attributeValues.get(0);
    }
    return null;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    out.println(indent + "<Attribute AttributeId=\"" + id.toString() + "\"");
    if (xacmlVersion == 3)
    {
      out.print(" IncludeInResult=\"" + includeInResult + "\"");
    }
    else
    {
      out.println(" DataType=\"" + type.toString() + "\"");
      if (issueInstant != null) {
        out.print(" IssueInstant=\"" + issueInstant.encode() + "\"");
      }
    }
    if (issuer != null) {
      out.print(" Issuer=\"" + issuer + "\"");
    }
    out.print(">");
    indenter.in();
    if ((attributeValues != null) && (attributeValues.size() > 0)) {
      for (AttributeValue value : attributeValues) {
        out.println(value.encodeWithTags(true));
      }
    }
    indenter.out();
    
    out.println(indent + "</Attribute>");
  }
  
  public String encode()
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.Attribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
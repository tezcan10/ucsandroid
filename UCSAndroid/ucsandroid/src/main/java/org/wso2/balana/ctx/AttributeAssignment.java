package org.wso2.balana.ctx;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.AttributeValue;

public class AttributeAssignment
  extends AttributeValue
{
  private URI attributeId;
  private URI category;
  private String issuer;
  private String content;
  
  public AttributeAssignment(URI attributeId, URI dataType, URI category, String content, String issuer)
  {
    super(dataType);
    this.attributeId = attributeId;
    this.category = category;
    this.issuer = issuer;
    this.content = content;
  }
  
  public static AttributeAssignment getInstance(Node root)
    throws ParsingException
  {
    URI category = null;
    
    String issuer = null;
    String content = null;
    if (!root.getNodeName().equals("AttributeAssignment")) {
      throw new ParsingException("AttributeAssignment object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap nodeAttributes = root.getAttributes();
    try
    {
      attributeId = new URI(nodeAttributes.getNamedItem("AttributeId").getNodeValue());
    }
    catch (Exception e)
    {
      URI attributeId;
      throw new ParsingException("Error parsing required AttributeId in AttributeAssignmentType", 
        e);
    }
    URI attributeId;
    try
    {
      type = new URI(nodeAttributes.getNamedItem("DataType").getNodeValue());
    }
    catch (Exception e)
    {
      URI type;
      throw new ParsingException("Error parsing required AttributeId in AttributeAssignmentType", 
        e);
    }
    URI type;
    try
    {
      Node categoryNode = nodeAttributes.getNamedItem("Category");
      if (categoryNode != null) {
        category = new URI(categoryNode.getNodeValue());
      }
      Node issuerNode = nodeAttributes.getNamedItem("Issuer");
      if (issuerNode != null) {
        issuer = issuerNode.getNodeValue();
      }
      content = root.getTextContent();
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing optional attributes in AttributeAssignmentType", 
        e);
    }
    return new AttributeAssignment(attributeId, type, category, content, issuer);
  }
  
  public URI getAttributeId()
  {
    return attributeId;
  }
  
  public URI getCategory()
  {
    return category;
  }
  
  public String getIssuer()
  {
    return issuer;
  }
  
  public String getContent()
  {
    return content;
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    
    out.print("<AttributeAssignment  AttributeId=\"" + attributeId + "\"");
    
    out.print(" DataType=\"" + getType() + "\"");
    if (category != null) {
      out.print(" Category=\"" + category + "\"");
    }
    if (issuer != null) {
      out.print("\" Issuer=\"" + issuer + "\"");
    }
    out.print(">");
    if (content != null) {
      out.print(content);
    }
    out.println("</AttributeAssignment>");
  }
  
  public String encode()
  {
    OutputStream stream = new ByteArrayOutputStream();
    encode(stream);
    return stream.toString();
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.AttributeAssignment
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
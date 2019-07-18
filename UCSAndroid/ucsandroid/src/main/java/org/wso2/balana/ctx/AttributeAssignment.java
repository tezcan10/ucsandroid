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

public class AttributeAssignment extends AttributeValue {
  private URI attributeId;
  private URI category;
  private String issuer;
  private String content;

  public AttributeAssignment(URI attributeId, URI dataType, URI category, String content, String issuer) {
    super(dataType);
    this.attributeId = attributeId;
    this.category = category;
    this.issuer = issuer;
    this.content = content;
  }

  public static AttributeAssignment getInstance(Node root) throws ParsingException {
    URI category = null;
    String issuer = null;
    String content = null;
    if (!root.getNodeName().equals("AttributeAssignment")) {
      throw new ParsingException("AttributeAssignment object cannot be created with root node of type: " + root.getNodeName());
    } else {
      NamedNodeMap nodeAttributes = root.getAttributes();

      URI attributeId;
      try {
        attributeId = new URI(nodeAttributes.getNamedItem("AttributeId").getNodeValue());
      } catch (Exception var10) {
        throw new ParsingException("Error parsing required AttributeId in AttributeAssignmentType", var10);
      }

      URI type;
      try {
        type = new URI(nodeAttributes.getNamedItem("DataType").getNodeValue());
      } catch (Exception var9) {
        throw new ParsingException("Error parsing required AttributeId in AttributeAssignmentType", var9);
      }

      try {
        Node categoryNode = nodeAttributes.getNamedItem("Category");
        if (categoryNode != null) {
          category = new URI(categoryNode.getNodeValue());
        }

        Node issuerNode = nodeAttributes.getNamedItem("Issuer");
        if (issuerNode != null) {
          issuer = issuerNode.getNodeValue();
        }

        content = root.getTextContent();
      } catch (Exception var11) {
        throw new ParsingException("Error parsing optional attributes in AttributeAssignmentType", var11);
      }

      return new AttributeAssignment(attributeId, type, category, content, issuer);
    }
  }

  public URI getAttributeId() {
    return this.attributeId;
  }

  public URI getCategory() {
    return this.category;
  }

  public String getIssuer() {
    return this.issuer;
  }

  public String getContent() {
    return this.content;
  }

  public void encode(OutputStream output, Indenter indenter) {
    PrintStream out = new PrintStream(output);
    out.print("<AttributeAssignment  AttributeId=\"" + this.attributeId + "\"");
    out.print(" DataType=\"" + this.getType() + "\"");
    if (this.category != null) {
      out.print(" Category=\"" + this.category + "\"");
    }

    if (this.issuer != null) {
      out.print("\" Issuer=\"" + this.issuer + "\"");
    }

    out.print(">");
    if (this.content != null) {
      out.print(this.content);
    }

    out.println("</AttributeAssignment>");
  }

  public String encode() {
    OutputStream stream = new ByteArrayOutputStream();
    this.encode(stream);
    return stream.toString();
  }

  public void encode(OutputStream output) {
    this.encode(output, new Indenter(0));
  }
}


/* Location:
 * Qualified Name:     org.wso2.balana.ctx.AttributeAssignment
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.Attribute;

public class Attributes
{
  private URI category;
  private Node content;
  private Set<Attribute> attributes;
  private String id;
  
  public Attributes(URI category, Set<Attribute> attributes)
  {
    this(category, null, attributes, null);
  }
  
  public Attributes(URI category, Node content, Set<Attribute> attributes, String id)
  {
    this.category = category;
    this.content = content;
    this.attributes = attributes;
    this.id = id;
  }
  
  public static Attributes getInstance(Node root)
    throws ParsingException
  {
    Node content = null;
    String id = null;
    Set<Attribute> attributes = new HashSet();
    if (!root.getNodeName().equals("Attributes")) {
      throw new ParsingException("Attributes object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap attrs = root.getAttributes();
    URI category;
    try
    {
      category = new URI(attrs.getNamedItem("Category").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute AttributeId in AttributesType", 
        e);
    }
    try
    {
      Node idNode = attrs.getNamedItem("id");
      if (idNode != null) {
        id = idNode.getNodeValue();
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing optional attributes in AttributesType", 
        e);
    }
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Content"))
      {
        if (content != null) {
          throw new ParsingException("Too many content elements are defined.");
        }
        content = node;
      }
      else if (node.getNodeName().equals("Attribute"))
      {
        attributes.add(Attribute.getInstance(node, 3));
      }
    }
    return new Attributes(category, content, attributes, id);
  }
  
  public URI getCategory()
  {
    return category;
  }
  
  public Object getContent()
  {
    return content;
  }
  
  public Set<Attribute> getAttributes()
  {
    return attributes;
  }
  
  public String getId()
  {
    return id;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    String indent = indenter.makeString();
    PrintStream out = new PrintStream(output);
    
    out.println(indent + "<Attributes Category=\"" + category.toString() + "\">");
    
    indenter.in();
    for (Attribute attribute : attributes) {
      if (attribute.isIncludeInResult()) {
        attribute.encode(output, indenter);
      }
    }
    indenter.out();
    
    indenter.in();
    
    out.println(indent + "</Attributes>");
  }
  
  public void encodeIncludeAttribute(OutputStream output, Indenter indenter)
  {
    String indent = indenter.makeString();
    PrintStream out = new PrintStream(output);
    boolean atLestOneAttribute = false;
    for (Attribute attribute : attributes) {
      if (attribute.isIncludeInResult())
      {
        if (!atLestOneAttribute)
        {
          out.println(indent + "<Attributes Category=\"" + category.toString() + "\">");
          indenter.in();
        }
        atLestOneAttribute = true;
        attribute.encode(output, indenter);
      }
    }
    if (atLestOneAttribute)
    {
      indenter.out();
      indenter.in();
      
      out.println(indent + "</Attributes>");
    }
  }
  
  public void encodeWithIncludedAttributes(OutputStream output, Indenter indenter)
  {
    String indent = indenter.makeString();
    PrintStream out = new PrintStream(output);
    
    out.println(indent + "<Attributes Category=\"" + category.toString() + "\">");
    
    indenter.in();
    for (Attribute attribute : attributes) {
      if (attribute.isIncludeInResult()) {
        attribute.encode(output, indenter);
      }
    }
    indenter.out();
    
    indenter.in();
    
    out.println(indent + "</Attributes>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.Attributes
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
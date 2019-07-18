package org.wso2.balana.ctx;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Balana;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;

public class MissingAttributeDetail
{
  private URI id;
  private URI type;
  private URI category;
  private String issuer = null;
  private List<AttributeValue> attributeValues;
  private int xacmlVersion;
  
  public MissingAttributeDetail(URI id, URI type, URI category, String issuer, List<AttributeValue> attributeValues, int xacmlVersion)
  {
    this.id = id;
    this.type = type;
    this.category = category;
    this.issuer = issuer;
    this.attributeValues = attributeValues;
    this.xacmlVersion = xacmlVersion;
  }
  
  public MissingAttributeDetail(URI id, URI type, URI category, List<AttributeValue> attributeValues, int xacmlVersion)
  {
    this(id, type, category, null, attributeValues, xacmlVersion);
  }
  
  public MissingAttributeDetail(URI id, URI type, URI category, int xacmlVersion)
  {
    this(id, type, category, null, null, xacmlVersion);
  }
  
  public static MissingAttributeDetail getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    URI id = null;
    URI type = null;
    URI category = null;
    String issuer = null;
    List<AttributeValue> values = new ArrayList();
    int version = metaData.getXACMLVersion();
    
    AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
    if (!root.getNodeName().equals("MissingAttributeDetail")) {
      throw new ParsingException("MissingAttributeDetailType object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute AttributeId in MissingAttributeDetailType", 
        e);
    }
    try
    {
      type = new URI(attrs.getNamedItem("DataType").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute DataType in MissingAttributeDetailType", 
        e);
    }
    if (version == 3) {
      try
      {
        category = new URI(attrs.getNamedItem("IncludeInResult").getNodeValue());
      }
      catch (Exception e)
      {
        throw new ParsingException("Error parsing required attribute Category in MissingAttributeDetailType", 
          e);
      }
    }
    try
    {
      Node issuerNode = attrs.getNamedItem("Issuer");
      if (issuerNode != null) {
        issuer = issuerNode.getNodeValue();
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing optional attributes in MissingAttributeDetailType", e);
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
            throw new ParsingException("Error parsing required attribute DataType in MissingAttributeDetailType", 
              e);
          }
        }
        try
        {
          values.add(attrFactory.createValue(node, type));
        }
        catch (UnknownIdentifierException uie)
        {
          throw new ParsingException("Unknown AttributeValue", uie);
        }
      }
    }
    return new MissingAttributeDetail(id, type, category, issuer, values, version);
  }
  
  public String getEncoded()
    throws ParsingException
  {
    if (id != null) {
      throw new ParsingException("Required AttributeId attribute is Null");
    }
    if (type != null) {
      throw new ParsingException("Required DataType attribute is Null");
    }
    if ((xacmlVersion == 3) && (category != null)) {
      throw new ParsingException("Required Category attribute is Null");
    }
    String encoded = "<MissingAttributeDetail AttributeId=" + id + " DataType=" + type;
    if (xacmlVersion == 3) {
      encoded = encoded + " Category=" + category;
    }
    if (issuer != null) {
      encoded = encoded + " Issuer=" + issuer;
    }
    encoded = encoded + " >";
    if ((attributeValues != null) && (attributeValues.size() > 0)) {
      for (AttributeValue value : attributeValues) {
        encoded = encoded + value.encodeWithTags(true) + "\n";
      }
    }
    encoded = encoded + "</MissingAttributeDetail>";
    
    return encoded;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.MissingAttributeDetail
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
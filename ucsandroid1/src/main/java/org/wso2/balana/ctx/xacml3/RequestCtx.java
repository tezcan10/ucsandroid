package org.wso2.balana.ctx.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.balana.xacml3.MultiRequests;
import org.wso2.balana.xacml3.RequestDefaults;

public class RequestCtx
  extends AbstractRequestCtx
{
  private boolean returnPolicyIdList;
  private boolean combinedDecision;
  private MultiRequests multiRequests;
  private RequestDefaults defaults;
  
  public RequestCtx(Set<Attributes> attributesSet, Node documentRoot)
  {
    this(documentRoot, attributesSet, false, false, null, null);
  }
  
  public RequestCtx(Node documentRoot, Set<Attributes> attributesSet, boolean returnPolicyIdList, boolean combinedDecision, MultiRequests multiRequests, RequestDefaults defaults)
    throws IllegalArgumentException
  {
    xacmlVersion = 3;
    this.documentRoot = documentRoot;
    this.attributesSet = attributesSet;
    this.returnPolicyIdList = returnPolicyIdList;
    this.combinedDecision = combinedDecision;
    this.multiRequests = multiRequests;
    this.defaults = defaults;
  }
  
  public static RequestCtx getInstance(Node root)
    throws ParsingException
  {
    boolean returnPolicyIdList = false;
    boolean combinedDecision = false;
    MultiRequests multiRequests = null;
    RequestDefaults defaults = null;
    
    String tagName = root.getNodeName();
    if (!tagName.equals("Request")) {
      throw new ParsingException("Request cannot be constructed using type: " + 
        root.getNodeName());
    }
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      String attributeValue = attrs.getNamedItem("ReturnPolicyIdList")
        .getNodeValue();
      if ("true".equals(attributeValue)) {
        returnPolicyIdList = true;
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute ReturnPolicyIdList in RequestType", 
        e);
    }
    try
    {
      String attributeValue = attrs.getNamedItem("CombinedDecision")
        .getNodeValue();
      if ("true".equals(attributeValue)) {
        combinedDecision = true;
      }
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute CombinedDecision in RequestType", 
        e);
    }
    Set<Attributes> attributesElements = new HashSet();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node node = children.item(i);
      String tag = node.getNodeName();
      if (tag.equals("Attributes"))
      {
        Attributes attributes = Attributes.getInstance(node);
        attributesElements.add(attributes);
      }
      if (tag.equals("MultiRequests"))
      {
        if (multiRequests != null) {
          throw new ParsingException("Too many MultiRequests elements are defined.");
        }
        multiRequests = MultiRequests.getInstance(node);
      }
      if (tag.equals("RequestDefaults"))
      {
        if (multiRequests != null) {
          throw new ParsingException("Too many RequestDefaults elements are defined.");
        }
        defaults = RequestDefaults.getInstance(node);
      }
    }
    if (attributesElements.isEmpty()) {
      throw new ParsingException("Request must contain at least one AttributesType");
    }
    return new RequestCtx(root, attributesElements, returnPolicyIdList, combinedDecision, 
      multiRequests, defaults);
  }
  
  public boolean isCombinedDecision()
  {
    return combinedDecision;
  }
  
  public boolean isReturnPolicyIdList()
  {
    return returnPolicyIdList;
  }
  
  public MultiRequests getMultiRequests()
  {
    return multiRequests;
  }
  
  public RequestDefaults getDefaults()
  {
    return defaults;
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    String indent = indenter.makeString();
    PrintStream out = new PrintStream(output);
    
    out.println(indent + "<Request xmlns=\"" + "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" + 
      "\" ReturnPolicyIdList=\"" + returnPolicyIdList + "\" CombinedDecision=\"" + 
      combinedDecision + "\" >");
    
    indenter.in();
    for (Attributes attributes : attributesSet) {
      attributes.encode(output, indenter);
    }
    if (defaults != null) {
      defaults.encode(output, indenter);
    }
    indenter.out();
    
    out.println(indent + "</Request>");
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml3.RequestCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
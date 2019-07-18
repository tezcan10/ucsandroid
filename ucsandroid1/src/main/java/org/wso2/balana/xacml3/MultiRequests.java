package org.wso2.balana.xacml3;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.ParsingException;

public class MultiRequests
{
  private Set<RequestReference> requestReferences;
  
  private MultiRequests(Set<RequestReference> requestReferences)
  {
    this.requestReferences = requestReferences;
  }
  
  public static MultiRequests getInstance(Node root)
    throws ParsingException
  {
    Set<RequestReference> requestReferences = new HashSet();
    if (!root.getNodeName().equals("MultiRequests")) {
      throw new ParsingException("MultiRequests object cannot be created with root node of type: " + 
        root.getNodeName());
    }
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if ("RequestReference".equals(node.getNodeName()))
      {
        Set<AttributesReference> attributesReferences = new HashSet();
        RequestReference requestReference = new RequestReference();
        NodeList childNodes = node.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++)
        {
          Node childNode = childNodes.item(i);
          if ("AttributesReference".equals(childNode.getNodeName()))
          {
            AttributesReference attributesReference = new AttributesReference();
            attributesReferences.add(attributesReference);
            NamedNodeMap nodeAttributes = root.getAttributes();
            try
            {
              String referenceId = nodeAttributes.getNamedItem("ReferenceId").getNodeValue();
              attributesReference.setId(referenceId);
            }
            catch (Exception e)
            {
              throw new ParsingException("Error parsing required ReferenceId in MultiRequestsType", 
                e);
            }
          }
        }
        if (attributesReferences.isEmpty()) {
          throw new ParsingException("RequestReference must contain at least one AttributesReference");
        }
        requestReference.setReferences(attributesReferences);
        requestReferences.add(requestReference);
      }
    }
    if (requestReferences.isEmpty()) {
      throw new ParsingException("MultiRequests must contain at least one RequestReference");
    }
    return new MultiRequests(requestReferences);
  }
  
  public Set<RequestReference> getRequestReferences()
  {
    return requestReferences;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.MultiRequests
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
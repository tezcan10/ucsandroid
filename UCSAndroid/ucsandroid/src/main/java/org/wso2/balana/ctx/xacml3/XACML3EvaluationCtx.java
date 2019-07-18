package org.wso2.balana.ctx.xacml3;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Balana;
import org.wso2.balana.DefaultNamespaceContext;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.Utils;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.attr.xacml3.XPathAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.BasicEvaluationCtx;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.ResourceFinder;
import org.wso2.balana.finder.ResourceFinderResult;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.balana.xacml3.AttributesReference;
import org.wso2.balana.xacml3.MultiRequests;
import org.wso2.balana.xacml3.MultipleCtxResult;
import org.wso2.balana.xacml3.RequestReference;

public class XACML3EvaluationCtx
  extends BasicEvaluationCtx
{
  private Set<Attributes> attributesSet;
  private Set<Attributes> multipleContentSelectors;
  private boolean multipleAttributes;
  private Set<PolicyReference> policyReferences;
  private Map<String, Set<Attributes>> mapAttributes;
  private RequestCtx requestCtx;
  private int resourceScope;
  private AttributeValue resourceId;
  private static Log logger = LogFactory.getLog(XACML3EvaluationCtx.class);
  
  public XACML3EvaluationCtx(RequestCtx requestCtx, PDPConfig pdpConfig)
  {
    currentDate = null;
    currentTime = null;
    currentDateTime = null;
    
    mapAttributes = new HashMap();
    
    attributesSet = requestCtx.getAttributesSet();
    this.pdpConfig = pdpConfig;
    this.requestCtx = requestCtx;
    
    setupAttributes(attributesSet, mapAttributes);
  }
  
  public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category)
  {
    List<AttributeValue> attributeValues = new ArrayList();
    Set<Attributes> attributesSet = (Set)mapAttributes.get(category.toString());
    if ((attributesSet != null) && (attributesSet.size() > 0))
    {
      Set<Attribute> attributeSet = ((Attributes)attributesSet.iterator().next()).getAttributes();
      for (Attribute attribute : attributeSet) {
        if ((attribute.getId().equals(id)) && (attribute.getType().equals(type)) && 
          ((issuer == null) || (issuer.equals(attribute.getIssuer()))) && 
          (attribute.getValue() != null)) {
          attributeValues.add(attribute.getValue());
        }
      }
      if (attributeValues.size() < 1) {
        return callHelper(type, id, issuer, category);
      }
    }
    return new EvaluationResult(new BagAttribute(type, attributeValues));
  }
  
  public EvaluationResult getAttribute(String path, URI type, URI category, URI contextSelector, String xpathVersion)
  {
    if (pdpConfig.getAttributeFinder() == null)
    {
      logger.warn("Context tried to invoke AttributeFinder but was not configured with one");
      
      return new EvaluationResult(BagAttribute.createEmptyBag(type));
    }
    Set<Attributes> attributesSet = null;
    if (category != null) {
      attributesSet = (Set)mapAttributes.get(category.toString());
    }
    if ((attributesSet != null) && (attributesSet.size() > 0))
    {
      Attributes attributes = (Attributes)attributesSet.iterator().next();
      Object content = attributes.getContent();
      if ((content instanceof Node))
      {
        Node root = (Node)content;
        if ((contextSelector != null) && (contextSelector.toString().trim().length() > 0)) {
          for (Attribute attribute : attributes.getAttributes()) {
            if (attribute.getId().equals(contextSelector))
            {
              List<AttributeValue> values = attribute.getValues();
              for (AttributeValue value : values) {
                if ((value instanceof XPathAttribute))
                {
                  XPathAttribute xPathAttribute = (XPathAttribute)value;
                  if (xPathAttribute.getXPathCategory().equals(category.toString())) {
                    return pdpConfig.getAttributeFinder().findAttribute(path, 
                      xPathAttribute.getValue(), type, 
                      root, this, xpathVersion);
                  }
                }
              }
            }
          }
        } else {
          return pdpConfig.getAttributeFinder().findAttribute(path, null, type, 
            root, this, xpathVersion);
        }
      }
    }
    return new EvaluationResult(BagAttribute.createEmptyBag(type));
  }
  
  public int getXacmlVersion()
  {
    return requestCtx.getXacmlVersion();
  }
  
  private void setupAttributes(Set<Attributes> attributeSet, Map<String, Set<Attributes>> mapAttributes)
  {
    for (Attributes attributes : attributeSet)
    {
      String category = attributes.getCategory().toString();
      for (Attribute attribute : attributes.getAttributes())
      {
        if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(category))
        {
          if ("urn:oasis:names:tc:xacml:2.0:resource:scope".equals(attribute.getId().toString()))
          {
            AttributeValue value = attribute.getValue();
            if ((value instanceof StringAttribute))
            {
              String scope = ((StringAttribute)value).getValue();
              if (scope.equals("Children"))
              {
                resourceScope = 1;
                break;
              }
              if (!scope.equals("Descendants")) {
                break;
              }
              resourceScope = 2;
              
              break;
            }
            logger.error("scope attribute must be a string");
            
            break;
          }
          if (("urn:oasis:names:tc:xacml:1.0:resource:resource-id".equals(attribute.getId().toString())) && 
            (resourceId == null)) {
            resourceId = attribute.getValue();
          }
        }
        if (attribute.getId().toString().equals("urn:oasis:names:tc:xacml:3.0:profile:multiple:content-selector"))
        {
          if (multipleContentSelectors == null) {
            multipleContentSelectors = new HashSet();
          }
          multipleContentSelectors.add(attributes);
        }
      }
      if (mapAttributes.containsKey(category))
      {
        Set<Attributes> set = (Set)mapAttributes.get(category);
        set.add(attributes);
        multipleAttributes = true;
      }
      else
      {
        Set<Attributes> set = new HashSet();
        set.add(attributes);
        mapAttributes.put(category, set);
      }
    }
  }
  
  public MultipleCtxResult getMultipleEvaluationCtx()
  {
    Set<EvaluationCtx> evaluationCtxSet = new HashSet();
    MultiRequests multiRequests = requestCtx.getMultiRequests();
    if (multiRequests != null)
    {
      MultipleCtxResult result = processMultiRequestElement(this);
      if (result.isIndeterminate()) {
        return result;
      }
      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }
    if (evaluationCtxSet.size() > 0)
    {
      Set<EvaluationCtx> newSet = new HashSet(evaluationCtxSet);
      for (EvaluationCtx evaluationCtx : newSet) {
        if (multipleAttributes)
        {
          evaluationCtxSet.remove(evaluationCtx);
          MultipleCtxResult result = processMultipleAttributes((XACML3EvaluationCtx)evaluationCtx);
          if (result.isIndeterminate()) {
            return result;
          }
          evaluationCtxSet.addAll(result.getEvaluationCtxSet());
        }
      }
    }
    else if (multipleAttributes)
    {
      MultipleCtxResult result = processMultipleAttributes(this);
      if (result.isIndeterminate()) {
        return result;
      }
      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }
    if (evaluationCtxSet.size() > 0)
    {
      Set<EvaluationCtx> newSet = new HashSet(evaluationCtxSet);
      for (EvaluationCtx evaluationCtx : newSet) {
        if (resourceScope != 0)
        {
          evaluationCtxSet.remove(evaluationCtx);
          MultipleCtxResult result = processHierarchicalAttributes((XACML3EvaluationCtx)evaluationCtx);
          if (result.isIndeterminate()) {
            return result;
          }
          evaluationCtxSet.addAll(result.getEvaluationCtxSet());
        }
        else if (((XACML3EvaluationCtx)evaluationCtx).getMultipleContentSelectors() != null)
        {
          MultipleCtxResult result = processMultipleContentSelectors((XACML3EvaluationCtx)evaluationCtx);
          if (result.isIndeterminate()) {
            return result;
          }
          evaluationCtxSet.addAll(result.getEvaluationCtxSet());
        }
      }
    }
    else if (resourceScope != 0)
    {
      MultipleCtxResult result = processHierarchicalAttributes(this);
      if (result.isIndeterminate()) {
        return result;
      }
      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }
    else if (multipleContentSelectors != null)
    {
      MultipleCtxResult result = processMultipleContentSelectors(this);
      if (result.isIndeterminate()) {
        return result;
      }
      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }
    if (evaluationCtxSet.size() > 0) {
      return new MultipleCtxResult(evaluationCtxSet);
    }
    evaluationCtxSet.add(this);
    return new MultipleCtxResult(evaluationCtxSet);
  }
  
  private MultipleCtxResult processMultiRequestElement(XACML3EvaluationCtx evaluationCtx)
  {
    Set<EvaluationCtx> children = new HashSet();
    MultiRequests multiRequests = requestCtx.getMultiRequests();
    if (multiRequests != null)
    {
      Set<RequestReference> requestReferences = multiRequests.getRequestReferences();
      Iterator localIterator2;
      label226:
      for (Iterator localIterator1 = requestReferences.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
      {
        RequestReference reference = (RequestReference)localIterator1.next();
        Set<AttributesReference> attributesReferences = reference.getReferences();
        if ((attributesReferences == null) || (attributesReferences.size() <= 0)) {
          break label226;
        }
        Set<Attributes> attributes = new HashSet();
        localIterator2 = attributesReferences.iterator(); continue;AttributesReference attributesReference = (AttributesReference)localIterator2.next();
        String referenceId = attributesReference.getId();
        if (referenceId != null) {
          for (Attributes attribute : evaluationCtx.getAttributesSet()) {
            if ((attribute.getId() != null) && (attribute.getId().equals(referenceId))) {
              attributes.add(attribute);
            }
          }
        }
        RequestCtx ctx = new RequestCtx(attributes, null);
        children.add(new XACML3EvaluationCtx(ctx, pdpConfig));
      }
    }
    return new MultipleCtxResult(children);
  }
  
  private MultipleCtxResult processMultipleAttributes(XACML3EvaluationCtx evaluationCtx)
  {
    Set<EvaluationCtx> children = new HashSet();
    Set<RequestCtx> newRequestCtxSet = new HashSet();
    
    Map<String, Set<Attributes>> mapAttributes = evaluationCtx.getMapAttributes();
    for (Map.Entry<String, Set<Attributes>> mapAttributesEntry : mapAttributes.entrySet()) {
      if (((Set)mapAttributesEntry.getValue()).size() > 1) {
        for (Attributes attributesElement : (Set)mapAttributesEntry.getValue())
        {
          Set<Attributes> newSet = new HashSet(evaluationCtx.getAttributesSet());
          newSet.removeAll((Collection)mapAttributesEntry.getValue());
          newSet.add(attributesElement);
          RequestCtx newRequestCtx = new RequestCtx(newSet, null);
          newRequestCtxSet.add(newRequestCtx);
        }
      }
    }
    for (RequestCtx ctx : newRequestCtxSet) {
      children.add(new XACML3EvaluationCtx(ctx, pdpConfig));
    }
    return new MultipleCtxResult(children);
  }
  
  private MultipleCtxResult processHierarchicalAttributes(XACML3EvaluationCtx evaluationCtx)
  {
    ResourceFinderResult resourceResult = null;
    Set<EvaluationCtx> children = new HashSet();
    if (resourceId != null)
    {
      if (resourceScope == 1) {
        resourceResult = 
          pdpConfig.getResourceFinder().findChildResources(resourceId, evaluationCtx);
      } else if (resourceScope == 2) {
        resourceResult = 
          pdpConfig.getResourceFinder().findDescendantResources(resourceId, evaluationCtx);
      } else {
        logger.error("Unknown scope type: ");
      }
    }
    else {
      logger.error("ResourceId Attribute is NULL: ");
    }
    if ((resourceResult == null) || (resourceResult.isEmpty())) {
      logger.error("Resource Finder result is NULL: ");
    } else {
      for (AttributeValue resource : resourceResult.getResources())
      {
        evaluationCtx.setResourceId(resource, attributesSet);
        children.add(new XACML3EvaluationCtx(new RequestCtx(attributesSet, null), pdpConfig));
      }
    }
    return new MultipleCtxResult(children);
  }
  
  private MultipleCtxResult processMultipleContentSelectors(XACML3EvaluationCtx evaluationCtx)
  {
    Set<EvaluationCtx> children = new HashSet();
    Set<Attributes> newAttributesSet = new HashSet();
    for (Attributes attributes : evaluationCtx.getMultipleContentSelectors())
    {
      Set<Attribute> newAttributes = null;
      Attribute oldAttribute = null;
      Object content = attributes.getContent();
      if ((content != null) && ((content instanceof Node)))
      {
        Node root = (Node)content;
        for (Attribute attribute : attributes.getAttributes())
        {
          oldAttribute = attribute;
          if (attribute.getId().toString().equals("urn:oasis:names:tc:xacml:3.0:profile:multiple:content-selector"))
          {
            List<AttributeValue> values = attribute.getValues();
            for (AttributeValue value : values) {
              if ((value instanceof XPathAttribute))
              {
                XPathAttribute xPathAttribute = (XPathAttribute)value;
                if (xPathAttribute.getXPathCategory().equals(attributes.getCategory().toString()))
                {
                  Set<String> xPaths = getChildXPaths(root, xPathAttribute.getValue());
                  for (String xPath : xPaths) {
                    try
                    {
                      AttributeValue newValue = Balana.getInstance().getAttributeFactory()
                        .createValue(value.getType(), xPath, 
                        new String[] { xPathAttribute.getXPathCategory() });
                      Attribute newAttribute = 
                        new Attribute(new URI("urn:oasis:names:tc:xacml:3.0:content-selector"), 
                        attribute.getIssuer(), attribute.getIssueInstant(), 
                        newValue, attribute.isIncludeInResult(), 
                        3);
                      if (newAttributes == null) {
                        newAttributes = new HashSet();
                      }
                      newAttributes.add(newAttribute);
                    }
                    catch (Exception e)
                    {
                      e.printStackTrace();
                    }
                  }
                }
              }
            }
          }
        }
        if (newAttributes != null)
        {
          attributes.getAttributes().remove(oldAttribute);
          for (Attribute attribute : newAttributes)
          {
            Set<Attribute> set = new HashSet(attributes.getAttributes());
            set.add(attribute);
            Attributes attr = new Attributes(attributes.getCategory(), set);
            newAttributesSet.add(attr);
          }
          evaluationCtx.getAttributesSet().remove(attributes);
        }
      }
    }
    for (Attributes attributes : newAttributesSet)
    {
      Set<Attributes> set = new HashSet(evaluationCtx.getAttributesSet());
      set.add(attributes);
      RequestCtx requestCtx = new RequestCtx(set, null);
      children.add(new XACML3EvaluationCtx(requestCtx, pdpConfig));
    }
    return new MultipleCtxResult(children);
  }
  
  public void setResourceId(AttributeValue resourceId, Set<Attributes> attributesSet)
  {
    for (Attributes attributes : attributesSet) {
      if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(attributes.getCategory().toString()))
      {
        Set<Attribute> attributeSet = attributes.getAttributes();
        Set<Attribute> newSet = new HashSet(attributeSet);
        Attribute resourceIdAttribute = null;
        for (Attribute attribute : newSet) {
          if ("urn:oasis:names:tc:xacml:1.0:resource:resource-id".equals(attribute.getId().toString()))
          {
            resourceIdAttribute = attribute;
            attributeSet.remove(attribute);
          }
          else if ("urn:oasis:names:tc:xacml:2.0:resource:scope".equals(attribute.getId().toString()))
          {
            attributeSet.remove(attribute);
          }
        }
        if (resourceIdAttribute == null) {
          break;
        }
        attributeSet.add(new Attribute(resourceIdAttribute.getId(), resourceIdAttribute.getIssuer(), 
          resourceIdAttribute.getIssueInstant(), resourceId, resourceIdAttribute.isIncludeInResult(), 
          3));
        
        break;
      }
    }
  }
  
  private Set<String> getChildXPaths(Node root, String xPath)
  {
    Set<String> xPaths = new HashSet();
    
    String namespace = root.getNamespaceURI();
    
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    if (namespace != null)
    {
      NamedNodeMap namedNodeMap = root.getAttributes();
      String prefix = "ns";
      String nodeName = null;
      for (int i = 0; i < namedNodeMap.getLength(); i++)
      {
        Node n = namedNodeMap.item(i);
        if (n.getNodeValue().equals(namespace))
        {
          nodeName = n.getNodeName();
          break;
        }
      }
      if (nodeName != null)
      {
        int pos = nodeName.indexOf(':');
        if (pos != -1) {
          prefix = nodeName.substring(pos + 1);
        } else {
          xPath = Utils.prepareXPathForDefaultNs(xPath);
        }
      }
      else
      {
        xPath = Utils.prepareXPathForDefaultNs(xPath);
      }
      NamespaceContext namespaceContext = new DefaultNamespaceContext(prefix, namespace);
      
      xpath.setNamespaceContext(namespaceContext);
    }
    try
    {
      XPathExpression expression = xpath.compile(xPath);
      NodeList matches = (NodeList)expression.evaluate(root, XPathConstants.NODESET);
      if ((matches != null) && (matches.getLength() > 0)) {
        for (int i = 0; i < matches.getLength(); i++)
        {
          String text = null;
          Node node = matches.item(i);
          short nodeType = node.getNodeType();
          if ((nodeType == 4) || (nodeType == 8) || 
            (nodeType == 3) || (nodeType == 2)) {
            text = node.getNodeValue();
          } else {
            text = "/" + node.getNodeName();
          }
          xPaths.add(text);
        }
      }
    }
    catch (Exception localException) {}
    return xPaths;
  }
  
  public boolean isMultipleAttributes()
  {
    return multipleAttributes;
  }
  
  public AbstractRequestCtx getRequestCtx()
  {
    return requestCtx;
  }
  
  public Set<PolicyReference> getPolicyReferences()
  {
    return policyReferences;
  }
  
  public void setPolicyReferences(Set<PolicyReference> policyReferences)
  {
    this.policyReferences = policyReferences;
  }
  
  public Set<Attributes> getMultipleContentSelectors()
  {
    return multipleContentSelectors;
  }
  
  public Map<String, Set<Attributes>> getMapAttributes()
  {
    return mapAttributes;
  }
  
  public Set<Attributes> getAttributesSet()
  {
    return attributesSet;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
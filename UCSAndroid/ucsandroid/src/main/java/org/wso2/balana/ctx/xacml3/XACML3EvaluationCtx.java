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

public class XACML3EvaluationCtx extends BasicEvaluationCtx {
  private Set<Attributes> attributesSet;
  private Set<Attributes> multipleContentSelectors;
  private boolean multipleAttributes;
  private Set<PolicyReference> policyReferences;
  private Map<String, Set<Attributes>> mapAttributes;
  private RequestCtx requestCtx;
  private int resourceScope;
  private AttributeValue resourceId;
  private static Log logger = LogFactory.getLog(XACML3EvaluationCtx.class);

  public XACML3EvaluationCtx(RequestCtx requestCtx, PDPConfig pdpConfig) {
    this.currentDate = null;
    this.currentTime = null;
    this.currentDateTime = null;
    this.mapAttributes = new HashMap();
    this.attributesSet = requestCtx.getAttributesSet();
    this.pdpConfig = pdpConfig;
    this.requestCtx = requestCtx;
    this.setupAttributes(this.attributesSet, this.mapAttributes);
  }

  public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category) {
    List<AttributeValue> attributeValues = new ArrayList();
    Set<Attributes> attributesSet = (Set)this.mapAttributes.get(category.toString());
    if (attributesSet != null && attributesSet.size() > 0) {
      Set<Attribute> attributeSet = ((Attributes)attributesSet.iterator().next()).getAttributes();
      Iterator var9 = attributeSet.iterator();

      while(true) {
        Attribute attribute;
        do {
          do {
            do {
              if (!var9.hasNext()) {
                if (attributeValues.size() < 1) {
                  return this.callHelper(type, id, issuer, category);
                }

                return new EvaluationResult(new BagAttribute(type, attributeValues));
              }

              attribute = (Attribute)var9.next();
            } while(!attribute.getId().equals(id));
          } while(!attribute.getType().equals(type));
        } while(issuer != null && !issuer.equals(attribute.getIssuer()));

        if (attribute.getValue() != null) {
          attributeValues.add(attribute.getValue());
        }
      }
    } else {
      return new EvaluationResult(new BagAttribute(type, attributeValues));
    }
  }

  public EvaluationResult getAttribute(String path, URI type, URI category, URI contextSelector, String xpathVersion) {
    if (this.pdpConfig.getAttributeFinder() == null) {
      logger.warn("Context tried to invoke AttributeFinder but was not configured with one");
      return new EvaluationResult(BagAttribute.createEmptyBag(type));
    } else {
      Set<Attributes> attributesSet = null;
      if (category != null) {
        attributesSet = (Set)this.mapAttributes.get(category.toString());
      }

      if (attributesSet != null && attributesSet.size() > 0) {
        Attributes attributes = (Attributes)attributesSet.iterator().next();
        Object content = attributes.getContent();
        if (content instanceof Node) {
          Node root = (Node)content;
          if (contextSelector == null || contextSelector.toString().trim().length() <= 0) {
            return this.pdpConfig.getAttributeFinder().findAttribute(path, (String)null, type, root, this, xpathVersion);
          }

          Iterator var11 = attributes.getAttributes().iterator();

          while(true) {
            Attribute attribute;
            do {
              if (!var11.hasNext()) {
                return new EvaluationResult(BagAttribute.createEmptyBag(type));
              }

              attribute = (Attribute)var11.next();
            } while(!attribute.getId().equals(contextSelector));

            List<AttributeValue> values = attribute.getValues();
            Iterator var14 = values.iterator();

            while(var14.hasNext()) {
              AttributeValue value = (AttributeValue)var14.next();
              if (value instanceof XPathAttribute) {
                XPathAttribute xPathAttribute = (XPathAttribute)value;
                if (xPathAttribute.getXPathCategory().equals(category.toString())) {
                  return this.pdpConfig.getAttributeFinder().findAttribute(path, xPathAttribute.getValue(), type, root, this, xpathVersion);
                }
              }
            }
          }
        }
      }

      return new EvaluationResult(BagAttribute.createEmptyBag(type));
    }
  }

  public int getXacmlVersion() {
    return this.requestCtx.getXacmlVersion();
  }

  private void setupAttributes(Set<Attributes> attributeSet, Map<String, Set<Attributes>> mapAttributes) {
    Iterator var4 = attributeSet.iterator();

    while(var4.hasNext()) {
      Attributes attributes = (Attributes)var4.next();
      String category = attributes.getCategory().toString();
      Iterator var7 = attributes.getAttributes().iterator();

      while(var7.hasNext()) {
        Attribute attribute = (Attribute)var7.next();
        if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(category)) {
          if ("urn:oasis:names:tc:xacml:2.0:resource:scope".equals(attribute.getId().toString())) {
            AttributeValue value = attribute.getValue();
            if (value instanceof StringAttribute) {
              String scope = ((StringAttribute)value).getValue();
              if (scope.equals("Children")) {
                this.resourceScope = 1;
              } else if (scope.equals("Descendants")) {
                this.resourceScope = 2;
              }
            } else {
              logger.error("scope attribute must be a string");
            }
            break;
          }

          if ("urn:oasis:names:tc:xacml:1.0:resource:resource-id".equals(attribute.getId().toString()) && this.resourceId == null) {
            this.resourceId = attribute.getValue();
          }
        }

        if (attribute.getId().toString().equals("urn:oasis:names:tc:xacml:3.0:profile:multiple:content-selector")) {
          if (this.multipleContentSelectors == null) {
            this.multipleContentSelectors = new HashSet();
          }

          this.multipleContentSelectors.add(attributes);
        }
      }

      if (mapAttributes.containsKey(category)) {
        Set<Attributes> set = (Set)mapAttributes.get(category);
        set.add(attributes);
        this.multipleAttributes = true;
      } else {
        Set<Attributes> set = new HashSet();
        set.add(attributes);
        mapAttributes.put(category, set);
      }
    }

  }

  public MultipleCtxResult getMultipleEvaluationCtx() {
    Set<EvaluationCtx> evaluationCtxSet = new HashSet();
    MultiRequests multiRequests = this.requestCtx.getMultiRequests();
    MultipleCtxResult result;
    if (multiRequests != null) {
      result = this.processMultiRequestElement(this);
      if (result.isIndeterminate()) {
        return result;
      }

      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }

    EvaluationCtx evaluationCtx;
    Iterator var5;
    HashSet newSet;
    if (evaluationCtxSet.size() > 0) {
      newSet = new HashSet(evaluationCtxSet);
      var5 = newSet.iterator();

      while(var5.hasNext()) {
        evaluationCtx = (EvaluationCtx)var5.next();
        if (this.multipleAttributes) {
          evaluationCtxSet.remove(evaluationCtx);
          result = this.processMultipleAttributes((XACML3EvaluationCtx)evaluationCtx);
          if (result.isIndeterminate()) {
            return result;
          }

          evaluationCtxSet.addAll(result.getEvaluationCtxSet());
        }
      }
    } else if (this.multipleAttributes) {
      result = this.processMultipleAttributes(this);
      if (result.isIndeterminate()) {
        return result;
      }

      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }

    if (evaluationCtxSet.size() > 0) {
      newSet = new HashSet(evaluationCtxSet);
      var5 = newSet.iterator();

      while(var5.hasNext()) {
        evaluationCtx = (EvaluationCtx)var5.next();
        if (this.resourceScope != 0) {
          evaluationCtxSet.remove(evaluationCtx);
          result = this.processHierarchicalAttributes((XACML3EvaluationCtx)evaluationCtx);
          if (result.isIndeterminate()) {
            return result;
          }

          evaluationCtxSet.addAll(result.getEvaluationCtxSet());
        } else if (((XACML3EvaluationCtx)evaluationCtx).getMultipleContentSelectors() != null) {
          result = this.processMultipleContentSelectors((XACML3EvaluationCtx)evaluationCtx);
          if (result.isIndeterminate()) {
            return result;
          }

          evaluationCtxSet.addAll(result.getEvaluationCtxSet());
        }
      }
    } else if (this.resourceScope != 0) {
      result = this.processHierarchicalAttributes(this);
      if (result.isIndeterminate()) {
        return result;
      }

      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    } else if (this.multipleContentSelectors != null) {
      result = this.processMultipleContentSelectors(this);
      if (result.isIndeterminate()) {
        return result;
      }

      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }

    if (evaluationCtxSet.size() > 0) {
      return new MultipleCtxResult(evaluationCtxSet);
    } else {
      evaluationCtxSet.add(this);
      return new MultipleCtxResult(evaluationCtxSet);
    }
  }

  private MultipleCtxResult processMultiRequestElement(XACML3EvaluationCtx evaluationCtx) {
    Set<EvaluationCtx> children = new HashSet();
    MultiRequests multiRequests = this.requestCtx.getMultiRequests();
    if (multiRequests != null) {
      Set<RequestReference> requestReferences = multiRequests.getRequestReferences();
      Iterator var6 = requestReferences.iterator();

      while(true) {
        Set attributesReferences;
        do {
          do {
            if (!var6.hasNext()) {
              return new MultipleCtxResult(children);
            }

            RequestReference reference = (RequestReference)var6.next();
            attributesReferences = reference.getReferences();
          } while(attributesReferences == null);
        } while(attributesReferences.size() <= 0);

        Set<Attributes> attributes = new HashSet();
        Iterator var10 = attributesReferences.iterator();

        while(var10.hasNext()) {
          AttributesReference attributesReference = (AttributesReference)var10.next();
          String referenceId = attributesReference.getId();
          if (referenceId != null) {
            Iterator var13 = evaluationCtx.getAttributesSet().iterator();

            while(var13.hasNext()) {
              Attributes attribute = (Attributes)var13.next();
              if (attribute.getId() != null && attribute.getId().equals(referenceId)) {
                attributes.add(attribute);
              }
            }
          }

          RequestCtx ctx = new RequestCtx(attributes, (Node)null);
          children.add(new XACML3EvaluationCtx(ctx, this.pdpConfig));
        }
      }
    } else {
      return new MultipleCtxResult(children);
    }
  }

  private MultipleCtxResult processMultipleAttributes(XACML3EvaluationCtx evaluationCtx) {
    Set<EvaluationCtx> children = new HashSet();
    Set<RequestCtx> newRequestCtxSet = new HashSet();
    Map<String, Set<Attributes>> mapAttributes = evaluationCtx.getMapAttributes();
    Iterator var6 = mapAttributes.entrySet().iterator();

    while(true) {
      Entry mapAttributesEntry;
      do {
        if (!var6.hasNext()) {
          var6 = newRequestCtxSet.iterator();

          while(var6.hasNext()) {
            RequestCtx ctx = (RequestCtx)var6.next();
            children.add(new XACML3EvaluationCtx(ctx, this.pdpConfig));
          }

          return new MultipleCtxResult(children);
        }

        mapAttributesEntry = (Entry)var6.next();
      } while(((Set)mapAttributesEntry.getValue()).size() <= 1);

      Iterator var8 = ((Set)mapAttributesEntry.getValue()).iterator();

      while(var8.hasNext()) {
        Attributes attributesElement = (Attributes)var8.next();
        Set<Attributes> newSet = new HashSet(evaluationCtx.getAttributesSet());
        newSet.removeAll((Collection)mapAttributesEntry.getValue());
        newSet.add(attributesElement);
        RequestCtx newRequestCtx = new RequestCtx(newSet, (Node)null);
        newRequestCtxSet.add(newRequestCtx);
      }
    }
  }

  private MultipleCtxResult processHierarchicalAttributes(XACML3EvaluationCtx evaluationCtx) {
    ResourceFinderResult resourceResult = null;
    Set<EvaluationCtx> children = new HashSet();
    if (this.resourceId != null) {
      if (this.resourceScope == 1) {
        resourceResult = this.pdpConfig.getResourceFinder().findChildResources(this.resourceId, evaluationCtx);
      } else if (this.resourceScope == 2) {
        resourceResult = this.pdpConfig.getResourceFinder().findDescendantResources(this.resourceId, evaluationCtx);
      } else {
        logger.error("Unknown scope type: ");
      }
    } else {
      logger.error("ResourceId Attribute is NULL: ");
    }

    if (resourceResult != null && !resourceResult.isEmpty()) {
      Iterator var5 = resourceResult.getResources().iterator();

      while(var5.hasNext()) {
        AttributeValue resource = (AttributeValue)var5.next();
        evaluationCtx.setResourceId(resource, this.attributesSet);
        children.add(new XACML3EvaluationCtx(new RequestCtx(this.attributesSet, (Node)null), this.pdpConfig));
      }
    } else {
      logger.error("Resource Finder result is NULL: ");
    }

    return new MultipleCtxResult(children);
  }

  private MultipleCtxResult processMultipleContentSelectors(XACML3EvaluationCtx evaluationCtx) {
    Set<EvaluationCtx> children = new HashSet();
    Set<Attributes> newAttributesSet = new HashSet();
    Iterator var5 = evaluationCtx.getMultipleContentSelectors().iterator();

    while(true) {
      Attributes attributes;
      HashSet newAttributes;
      Attribute oldAttribute;
      Attribute attribute;
      Iterator var11;
      label84:
      do {
        Object content;
        do {
          do {
            if (!var5.hasNext()) {
              var5 = newAttributesSet.iterator();

              while(var5.hasNext()) {
                attributes = (Attributes)var5.next();
                newAttributes = new HashSet(evaluationCtx.getAttributesSet());
                newAttributes.add(attributes);
                RequestCtx requestCtx = new RequestCtx(newAttributes, (Node)null);
                children.add(new XACML3EvaluationCtx(requestCtx, this.pdpConfig));
              }

              return new MultipleCtxResult(children);
            }

            attributes = (Attributes)var5.next();
            newAttributes = null;
            oldAttribute = null;
            content = attributes.getContent();
          } while(content == null);
        } while(!(content instanceof Node));

        Node root = (Node)content;
        var11 = attributes.getAttributes().iterator();

        label79:
        while(true) {
          do {
            if (!var11.hasNext()) {
              continue label84;
            }

            attribute = (Attribute)var11.next();
            oldAttribute = attribute;
          } while(!attribute.getId().toString().equals("urn:oasis:names:tc:xacml:3.0:profile:multiple:content-selector"));

          List<AttributeValue> values = attribute.getValues();
          Iterator var14 = values.iterator();

          while(true) {
            AttributeValue value;
            XPathAttribute xPathAttribute;
            do {
              do {
                if (!var14.hasNext()) {
                  continue label79;
                }

                value = (AttributeValue)var14.next();
              } while(!(value instanceof XPathAttribute));

              xPathAttribute = (XPathAttribute)value;
            } while(!xPathAttribute.getXPathCategory().equals(attributes.getCategory().toString()));

            Set<String> xPaths = this.getChildXPaths(root, xPathAttribute.getValue());
            Iterator var18 = xPaths.iterator();

            while(var18.hasNext()) {
              String xPath = (String)var18.next();

              try {
                AttributeValue newValue = Balana.getInstance().getAttributeFactory().createValue(value.getType(), xPath, new String[]{xPathAttribute.getXPathCategory()});
                Attribute newAttribute = new Attribute(new URI("urn:oasis:names:tc:xacml:3.0:content-selector"), attribute.getIssuer(), attribute.getIssueInstant(), newValue, attribute.isIncludeInResult(), 3);
                if (newAttributes == null) {
                  newAttributes = new HashSet();
                }

                newAttributes.add(newAttribute);
              } catch (Exception var21) {
                var21.printStackTrace();
              }
            }
          }
        }
      } while(newAttributes == null);

      attributes.getAttributes().remove(oldAttribute);
      var11 = newAttributes.iterator();

      while(var11.hasNext()) {
        attribute = (Attribute)var11.next();
        Set<Attribute> set = new HashSet(attributes.getAttributes());
        set.add(attribute);
        Attributes attr = new Attributes(attributes.getCategory(), set);
        newAttributesSet.add(attr);
      }

      evaluationCtx.getAttributesSet().remove(attributes);
    }
  }

  public void setResourceId(AttributeValue resourceId, Set<Attributes> attributesSet) {
    Iterator var4 = attributesSet.iterator();

    while(var4.hasNext()) {
      Attributes attributes = (Attributes)var4.next();
      if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(attributes.getCategory().toString())) {
        Set<Attribute> attributeSet = attributes.getAttributes();
        Set<Attribute> newSet = new HashSet(attributeSet);
        Attribute resourceIdAttribute = null;
        Iterator var9 = newSet.iterator();

        while(var9.hasNext()) {
          Attribute attribute = (Attribute)var9.next();
          if ("urn:oasis:names:tc:xacml:1.0:resource:resource-id".equals(attribute.getId().toString())) {
            resourceIdAttribute = attribute;
            attributeSet.remove(attribute);
          } else if ("urn:oasis:names:tc:xacml:2.0:resource:scope".equals(attribute.getId().toString())) {
            attributeSet.remove(attribute);
          }
        }

        if (resourceIdAttribute != null) {
          attributeSet.add(new Attribute(resourceIdAttribute.getId(), resourceIdAttribute.getIssuer(), resourceIdAttribute.getIssueInstant(), resourceId, resourceIdAttribute.isIncludeInResult(), 3));
        }
        break;
      }
    }

  }

  private Set<String> getChildXPaths(Node root, String xPath) {
    Set<String> xPaths = new HashSet();
    String namespace = root.getNamespaceURI();
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    Node node;
    if (namespace != null) {
      NamedNodeMap namedNodeMap = root.getAttributes();
      String prefix = "ns";
      String nodeName = null;

      int pos;
      for(pos = 0; pos < namedNodeMap.getLength(); ++pos) {
        node = namedNodeMap.item(pos);
        if (node.getNodeValue().equals(namespace)) {
          nodeName = node.getNodeName();
          break;
        }
      }

      if (nodeName != null) {
        pos = nodeName.indexOf(58);
        if (pos != -1) {
          prefix = nodeName.substring(pos + 1);
        } else {
          xPath = Utils.prepareXPathForDefaultNs(xPath);
        }
      } else {
        xPath = Utils.prepareXPathForDefaultNs(xPath);
      }

      NamespaceContext namespaceContext = new DefaultNamespaceContext(prefix, namespace);
      xpath.setNamespaceContext(namespaceContext);
    }

    try {
      XPathExpression expression = xpath.compile(xPath);
      NodeList matches = (NodeList)expression.evaluate(root, XPathConstants.NODESET);
      if (matches != null && matches.getLength() > 0) {
        for(int i = 0; i < matches.getLength(); ++i) {
          String text = null;
          node = matches.item(i);
          short nodeType = node.getNodeType();
          if (nodeType != 4 && nodeType != 8 && nodeType != 3 && nodeType != 2) {
            text = "/" + node.getNodeName();
          } else {
            text = node.getNodeValue();
          }

          xPaths.add(text);
        }
      }
    } catch (Exception var14) {
    }

    return xPaths;
  }

  public boolean isMultipleAttributes() {
    return this.multipleAttributes;
  }

  public AbstractRequestCtx getRequestCtx() {
    return this.requestCtx;
  }

  public Set<PolicyReference> getPolicyReferences() {
    return this.policyReferences;
  }

  public void setPolicyReferences(Set<PolicyReference> policyReferences) {
    this.policyReferences = policyReferences;
  }

  public Set<Attributes> getMultipleContentSelectors() {
    return this.multipleContentSelectors;
  }

  public Map<String, Set<Attributes>> getMapAttributes() {
    return this.mapAttributes;
  }

  public Set<Attributes> getAttributesSet() {
    return this.attributesSet;
  }
}


/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
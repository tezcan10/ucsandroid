package org.wso2.balana.ctx.xacml2;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.BasicEvaluationCtx;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.ResourceFinder;
import org.wso2.balana.finder.ResourceFinderResult;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.balana.xacml3.MultipleCtxResult;

public class XACML2EvaluationCtx
  extends BasicEvaluationCtx
{
  private Set<Attributes> attributesSet;
  private int xacmlVersion;
  private Node requestRoot;
  private HashMap subjectMap;
  private HashMap resourceMap;
  private HashMap actionMap;
  private HashMap environmentMap;
  private AttributeValue resourceId;
  private int scope;
  private DateAttribute currentDate;
  private TimeAttribute currentTime;
  private DateTimeAttribute currentDateTime;
  private boolean useCachedEnvValues;
  private MultipleCtxResult multipleCtxResult;
  private RequestCtx requestCtx;
  private static Log logger = LogFactory.getLog(XACML2EvaluationCtx.class);
  
  public XACML2EvaluationCtx() {}
  
  public XACML2EvaluationCtx(RequestCtx requestCtx, PDPConfig pdpConfig)
    throws ParsingException
  {
    this.pdpConfig = pdpConfig;
    
    this.requestCtx = requestCtx;
    
    xacmlVersion = requestCtx.getXacmlVersion();
    
    requestRoot = requestCtx.getDocumentRoot();
    
    attributesSet = requestCtx.getAttributesSet();
    
    useCachedEnvValues = false;
    currentDate = null;
    currentTime = null;
    currentDateTime = null;
    
    subjectMap = new HashMap();
    setupSubjects(requestCtx.getSubjects());
    
    resourceMap = new HashMap();
    setupResource(requestCtx.getResource());
    
    actionMap = new HashMap();
    mapAttributes(requestCtx.getAction(), actionMap);
    
    environmentMap = new HashMap();
    mapAttributes(requestCtx.getEnvironmentAttributes(), environmentMap);
  }
  
  private void setupSubjects(Set subjects)
  {
    Iterator it = subjects.iterator();
    Iterator attrIterator;
    for (; it.hasNext(); attrIterator.hasNext())
    {
      Subject subject = (Subject)it.next();
      
      URI category = subject.getCategory();
      Map categoryMap = null;
      if (subjectMap.containsKey(category))
      {
        categoryMap = (Map)subjectMap.get(category);
      }
      else
      {
        categoryMap = new HashMap();
        subjectMap.put(category, categoryMap);
      }
      attrIterator = subject.getAttributes().iterator();
      
      continue;
      Attribute attr = (Attribute)attrIterator.next();
      String id = attr.getId().toString();
      if (categoryMap.containsKey(id))
      {
        Set existingIds = (Set)categoryMap.get(id);
        existingIds.add(attr);
      }
      else
      {
        HashSet newIds = new HashSet();
        newIds.add(attr);
        categoryMap.put(id, newIds);
      }
    }
  }
  
  private void setupResource(Set resource)
    throws ParsingException
  {
    mapAttributes(resource, resourceMap);
    if (!resourceMap.containsKey("urn:oasis:names:tc:xacml:1.0:resource:resource-id"))
    {
      logger.error("Resource must contain resource-id attr");
      throw new ParsingException("resource missing resource-id");
    }
    Set set = (Set)resourceMap.get("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
    if (set.size() > 1)
    {
      logger.error("Resource may contain only one resource-id Attribute");
      throw new ParsingException("too many resource-id attrs");
    }
    resourceId = ((Attribute)set.iterator().next()).getValue();
    if (resourceMap.containsKey("urn:oasis:names:tc:xacml:1.0:resource:scope"))
    {
      Set set = (Set)resourceMap.get("urn:oasis:names:tc:xacml:1.0:resource:scope");
      if (set.size() > 1)
      {
        logger.error("Resource may contain only one resource-scope Attribute");
        throw new ParsingException("too many resource-scope attrs");
      }
      Attribute attr = (Attribute)set.iterator().next();
      AttributeValue attrValue = attr.getValue();
      if (!attrValue.getType().toString().equals("http://www.w3.org/2001/XMLSchema#string"))
      {
        logger.error("scope attr must be a string");
        throw new ParsingException("scope attr must be a string");
      }
      String value = ((StringAttribute)attrValue).getValue();
      if (value.equals("Immediate"))
      {
        scope = 0;
      }
      else if (value.equals("Children"))
      {
        scope = 1;
      }
      else if (value.equals("Descendants"))
      {
        scope = 2;
      }
      else
      {
        logger.error("Unknown scope type: " + value);
        throw new ParsingException("invalid scope type: " + value);
      }
    }
    else
    {
      scope = 0;
    }
  }
  
  private void mapAttributes(Set input, Map output)
  {
    Iterator it = input.iterator();
    while (it.hasNext())
    {
      Attribute attr = (Attribute)it.next();
      String id = attr.getId().toString();
      if (output.containsKey(id))
      {
        Set set = (Set)output.get(id);
        set.add(attr);
      }
      else
      {
        Set set = new HashSet();
        set.add(attr);
        output.put(id, set);
      }
    }
  }
  
  public int getScope()
  {
    return scope;
  }
  
  public AttributeValue getResourceId()
  {
    return resourceId;
  }
  
  public void setResourceId(AttributeValue resourceId, Set<Attributes> attributesSet)
  {
    this.resourceId = resourceId;
    
    Set attrSet = (Set)resourceMap.get("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
    Attribute attr = (Attribute)attrSet.iterator().next();
    
    attrSet.remove(attr);
    
    attrSet.add(new Attribute(attr.getId(), attr.getIssuer(), attr.getIssueInstant(), 
      resourceId, 2));
  }
  
  public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category)
  {
    if ("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject".equals(category.toString())) {
      return getSubjectAttribute(type, id, category, issuer);
    }
    if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(category.toString())) {
      return getResourceAttribute(type, id, category, issuer);
    }
    if ("urn:oasis:names:tc:xacml:3.0:attribute-category:action".equals(category.toString())) {
      return getActionAttribute(type, id, category, issuer);
    }
    if ("urn:oasis:names:tc:xacml:3.0:attribute-category:environment".equals(category.toString())) {
      return getEnvironmentAttribute(type, id, category, issuer);
    }
    ArrayList<String> code = new ArrayList();
    code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
    Status status = new Status(code);
    return new EvaluationResult(status);
  }
  
  public int getXacmlVersion()
  {
    return xacmlVersion;
  }
  
  public EvaluationResult getSubjectAttribute(URI type, URI id, URI category, String issuer)
  {
    Map map = (Map)subjectMap.get(category);
    if (map == null) {
      return callHelper(type, id, issuer, category);
    }
    return getGenericAttributes(type, id, category, issuer, map);
  }
  
  public EvaluationResult getResourceAttribute(URI type, URI id, URI category, String issuer)
  {
    return getGenericAttributes(type, id, category, issuer, resourceMap);
  }
  
  public EvaluationResult getActionAttribute(URI type, URI id, URI category, String issuer)
  {
    return getGenericAttributes(type, id, category, issuer, actionMap);
  }
  
  public EvaluationResult getEnvironmentAttribute(URI type, URI id, URI category, String issuer)
  {
    return getGenericAttributes(type, id, category, issuer, environmentMap);
  }
  
  private EvaluationResult getGenericAttributes(URI type, URI id, URI category, String issuer, Map map)
  {
    Set attrSet = (Set)map.get(id.toString());
    if (attrSet == null) {
      return callHelper(type, id, issuer, category);
    }
    List attributes = new ArrayList();
    Iterator it = attrSet.iterator();
    while (it.hasNext())
    {
      Attribute attr = (Attribute)it.next();
      if ((attr.getType().equals(type)) && (
        (issuer == null) || ((attr.getIssuer() != null) && 
        (attr.getIssuer().equals(issuer.toString()))))) {
        attributes.add(attr.getValue());
      }
    }
    if (attributes.size() == 0)
    {
      if (logger.isDebugEnabled()) {
        logger.debug("Attribute not in request: " + id.toString() + 
          " ... querying AttributeFinder");
      }
      return callHelper(type, id, issuer, category);
    }
    return new EvaluationResult(new BagAttribute(type, attributes));
  }
  
  public PDPConfig getPdpConfig()
  {
    return pdpConfig;
  }
  
  public AbstractRequestCtx getRequestCtx()
  {
    return requestCtx;
  }
  
  public MultipleCtxResult getMultipleEvaluationCtx()
  {
    Set<EvaluationCtx> evaluationCtxSet = new HashSet();
    if (scope != 0)
    {
      MultipleCtxResult result = processHierarchicalAttributes(this);
      if (result.isIndeterminate()) {
        return result;
      }
      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }
    if (evaluationCtxSet.size() > 0) {
      return new MultipleCtxResult(evaluationCtxSet, null, false);
    }
    evaluationCtxSet.add(this);
    return new MultipleCtxResult(evaluationCtxSet, null, false);
  }
  
  public int getResourceScope()
  {
    return scope;
  }
  
  private MultipleCtxResult processHierarchicalAttributes(XACML2EvaluationCtx evaluationCtx)
  {
    ResourceFinderResult resourceResult = null;
    Set<EvaluationCtx> children = new HashSet();
    AttributeValue resourceId = evaluationCtx.getResourceId();
    int resourceScope = evaluationCtx.getResourceScope();
    if (resourceId != null)
    {
      if (resourceScope == 1) {
        resourceResult = 
          evaluationCtx.getPdpConfig().getResourceFinder().findChildResources(resourceId, evaluationCtx);
      } else if (resourceScope == 2) {
        resourceResult = 
          evaluationCtx.getPdpConfig().getResourceFinder().findDescendantResources(resourceId, evaluationCtx);
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
        children.add(evaluationCtx);
      }
    }
    return new MultipleCtxResult(children, null, false);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml2.XACML2EvaluationCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
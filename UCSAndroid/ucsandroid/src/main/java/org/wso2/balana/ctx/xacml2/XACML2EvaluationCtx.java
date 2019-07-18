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

public class XACML2EvaluationCtx extends BasicEvaluationCtx {
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

  public XACML2EvaluationCtx() {
  }

  public XACML2EvaluationCtx(RequestCtx requestCtx, PDPConfig pdpConfig) throws ParsingException {
    this.pdpConfig = pdpConfig;
    this.requestCtx = requestCtx;
    this.xacmlVersion = requestCtx.getXacmlVersion();
    this.requestRoot = requestCtx.getDocumentRoot();
    this.attributesSet = requestCtx.getAttributesSet();
    this.useCachedEnvValues = false;
    this.currentDate = null;
    this.currentTime = null;
    this.currentDateTime = null;
    this.subjectMap = new HashMap();
    this.setupSubjects(requestCtx.getSubjects());
    this.resourceMap = new HashMap();
    this.setupResource(requestCtx.getResource());
    this.actionMap = new HashMap();
    this.mapAttributes(requestCtx.getAction(), this.actionMap);
    this.environmentMap = new HashMap();
    this.mapAttributes(requestCtx.getEnvironmentAttributes(), this.environmentMap);
  }

  private void setupSubjects(Set subjects) {
    Iterator it = subjects.iterator();

    while(it.hasNext()) {
      Subject subject = (Subject)it.next();
      URI category = subject.getCategory();
      Map categoryMap = null;
      if (this.subjectMap.containsKey(category)) {
        categoryMap = (Map)this.subjectMap.get(category);
      } else {
        categoryMap = new HashMap();
        this.subjectMap.put(category, categoryMap);
      }

      Iterator attrIterator = subject.getAttributes().iterator();

      while(attrIterator.hasNext()) {
        Attribute attr = (Attribute)attrIterator.next();
        String id = attr.getId().toString();
        if (((Map)categoryMap).containsKey(id)) {
          Set existingIds = (Set)((Map)categoryMap).get(id);
          existingIds.add(attr);
        } else {
          HashSet newIds = new HashSet();
          newIds.add(attr);
          ((Map)categoryMap).put(id, newIds);
        }
      }
    }

  }

  private void setupResource(Set resource) throws ParsingException {
    this.mapAttributes(resource, this.resourceMap);
    if (!this.resourceMap.containsKey("urn:oasis:names:tc:xacml:1.0:resource:resource-id")) {
      logger.error("Resource must contain resource-id attr");
      throw new ParsingException("resource missing resource-id");
    } else {
      Set set = (Set)this.resourceMap.get("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
      if (set.size() > 1) {
        logger.error("Resource may contain only one resource-id Attribute");
        throw new ParsingException("too many resource-id attrs");
      } else {
        this.resourceId = ((Attribute)set.iterator().next()).getValue();
        if (this.resourceMap.containsKey("urn:oasis:names:tc:xacml:1.0:resource:scope")) {
          set = (Set)this.resourceMap.get("urn:oasis:names:tc:xacml:1.0:resource:scope");
          if (set.size() > 1) {
            logger.error("Resource may contain only one resource-scope Attribute");
            throw new ParsingException("too many resource-scope attrs");
          }

          Attribute attr = (Attribute)set.iterator().next();
          AttributeValue attrValue = attr.getValue();
          if (!attrValue.getType().toString().equals("http://www.w3.org/2001/XMLSchema#string")) {
            logger.error("scope attr must be a string");
            throw new ParsingException("scope attr must be a string");
          }

          String value = ((StringAttribute)attrValue).getValue();
          if (value.equals("Immediate")) {
            this.scope = 0;
          } else if (value.equals("Children")) {
            this.scope = 1;
          } else {
            if (!value.equals("Descendants")) {
              logger.error("Unknown scope type: " + value);
              throw new ParsingException("invalid scope type: " + value);
            }

            this.scope = 2;
          }
        } else {
          this.scope = 0;
        }

      }
    }
  }

  private void mapAttributes(Set input, Map output) {
    Iterator it = input.iterator();

    while(it.hasNext()) {
      Attribute attr = (Attribute)it.next();
      String id = attr.getId().toString();
      if (output.containsKey(id)) {
        Set set = (Set)output.get(id);
        set.add(attr);
      } else {
        Set set = new HashSet();
        set.add(attr);
        output.put(id, set);
      }
    }

  }

  public int getScope() {
    return this.scope;
  }

  public AttributeValue getResourceId() {
    return this.resourceId;
  }

  public void setResourceId(AttributeValue resourceId, Set<Attributes> attributesSet) {
    this.resourceId = resourceId;
    Set attrSet = (Set)this.resourceMap.get("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
    Attribute attr = (Attribute)attrSet.iterator().next();
    attrSet.remove(attr);
    attrSet.add(new Attribute(attr.getId(), attr.getIssuer(), attr.getIssueInstant(), resourceId, 2));
  }

  public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category) {
    if ("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject".equals(category.toString())) {
      return this.getSubjectAttribute(type, id, category, issuer);
    } else if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(category.toString())) {
      return this.getResourceAttribute(type, id, category, issuer);
    } else if ("urn:oasis:names:tc:xacml:3.0:attribute-category:action".equals(category.toString())) {
      return this.getActionAttribute(type, id, category, issuer);
    } else if ("urn:oasis:names:tc:xacml:3.0:attribute-category:environment".equals(category.toString())) {
      return this.getEnvironmentAttribute(type, id, category, issuer);
    } else {
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      Status status = new Status(code);
      return new EvaluationResult(status);
    }
  }

  public int getXacmlVersion() {
    return this.xacmlVersion;
  }

  public EvaluationResult getSubjectAttribute(URI type, URI id, URI category, String issuer) {
    Map map = (Map)this.subjectMap.get(category);
    return map == null ? this.callHelper(type, id, issuer, category) : this.getGenericAttributes(type, id, category, issuer, map);
  }

  public EvaluationResult getResourceAttribute(URI type, URI id, URI category, String issuer) {
    return this.getGenericAttributes(type, id, category, issuer, this.resourceMap);
  }

  public EvaluationResult getActionAttribute(URI type, URI id, URI category, String issuer) {
    return this.getGenericAttributes(type, id, category, issuer, this.actionMap);
  }

  public EvaluationResult getEnvironmentAttribute(URI type, URI id, URI category, String issuer) {
    return this.getGenericAttributes(type, id, category, issuer, this.environmentMap);
  }

  private EvaluationResult getGenericAttributes(URI type, URI id, URI category, String issuer, Map map) {
    Set attrSet = (Set)map.get(id.toString());
    if (attrSet == null) {
      return this.callHelper(type, id, issuer, category);
    } else {
      List attributes = new ArrayList();
      Iterator it = attrSet.iterator();

      while(true) {
        Attribute attr;
        do {
          do {
            if (!it.hasNext()) {
              if (attributes.size() == 0) {
                if (logger.isDebugEnabled()) {
                  logger.debug("Attribute not in request: " + id.toString() + " ... querying AttributeFinder");
                }

                return this.callHelper(type, id, issuer, category);
              }

              return new EvaluationResult(new BagAttribute(type, attributes));
            }

            attr = (Attribute)it.next();
          } while(!attr.getType().equals(type));
        } while(issuer != null && (attr.getIssuer() == null || !attr.getIssuer().equals(issuer.toString())));

        attributes.add(attr.getValue());
      }
    }
  }

  public PDPConfig getPdpConfig() {
    return this.pdpConfig;
  }

  public AbstractRequestCtx getRequestCtx() {
    return this.requestCtx;
  }

  public MultipleCtxResult getMultipleEvaluationCtx() {
    Set<EvaluationCtx> evaluationCtxSet = new HashSet();
    if (this.scope != 0) {
      MultipleCtxResult result = this.processHierarchicalAttributes(this);
      if (result.isIndeterminate()) {
        return result;
      }

      evaluationCtxSet.addAll(result.getEvaluationCtxSet());
    }

    if (evaluationCtxSet.size() > 0) {
      return new MultipleCtxResult(evaluationCtxSet, (Status)null, false);
    } else {
      evaluationCtxSet.add(this);
      return new MultipleCtxResult(evaluationCtxSet, (Status)null, false);
    }
  }

  public int getResourceScope() {
    return this.scope;
  }

  private MultipleCtxResult processHierarchicalAttributes(XACML2EvaluationCtx evaluationCtx) {
    ResourceFinderResult resourceResult = null;
    Set<EvaluationCtx> children = new HashSet();
    AttributeValue resourceId = evaluationCtx.getResourceId();
    int resourceScope = evaluationCtx.getResourceScope();
    if (resourceId != null) {
      if (resourceScope == 1) {
        resourceResult = evaluationCtx.getPdpConfig().getResourceFinder().findChildResources(resourceId, evaluationCtx);
      } else if (resourceScope == 2) {
        resourceResult = evaluationCtx.getPdpConfig().getResourceFinder().findDescendantResources(resourceId, evaluationCtx);
      } else {
        logger.error("Unknown scope type: ");
      }
    } else {
      logger.error("ResourceId Attribute is NULL: ");
    }

    if (resourceResult != null && !resourceResult.isEmpty()) {
      Iterator var7 = resourceResult.getResources().iterator();

      while(var7.hasNext()) {
        AttributeValue resource = (AttributeValue)var7.next();
        evaluationCtx.setResourceId(resource, this.attributesSet);
        children.add(evaluationCtx);
      }
    } else {
      logger.error("Resource Finder result is NULL: ");
    }

    return new MultipleCtxResult(children, (Status)null, false);
  }
}


/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml2.XACML2EvaluationCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
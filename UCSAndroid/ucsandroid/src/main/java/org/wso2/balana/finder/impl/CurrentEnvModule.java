package org.wso2.balana.finder.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.AttributeFinderModule;

public class CurrentEnvModule
  extends AttributeFinderModule
{
  public static final String ENVIRONMENT_CURRENT_TIME = "urn:oasis:names:tc:xacml:1.0:environment:current-time";
  public static final String ENVIRONMENT_CURRENT_DATE = "urn:oasis:names:tc:xacml:1.0:environment:current-date";
  public static final String ENVIRONMENT_CURRENT_DATETIME = "urn:oasis:names:tc:xacml:1.0:environment:current-dateTime";
  
  public boolean isDesignatorSupported()
  {
    return true;
  }
  
  public Set<String> getSupportedCategories()
  {
    HashSet<String> set = new HashSet();
    set.add("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");
    return set;
  }
  
  public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer, URI category, EvaluationCtx context)
  {
    if (!"urn:oasis:names:tc:xacml:3.0:attribute-category:environment".equals(category.toString())) {
      return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
    }
    String attrName = attributeId.toString();
    if (attrName.equals("urn:oasis:names:tc:xacml:1.0:environment:current-time")) {
      return handleTime(attributeType, issuer, context);
    }
    if (attrName.equals("urn:oasis:names:tc:xacml:1.0:environment:current-date")) {
      return handleDate(attributeType, issuer, context);
    }
    if (attrName.equals("urn:oasis:names:tc:xacml:1.0:environment:current-dateTime")) {
      return handleDateTime(attributeType, issuer, context);
    }
    return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
  }
  
  private EvaluationResult handleTime(URI type, String issuer, EvaluationCtx context)
  {
    if (!type.toString().equals("http://www.w3.org/2001/XMLSchema#time")) {
      return new EvaluationResult(BagAttribute.createEmptyBag(type));
    }
    return makeBag(context.getCurrentTime());
  }
  
  private EvaluationResult handleDate(URI type, String issuer, EvaluationCtx context)
  {
    if (!type.toString().equals("http://www.w3.org/2001/XMLSchema#date")) {
      return new EvaluationResult(BagAttribute.createEmptyBag(type));
    }
    return makeBag(context.getCurrentDate());
  }
  
  private EvaluationResult handleDateTime(URI type, String issuer, EvaluationCtx context)
  {
    if (!type.toString().equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
      return new EvaluationResult(BagAttribute.createEmptyBag(type));
    }
    return makeBag(context.getCurrentDateTime());
  }
  
  private EvaluationResult makeProcessingError(String message)
  {
    ArrayList code = new ArrayList();
    code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
    return new EvaluationResult(new Status(code, message));
  }
  
  private EvaluationResult makeBag(AttributeValue attribute)
  {
    Set set = new HashSet();
    set.add(attribute);
    
    BagAttribute bag = new BagAttribute(attribute.getType(), set);
    
    return new EvaluationResult(bag);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.impl.CurrentEnvModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
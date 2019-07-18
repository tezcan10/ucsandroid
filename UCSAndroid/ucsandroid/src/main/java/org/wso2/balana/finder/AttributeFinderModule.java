package org.wso2.balana.finder;

import java.net.URI;
import java.util.Set;
import org.w3c.dom.Node;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class AttributeFinderModule
{
  public String getIdentifier()
  {
    return getClass().getName();
  }
  
  public boolean isDesignatorSupported()
  {
    return false;
  }
  
  public boolean isSelectorSupported()
  {
    return false;
  }
  
  public Set<String> getSupportedCategories()
  {
    return null;
  }
  
  public Set getSupportedIds()
  {
    return null;
  }
  
  public void invalidateCache() {}
  
  public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer, URI category, EvaluationCtx context)
  {
    return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
  }
  
  public EvaluationResult findAttribute(String contextPath, URI attributeType, String contextSelector, Node root, EvaluationCtx context, String xpathVersion)
  {
    return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.AttributeFinderModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.finder;

import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class ResourceFinderModule
{
  public String getIdentifier()
  {
    return getClass().getName();
  }
  
  public boolean isChildSupported()
  {
    return false;
  }
  
  public boolean isDescendantSupported()
  {
    return false;
  }
  
  public void invalidateCache() {}
  
  public ResourceFinderResult findChildResources(AttributeValue parentResourceId, EvaluationCtx context)
  {
    return new ResourceFinderResult();
  }
  
  /**
   * @deprecated
   */
  public ResourceFinderResult findChildResources(AttributeValue parentResourceId)
  {
    return new ResourceFinderResult();
  }
  
  public ResourceFinderResult findDescendantResources(AttributeValue parentResourceId, EvaluationCtx context)
  {
    return new ResourceFinderResult();
  }
  
  /**
   * @deprecated
   */
  public ResourceFinderResult findDescendantResources(AttributeValue parentResourceId)
  {
    return new ResourceFinderResult();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.ResourceFinderModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
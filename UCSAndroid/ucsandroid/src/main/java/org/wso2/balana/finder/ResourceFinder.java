package org.wso2.balana.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;

public class ResourceFinder
{
  private List allModules;
  private List childModules;
  private List descendantModules;
  private static Log logger = LogFactory.getLog(ResourceFinder.class);
  
  public ResourceFinder()
  {
    allModules = new ArrayList();
    childModules = new ArrayList();
    descendantModules = new ArrayList();
  }
  
  public List getModules()
  {
    return new ArrayList(allModules);
  }
  
  public void setModules(List modules)
  {
    Iterator it = modules.iterator();
    
    allModules = new ArrayList(modules);
    childModules = new ArrayList();
    descendantModules = new ArrayList();
    while (it.hasNext())
    {
      ResourceFinderModule module = (ResourceFinderModule)it.next();
      if (module.isChildSupported()) {
        childModules.add(module);
      }
      if (module.isDescendantSupported()) {
        descendantModules.add(module);
      }
    }
  }
  
  public ResourceFinderResult findChildResources(AttributeValue parentResourceId, EvaluationCtx context)
  {
    Iterator it = childModules.iterator();
    while (it.hasNext())
    {
      ResourceFinderModule module = (ResourceFinderModule)it.next();
      
      ResourceFinderResult result = module.findChildResources(parentResourceId, context);
      if (!result.isEmpty()) {
        return result;
      }
    }
    logger.info("No ResourceFinderModule existed to handle the children of " + 
      parentResourceId.encode());
    
    return new ResourceFinderResult();
  }
  
  /**
   * @deprecated
   */
  public ResourceFinderResult findChildResources(AttributeValue parentResourceId)
  {
    Iterator it = childModules.iterator();
    while (it.hasNext())
    {
      ResourceFinderModule module = (ResourceFinderModule)it.next();
      
      ResourceFinderResult result = module.findChildResources(parentResourceId);
      if (!result.isEmpty()) {
        return result;
      }
    }
    logger.info("No ResourceFinderModule existed to handle the children of " + 
      parentResourceId.encode());
    
    return new ResourceFinderResult();
  }
  
  public ResourceFinderResult findDescendantResources(AttributeValue parentResourceId, EvaluationCtx context)
  {
    Iterator it = descendantModules.iterator();
    while (it.hasNext())
    {
      ResourceFinderModule module = (ResourceFinderModule)it.next();
      
      ResourceFinderResult result = module.findDescendantResources(parentResourceId, context);
      if (!result.isEmpty()) {
        return result;
      }
    }
    logger.info("No ResourceFinderModule existed to handle the descendants of " + 
      parentResourceId.encode());
    
    return new ResourceFinderResult();
  }
  
  /**
   * @deprecated
   */
  public ResourceFinderResult findDescendantResources(AttributeValue parentResourceId)
  {
    Iterator it = descendantModules.iterator();
    while (it.hasNext())
    {
      ResourceFinderModule module = (ResourceFinderModule)it.next();
      
      ResourceFinderResult result = module.findDescendantResources(parentResourceId);
      if (!result.isEmpty()) {
        return result;
      }
    }
    logger.info("No ResourceFinderModule existed to handle the descendants of " + 
      parentResourceId.encode());
    
    return new ResourceFinderResult();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.ResourceFinder
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
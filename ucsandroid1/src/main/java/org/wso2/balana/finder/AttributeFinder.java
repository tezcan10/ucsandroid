package org.wso2.balana.finder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AttributeFinder
{
  private List<AttributeFinderModule> allModules;
  private List<AttributeFinderModule> designatorModules;
  private List<AttributeFinderModule> selectorModules;
  private static Log logger = LogFactory.getLog(AttributeFinder.class);
  
  public AttributeFinder()
  {
    allModules = new ArrayList();
    designatorModules = new ArrayList();
    selectorModules = new ArrayList();
  }
  
  public List<AttributeFinderModule> getModules()
  {
    return new ArrayList(allModules);
  }
  
  public void setModules(List<AttributeFinderModule> modules)
  {
    Iterator it = modules.iterator();
    
    allModules = new ArrayList(modules);
    designatorModules = new ArrayList();
    selectorModules = new ArrayList();
    while (it.hasNext())
    {
      AttributeFinderModule module = (AttributeFinderModule)it.next();
      if (module.isDesignatorSupported()) {
        designatorModules.add(module);
      }
      if (module.isSelectorSupported()) {
        selectorModules.add(module);
      }
    }
  }
  
  public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer, URI category, EvaluationCtx context)
  {
    Iterator it = designatorModules.iterator();
    while (it.hasNext())
    {
      AttributeFinderModule module = (AttributeFinderModule)it.next();
      
      EvaluationResult result = module.findAttribute(attributeType, attributeId, issuer, 
        category, context);
      if (result.indeterminate())
      {
        logger.info("Error while trying to resolve values: " + 
          result.getStatus().getMessage());
        return result;
      }
      BagAttribute bag = (BagAttribute)result.getAttributeValue();
      if (!bag.isEmpty()) {
        return result;
      }
    }
    logger.info("Failed to resolve any values for " + attributeId.toString());
    
    return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
  }
  
  public EvaluationResult findAttribute(String contextPath, URI attributeType, EvaluationCtx context, String xpathVersion)
  {
    Iterator it = selectorModules.iterator();
    while (it.hasNext())
    {
      AttributeFinderModule module = (AttributeFinderModule)it.next();
      
      EvaluationResult result = module.findAttribute(contextPath, 
        attributeType, null, null, context, xpathVersion);
      if (result.indeterminate())
      {
        logger.info("Error while trying to resolve values: " + 
          result.getStatus().getMessage());
        return result;
      }
      BagAttribute bag = (BagAttribute)result.getAttributeValue();
      if (!bag.isEmpty()) {
        return result;
      }
    }
    logger.info("Failed to resolve any values for " + contextPath);
    
    return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
  }
  
  public EvaluationResult findAttribute(String contextPath, String contextSelector, URI attributeType, Node root, EvaluationCtx context, String xpathVersion)
  {
    Iterator it = selectorModules.iterator();
    while (it.hasNext())
    {
      AttributeFinderModule module = (AttributeFinderModule)it.next();
      
      EvaluationResult result = module.findAttribute(contextPath, 
        attributeType, contextSelector, root, context, xpathVersion);
      if (result.indeterminate())
      {
        logger.info("Error while trying to resolve values: " + 
          result.getStatus().getMessage());
        return result;
      }
      BagAttribute bag = (BagAttribute)result.getAttributeValue();
      if (!bag.isEmpty()) {
        return result;
      }
    }
    logger.info("Failed to resolve any values for " + contextPath);
    
    return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.AttributeFinder
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
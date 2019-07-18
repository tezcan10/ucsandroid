package org.wso2.balana.finder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class PolicyFinder
{
  private Set allModules;
  private Set requestModules;
  private Set referenceModules;
  private static Log logger = LogFactory.getLog(PolicyFinder.class);
  
  public Set getModules()
  {
    return new HashSet(allModules);
  }
  
  public void setModules(Set modules)
  {
    Iterator it = modules.iterator();
    
    allModules = new HashSet(modules);
    requestModules = new HashSet();
    referenceModules = new HashSet();
    while (it.hasNext())
    {
      PolicyFinderModule module = (PolicyFinderModule)it.next();
      if (module.isRequestSupported()) {
        requestModules.add(module);
      }
      if (module.isIdReferenceSupported()) {
        referenceModules.add(module);
      }
    }
  }
  
  public void init()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Initializing PolicyFinder");
    }
    Iterator it = allModules.iterator();
    while (it.hasNext())
    {
      PolicyFinderModule module = (PolicyFinderModule)it.next();
      module.init(this);
    }
  }
  
  public PolicyFinderResult findPolicy(EvaluationCtx context)
  {
    PolicyFinderResult result = null;
    Iterator it = requestModules.iterator();
    while (it.hasNext())
    {
      PolicyFinderModule module = (PolicyFinderModule)it.next();
      PolicyFinderResult newResult = module.findPolicy(context);
      if (newResult.indeterminate())
      {
        logger.info("An error occured while trying to find a single applicable policy for a request: " + 
        
          newResult.getStatus().getMessage());
        
        return newResult;
      }
      if (!newResult.notApplicable())
      {
        if (result != null)
        {
          logger.info("More than one top-level applicable policy for the request");
          
          ArrayList code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          Status status = new Status(code, "too many applicable top-level policies");
          return new PolicyFinderResult(status);
        }
        result = newResult;
      }
    }
    if (result != null) {
      return result;
    }
    logger.info("No applicable policies were found for the request");
    
    return new PolicyFinderResult();
  }
  
  public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData)
    throws IllegalArgumentException
  {
    PolicyFinderResult result = null;
    Iterator it = referenceModules.iterator();
    if ((type != 0) && 
      (type != 1)) {
      throw new IllegalArgumentException("Unknown reference type");
    }
    while (it.hasNext())
    {
      PolicyFinderModule module = (PolicyFinderModule)it.next();
      PolicyFinderResult newResult = module.findPolicy(idReference, type, constraints, 
        parentMetaData);
      if (newResult.indeterminate())
      {
        logger.info("An error occured while trying to find the referenced policy " + 
          idReference.toString() + ": " + newResult.getStatus().getMessage());
        
        return newResult;
      }
      if (!newResult.notApplicable())
      {
        if (result != null)
        {
          logger.info("More than one policy applies for the reference: " + 
            idReference.toString());
          ArrayList code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          Status status = new Status(code, "too many applicable top-level policies");
          return new PolicyFinderResult(status);
        }
        result = newResult;
      }
    }
    if (result != null) {
      return result;
    }
    logger.info("No policies were resolved for the reference: " + idReference.toString());
    return new PolicyFinderResult();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.PolicyFinder
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
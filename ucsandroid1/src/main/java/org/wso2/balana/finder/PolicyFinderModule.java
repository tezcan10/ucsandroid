package org.wso2.balana.finder;

import java.net.URI;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class PolicyFinderModule
{
  public String getIdentifier()
  {
    return getClass().getName();
  }
  
  public boolean isRequestSupported()
  {
    return false;
  }
  
  public boolean isIdReferenceSupported()
  {
    return false;
  }
  
  public abstract void init(PolicyFinder paramPolicyFinder);
  
  public void invalidateCache() {}
  
  public PolicyFinderResult findPolicy(EvaluationCtx context)
  {
    return new PolicyFinderResult();
  }
  
  public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData)
  {
    return new PolicyFinderResult();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.PolicyFinderModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
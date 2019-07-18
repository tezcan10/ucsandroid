package org.wso2.balana.finder;

import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.ctx.Status;

public class PolicyFinderResult
{
  private AbstractPolicy policy;
  private Status status;
  
  public PolicyFinderResult()
  {
    policy = null;
    status = null;
  }
  
  public PolicyFinderResult(AbstractPolicy policy)
  {
    this.policy = policy;
    status = null;
  }
  
  public PolicyFinderResult(Status status)
  {
    policy = null;
    this.status = status;
  }
  
  public boolean notApplicable()
  {
    return (policy == null) && (status == null);
  }
  
  public boolean indeterminate()
  {
    return status != null;
  }
  
  public AbstractPolicy getPolicy()
  {
    return policy;
  }
  
  public Status getStatus()
  {
    return status;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.PolicyFinderResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
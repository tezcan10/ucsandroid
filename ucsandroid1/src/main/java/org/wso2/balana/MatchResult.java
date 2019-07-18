package org.wso2.balana;

import org.wso2.balana.ctx.Status;

public class MatchResult
{
  public static final int MATCH = 0;
  public static final int NO_MATCH = 1;
  public static final int INDETERMINATE = 2;
  private int result;
  private Status status;
  private String policyValue;
  private String subjectPolicyValue;
  private String resourcePolicyValue;
  private String actionPolicyValue;
  private String envPolicyValue;
  
  public String getSubjectPolicyValue()
  {
    return subjectPolicyValue;
  }
  
  public void setSubjectPolicyValue(String subjectPolicyValue)
  {
    this.subjectPolicyValue = subjectPolicyValue;
  }
  
  public String getResourcePolicyValue()
  {
    return resourcePolicyValue;
  }
  
  public void setResourcePolicyValue(String resourcePolicyValue)
  {
    this.resourcePolicyValue = resourcePolicyValue;
  }
  
  public String getActionPolicyValue()
  {
    return actionPolicyValue;
  }
  
  public void setActionPolicyValue(String actionPolicyValue)
  {
    this.actionPolicyValue = actionPolicyValue;
  }
  
  public String getEnvPolicyValue()
  {
    return envPolicyValue;
  }
  
  public void setEnvPolicyValue(String envPolicyValue)
  {
    this.envPolicyValue = envPolicyValue;
  }
  
  public String getPolicyValue()
  {
    return policyValue;
  }
  
  public void setPolicyValue(String policyValue)
  {
    this.policyValue = policyValue;
  }
  
  public MatchResult(int result)
  {
    this(result, null);
  }
  
  public MatchResult(int result, Status status)
    throws IllegalArgumentException
  {
    if ((result != 0) && (result != 1) && (result != 2)) {
      throw new IllegalArgumentException("Input result is not a validvalue");
    }
    this.result = result;
    this.status = status;
  }
  
  public int getResult()
  {
    return result;
  }
  
  public Status getStatus()
  {
    return status;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.MatchResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import org.wso2.balana.MatchResult;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.ctx.Status;

public class EvaluationResult
{
  private boolean wasInd;
  private AttributeValue value;
  private Status status;
  private MatchResult matchResult;
  private static EvaluationResult falseBooleanResult;
  private static EvaluationResult trueBooleanResult;
  
  public EvaluationResult(AttributeValue value)
  {
    wasInd = false;
    this.value = value;
    status = null;
  }
  
  public EvaluationResult(Status status)
  {
    wasInd = true;
    value = null;
    this.status = status;
  }
  
  public MatchResult getMatchResult()
  {
    return matchResult;
  }
  
  public void setMatchResult(MatchResult matchResult)
  {
    this.matchResult = matchResult;
  }
  
  public boolean indeterminate()
  {
    return wasInd;
  }
  
  public AttributeValue getAttributeValue()
  {
    return value;
  }
  
  public Status getStatus()
  {
    return status;
  }
  
  public static EvaluationResult getInstance(boolean value)
  {
    if (value) {
      return getTrueInstance();
    }
    return getFalseInstance();
  }
  
  public static EvaluationResult getFalseInstance()
  {
    if (falseBooleanResult == null) {
      falseBooleanResult = new EvaluationResult(BooleanAttribute.getFalseInstance());
    }
    return falseBooleanResult;
  }
  
  public static EvaluationResult getTrueInstance()
  {
    if (trueBooleanResult == null) {
      trueBooleanResult = new EvaluationResult(BooleanAttribute.getTrueInstance());
    }
    return trueBooleanResult;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.EvaluationResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
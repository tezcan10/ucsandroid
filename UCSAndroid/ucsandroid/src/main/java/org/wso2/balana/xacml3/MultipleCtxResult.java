package org.wso2.balana.xacml3;

import java.util.Set;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class MultipleCtxResult
{
  private Set<EvaluationCtx> evaluationCtxSet;
  private Status status;
  private boolean indeterminate;
  
  public MultipleCtxResult(Set<EvaluationCtx> evaluationCtxSet)
  {
    this(evaluationCtxSet, null, false);
  }
  
  public MultipleCtxResult(Set<EvaluationCtx> evaluationCtxSet, Status status, boolean indeterminate)
  {
    this.evaluationCtxSet = evaluationCtxSet;
    this.status = status;
    this.indeterminate = indeterminate;
  }
  
  public Set<EvaluationCtx> getEvaluationCtxSet()
  {
    return evaluationCtxSet;
  }
  
  public void setEvaluationCtxSet(Set<EvaluationCtx> evaluationCtxSet)
  {
    this.evaluationCtxSet = evaluationCtxSet;
  }
  
  public Status getStatus()
  {
    if (indeterminate) {
      return status;
    }
    return null;
  }
  
  public void setStatus(Status status)
  {
    this.status = status;
  }
  
  public boolean isIndeterminate()
  {
    return indeterminate;
  }
  
  public void setIndeterminate(boolean indeterminate)
  {
    this.indeterminate = indeterminate;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.MultipleCtxResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
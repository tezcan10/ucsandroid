package org.wso2.balana.cond;

import java.util.List;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract interface Evaluatable
  extends Expression
{
  public abstract EvaluationResult evaluate(EvaluationCtx paramEvaluationCtx);
  
  /**
   * @deprecated
   */
  public abstract boolean evaluatesToBag();
  
  public abstract List getChildren();
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.Evaluatable
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana;

import org.wso2.balana.ctx.EvaluationCtx;

public abstract class AbstractTarget
{
  public abstract MatchResult match(EvaluationCtx paramEvaluationCtx);
}

/* Location:
 * Qualified Name:     org.wso2.balana.AbstractTarget
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
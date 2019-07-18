package org.wso2.balana.combine;

import java.net.URI;
import java.util.List;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class RuleCombiningAlgorithm
  extends CombiningAlgorithm
{
  public RuleCombiningAlgorithm(URI identifier)
  {
    super(identifier);
  }
  
  public abstract AbstractResult combine(EvaluationCtx paramEvaluationCtx, List paramList1, List paramList2);
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.RuleCombiningAlgorithm
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
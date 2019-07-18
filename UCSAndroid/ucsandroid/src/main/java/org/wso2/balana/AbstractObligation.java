package org.wso2.balana;

import java.net.URI;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class AbstractObligation
{
  protected URI obligationId;
  protected int fulfillOn;
  
  public abstract ObligationResult evaluate(EvaluationCtx paramEvaluationCtx);
  
  public int getFulfillOn()
  {
    return fulfillOn;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.AbstractObligation
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
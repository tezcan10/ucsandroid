package org.wso2.balana.combine;

import java.net.URI;
import java.util.List;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class CombiningAlgorithm
{
  private URI identifier;
  
  public CombiningAlgorithm(URI identifier)
  {
    this.identifier = identifier;
  }
  
  public abstract AbstractResult combine(EvaluationCtx paramEvaluationCtx, List paramList1, List paramList2);
  
  public URI getIdentifier()
  {
    return identifier;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.CombiningAlgorithm
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
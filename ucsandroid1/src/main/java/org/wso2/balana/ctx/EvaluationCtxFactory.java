package org.wso2.balana.ctx;

import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.xacml2.XACML2EvaluationCtx;
import org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx;

public class EvaluationCtxFactory
{
  private static volatile EvaluationCtxFactory factoryInstance;
  
  public EvaluationCtx getEvaluationCtx(AbstractRequestCtx requestCtx, PDPConfig pdpConfig)
    throws ParsingException
  {
    if (3 == requestCtx.getXacmlVersion()) {
      return new XACML3EvaluationCtx((org.wso2.balana.ctx.xacml3.RequestCtx)requestCtx, pdpConfig);
    }
    return new XACML2EvaluationCtx((org.wso2.balana.ctx.xacml2.RequestCtx)requestCtx, pdpConfig);
  }
  
  public static EvaluationCtxFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (EvaluationCtxFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new EvaluationCtxFactory();
        }
      }
    }
    return factoryInstance;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.EvaluationCtxFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
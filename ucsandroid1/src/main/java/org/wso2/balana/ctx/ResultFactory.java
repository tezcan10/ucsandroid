package org.wso2.balana.ctx;

import java.util.List;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.xacml3.Advice;

public class ResultFactory
{
  private static volatile ResultFactory factoryInstance;
  
  public AbstractResult getResult(int decision, Status status, int version)
  {
    if (version == 3) {
      return new org.wso2.balana.ctx.xacml3.Result(decision, status, null, null, null);
    }
    return new org.wso2.balana.ctx.xacml2.Result(decision, status);
  }
  
  public AbstractResult getResult(int decision, EvaluationCtx evaluationCtx)
  {
    if (evaluationCtx.getXacmlVersion() == 3) {
      return new org.wso2.balana.ctx.xacml3.Result(decision, null, null, null, evaluationCtx);
    }
    return new org.wso2.balana.ctx.xacml2.Result(decision, null);
  }
  
  public AbstractResult getResult(int decision, Status status, EvaluationCtx evaluationCtx)
  {
    if (evaluationCtx.getXacmlVersion() == 3) {
      return new org.wso2.balana.ctx.xacml3.Result(decision, status, null, null, evaluationCtx);
    }
    return new org.wso2.balana.ctx.xacml2.Result(decision, status);
  }
  
  public AbstractResult getResult(int decision, List<ObligationResult> obligationResults, List<Advice> advices, EvaluationCtx evaluationCtx)
  {
    if (evaluationCtx.getXacmlVersion() == 3) {
      return new org.wso2.balana.ctx.xacml3.Result(decision, null, obligationResults, 
        advices, evaluationCtx);
    }
    return new org.wso2.balana.ctx.xacml2.Result(decision, null, obligationResults);
  }
  
  public AbstractResult getResult(int decision, Status status, List<ObligationResult> obligationResults, List<Advice> advices, EvaluationCtx evaluationCtx)
  {
    if (evaluationCtx.getXacmlVersion() == 3) {
      return new org.wso2.balana.ctx.xacml3.Result(decision, status, obligationResults, 
        advices, evaluationCtx);
    }
    return new org.wso2.balana.ctx.xacml2.Result(decision, status, obligationResults);
  }
  
  public static ResultFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (ResultFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new ResultFactory();
        }
      }
    }
    return factoryInstance;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.ResultFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
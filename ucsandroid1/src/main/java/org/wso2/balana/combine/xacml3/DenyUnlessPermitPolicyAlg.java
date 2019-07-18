package org.wso2.balana.combine.xacml3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.xacml3.Advice;

public class DenyUnlessPermitPolicyAlg
  extends PolicyCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public DenyUnlessPermitPolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List policyElements)
  {
    List<ObligationResult> denyObligations = new ArrayList();
    List<Advice> denyAdvices = new ArrayList();
    for (Object policyElement : policyElements)
    {
      AbstractPolicy policy = ((PolicyCombinerElement)policyElement).getPolicy();
      AbstractResult result = policy.evaluate(context);
      int value = result.getDecision();
      if (value == 0) {
        return result;
      }
      if (value == 1)
      {
        denyObligations.addAll(result.getObligations());
        denyAdvices.addAll(result.getAdvices());
      }
    }
    return ResultFactory.getFactory().getResult(1, denyObligations, 
      denyAdvices, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml3.DenyUnlessPermitPolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
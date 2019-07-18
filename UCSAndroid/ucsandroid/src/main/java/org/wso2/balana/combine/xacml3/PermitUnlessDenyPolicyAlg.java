package org.wso2.balana.combine.xacml3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.xacml3.Advice;

public class PermitUnlessDenyPolicyAlg
  extends PolicyCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public PermitUnlessDenyPolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public PermitUnlessDenyPolicyAlg(URI identifier)
  {
    super(identifier);
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements)
  {
    List<ObligationResult> permitObligations = new ArrayList();
    List<Advice> permitAdvices = new ArrayList();
    for (Object ruleElement : ruleElements)
    {
      Rule rule = ((RuleCombinerElement)ruleElement).getRule();
      AbstractResult result = rule.evaluate(context);
      int value = result.getDecision();
      if (value == 1) {
        return result;
      }
      if (value == 0)
      {
        permitObligations.addAll(result.getObligations());
        permitAdvices.addAll(result.getAdvices());
      }
    }
    return ResultFactory.getFactory().getResult(0, 
      permitObligations, permitAdvices, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml3.PermitUnlessDenyPolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
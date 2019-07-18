package org.wso2.balana.combine.xacml3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.xacml3.Advice;

public class DenyUnlessPermitRuleAlg
  extends RuleCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public DenyUnlessPermitRuleAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public DenyUnlessPermitRuleAlg(URI identifier)
  {
    super(identifier);
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements)
  {
    List<ObligationResult> denyObligations = new ArrayList();
    List<Advice> denyAdvices = new ArrayList();
    for (Object ruleElement : ruleElements)
    {
      Rule rule = ((RuleCombinerElement)ruleElement).getRule();
      AbstractResult result = rule.evaluate(context);
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
 * Qualified Name:     org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
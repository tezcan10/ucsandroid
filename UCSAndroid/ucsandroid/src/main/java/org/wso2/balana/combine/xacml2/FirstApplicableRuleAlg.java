package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;

public class FirstApplicableRuleAlg
  extends RuleCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public FirstApplicableRuleAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements)
  {
    Iterator it = ruleElements.iterator();
    while (it.hasNext())
    {
      Rule rule = ((RuleCombinerElement)it.next()).getRule();
      AbstractResult result = rule.evaluate(context);
      int value = result.getDecision();
      if (value != 3) {
        return result;
      }
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.FirstApplicableRuleAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
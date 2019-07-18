package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.xacml3.Advice;

public class PermitOverridesRuleAlg
  extends RuleCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public PermitOverridesRuleAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  protected PermitOverridesRuleAlg(URI identifier)
  {
    super(identifier);
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements)
  {
    boolean atLeastOneError = false;
    boolean potentialPermit = false;
    boolean atLeastOneDeny = false;
    AbstractResult firstIndeterminateResult = null;
    List<ObligationResult> denyObligations = new ArrayList();
    List<Advice> denyAdvices = new ArrayList();
    Iterator it = ruleElements.iterator();
    while (it.hasNext())
    {
      Rule rule = ((RuleCombinerElement)it.next()).getRule();
      AbstractResult result = rule.evaluate(context);
      int value = result.getDecision();
      if (value == 0) {
        return result;
      }
      if ((value == 2) || 
        (value == 4) || 
        (value == 5) || 
        (value == 6))
      {
        atLeastOneError = true;
        if (firstIndeterminateResult == null) {
          firstIndeterminateResult = result;
        }
        if (rule.getEffect() == 0) {
          potentialPermit = true;
        }
      }
      else
      {
        if (value == 1) {
          atLeastOneDeny = true;
        }
        denyAdvices.addAll(result.getAdvices());
        denyObligations.addAll(result.getObligations());
      }
    }
    if (potentialPermit) {
      return firstIndeterminateResult;
    }
    if (atLeastOneDeny) {
      return ResultFactory.getFactory().getResult(1, denyObligations, 
        denyAdvices, context);
    }
    if (atLeastOneError) {
      return firstIndeterminateResult;
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.PermitOverridesRuleAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
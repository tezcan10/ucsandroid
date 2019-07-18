package org.wso2.balana.combine.xacml3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.xacml3.Advice;

public class DenyOverridesPolicyAlg
  extends PolicyCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public DenyOverridesPolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  protected DenyOverridesPolicyAlg(URI identifier)
  {
    super(identifier);
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List policyElements)
  {
    boolean atLeastOneErrorD = false;
    boolean atLeastOneErrorP = false;
    boolean atLeastOneErrorDP = false;
    boolean atLeastOnePermit = false;
    AbstractResult firstIndeterminateResultD = null;
    AbstractResult firstIndeterminateResultP = null;
    AbstractResult firstIndeterminateResultDP = null;
    List<ObligationResult> permitObligations = new ArrayList();
    List<Advice> permitAdvices = new ArrayList();
    Iterator it = policyElements.iterator();
    while (it.hasNext())
    {
      AbstractPolicy policy = ((PolicyCombinerElement)it.next()).getPolicy();
      
      MatchResult match = policy.match(context);
      if (match.getResult() == 2) {
        return ResultFactory.getFactory().getResult(1, context);
      }
      if (match.getResult() == 0)
      {
        AbstractResult result = policy.evaluate(context);
        int value = result.getDecision();
        if (value == 1) {
          return result;
        }
        if (value != 3) {
          if (value == 0)
          {
            atLeastOnePermit = true;
            permitAdvices.addAll(result.getAdvices());
            permitObligations.addAll(result.getObligations());
          }
          else if (value == 4)
          {
            atLeastOneErrorD = true;
            if (firstIndeterminateResultD == null) {
              firstIndeterminateResultD = result;
            }
          }
          else if (value == 5)
          {
            atLeastOneErrorP = true;
            if (firstIndeterminateResultP == null) {
              firstIndeterminateResultP = result;
            }
          }
          else if (value == 6)
          {
            atLeastOneErrorDP = true;
            if (firstIndeterminateResultDP == null) {
              firstIndeterminateResultDP = result;
            }
          }
        }
      }
    }
    if (atLeastOneErrorDP) {
      return firstIndeterminateResultDP;
    }
    if ((atLeastOneErrorD) && (atLeastOneErrorP) && (atLeastOnePermit)) {
      return ResultFactory.getFactory().getResult(6, 
        firstIndeterminateResultD.getStatus(), context);
    }
    if (atLeastOneErrorD) {
      return firstIndeterminateResultD;
    }
    if (atLeastOnePermit) {
      return ResultFactory.getFactory().getResult(0, 
        permitObligations, permitAdvices, context);
    }
    if (atLeastOneErrorP) {
      return firstIndeterminateResultP;
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml3.DenyOverridesPolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
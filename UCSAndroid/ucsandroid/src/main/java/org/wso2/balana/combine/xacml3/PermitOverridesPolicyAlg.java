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

public class PermitOverridesPolicyAlg
  extends PolicyCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public PermitOverridesPolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  protected PermitOverridesPolicyAlg(URI identifier)
  {
    super(identifier);
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List policyElements)
  {
    boolean atLeastOneErrorD = false;
    boolean atLeastOneErrorP = false;
    boolean atLeastOneErrorDP = false;
    boolean atLeastOneDeny = false;
    AbstractResult firstIndeterminateResultD = null;
    AbstractResult firstIndeterminateResultP = null;
    AbstractResult firstIndeterminateResultDP = null;
    List<ObligationResult> denyObligations = new ArrayList();
    List<Advice> denyAdvices = new ArrayList();
    
    Iterator it = policyElements.iterator();
    while (it.hasNext())
    {
      AbstractPolicy policy = ((PolicyCombinerElement)it.next()).getPolicy();
      
      MatchResult match = policy.match(context);
      if (match.getResult() != 2) {
        if (match.getResult() == 0)
        {
          AbstractResult result = policy.evaluate(context);
          int value = result.getDecision();
          if (value == 0) {
            return result;
          }
          if (value != 3) {
            if (value == 1)
            {
              atLeastOneDeny = true;
              denyAdvices.addAll(result.getAdvices());
              denyObligations.addAll(result.getObligations());
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
    }
    if (atLeastOneErrorDP) {
      return firstIndeterminateResultDP;
    }
    if ((atLeastOneErrorP) && ((atLeastOneErrorD) || (atLeastOneDeny))) {
      return ResultFactory.getFactory().getResult(6, 
        firstIndeterminateResultP.getStatus(), context);
    }
    if (atLeastOneErrorP) {
      return ResultFactory.getFactory().getResult(5, 
        firstIndeterminateResultP.getStatus(), context);
    }
    if (atLeastOneDeny) {
      return ResultFactory.getFactory().getResult(1, 
        denyObligations, denyAdvices, context);
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml3.PermitOverridesPolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
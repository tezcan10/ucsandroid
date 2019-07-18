package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;

public class FirstApplicablePolicyAlg
  extends PolicyCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public FirstApplicablePolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List policyElements)
  {
    Iterator it = policyElements.iterator();
    while (it.hasNext())
    {
      AbstractPolicy policy = ((PolicyCombinerElement)it.next()).getPolicy();
      
      MatchResult match = policy.match(context);
      if (match.getResult() == 2) {
        return ResultFactory.getFactory().getResult(2, 
          match.getStatus(), context);
      }
      if (match.getResult() == 0)
      {
        AbstractResult result = policy.evaluate(context);
        int effect = result.getDecision();
        if ((effect != 3) && (!context.isSearching())) {
          return result;
        }
      }
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.FirstApplicablePolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
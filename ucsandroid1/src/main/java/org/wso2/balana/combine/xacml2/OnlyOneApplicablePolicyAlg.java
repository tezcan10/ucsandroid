package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.Status;

public class OnlyOneApplicablePolicyAlg
  extends PolicyCombiningAlgorithm
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public OnlyOneApplicablePolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public AbstractResult combine(EvaluationCtx context, List parameters, List policyElements)
  {
    boolean atLeastOne = false;
    AbstractPolicy selectedPolicy = null;
    Iterator it = policyElements.iterator();
    while (it.hasNext())
    {
      AbstractPolicy policy = ((PolicyCombinerElement)it.next()).getPolicy();
      
      MatchResult match = policy.match(context);
      int result = match.getResult();
      if (result == 2) {
        return ResultFactory.getFactory().getResult(2, 
          match.getStatus(), context);
      }
      if (result == 0)
      {
        if (atLeastOne)
        {
          List code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          String message = "Too many applicable policies";
          return ResultFactory.getFactory()
            .getResult(2, 
            new Status(code, message), context);
        }
        atLeastOne = true;
        selectedPolicy = policy;
      }
    }
    if (atLeastOne) {
      return selectedPolicy.evaluate(context);
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.OnlyOneApplicablePolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
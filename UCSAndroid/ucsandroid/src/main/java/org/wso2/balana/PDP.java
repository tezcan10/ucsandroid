package org.wso2.balana;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.combine.CombinerElement;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.EvaluationCtxFactory;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.xacml3.MultipleCtxResult;

public class PDP
{
  private PDPConfig pdpConfig;
  private PolicyFinder policyFinder;
  private static Log logger = LogFactory.getLog(PDP.class);
  
  public PDP(PDPConfig pdpConfig)
  {
    if (logger.isDebugEnabled()) {
      logger.debug("creating a PDP");
    }
    this.pdpConfig = pdpConfig;
    
    policyFinder = pdpConfig.getPolicyFinder();
    
    policyFinder.init();
  }
  
  public String evaluate(String request)
  {
    ResponseCtx responseCtx;
    try
    {
      AbstractRequestCtx requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request);
      responseCtx = evaluate(requestCtx);
    }
    catch (ParsingException e)
    {
      logger.error("Invalid request  : " + e.getMessage());
      
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
      Status status = new Status(code, e.getMessage());
      
      responseCtx = new ResponseCtx(new Result(2, status));
    }
    OutputStream stream = new ByteArrayOutputStream();
    responseCtx.encode(stream);
    return stream.toString();
  }
  
  public ResponseCtx evaluate(AbstractRequestCtx request)
  {
    EvaluationCtx evalContext = null;
    try
    {
      evalContext = EvaluationCtxFactory.getFactory().getEvaluationCtx(request, pdpConfig);
      return evaluate(evalContext);
    }
    catch (ParsingException e)
    {
      logger.error("Invalid request  : " + e.getMessage());
      
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
      Status status = new Status(code, e.getMessage());
      return new ResponseCtx(new Result(2, status));
    }
  }
  
  public ResponseCtx evaluate(EvaluationCtx context)
  {
    if (pdpConfig.isMultipleRequestHandle())
    {
      MultipleCtxResult multipleCtxResult = context.getMultipleEvaluationCtx();
      if (multipleCtxResult.isIndeterminate()) {
        return new ResponseCtx(ResultFactory.getFactory()
          .getResult(2, multipleCtxResult.getStatus(), context));
      }
      Set<EvaluationCtx> evaluationCtxSet = multipleCtxResult.getEvaluationCtxSet();
      HashSet<AbstractResult> results = new HashSet();
      for (EvaluationCtx ctx : evaluationCtxSet)
      {
        AbstractResult result = evaluateContext(ctx);
        
        results.add(result);
      }
      return new ResponseCtx(results);
    }
    if (((context instanceof XACML3EvaluationCtx)) && 
      (((XACML3EvaluationCtx)context).isMultipleAttributes()))
    {
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
      Status status = new Status(code, "PDP does not supports multiple decision profile. Multiple AttributesType elements with the same Category can be existed");
      
      return new ResponseCtx(ResultFactory.getFactory()
        .getResult(2, 
        status, context));
    }
    if (((context instanceof XACML3EvaluationCtx)) && 
      (((RequestCtx)context.getRequestCtx()).isCombinedDecision()))
    {
      List<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      Status status = new Status(code, "PDP does not supports multiple decision profile. Multiple decision is not existed to combine them");
      
      return new ResponseCtx(ResultFactory.getFactory()
        .getResult(2, 
        status, context));
    }
    return new ResponseCtx(evaluateContext(context));
  }
  
  private AbstractResult evaluateContext(EvaluationCtx context)
  {
    PolicyFinderResult finderResult = policyFinder.findPolicy(context);
    if (finderResult.notApplicable()) {
      return ResultFactory.getFactory().getResult(3, context);
    }
    if (finderResult.indeterminate()) {
      return ResultFactory.getFactory().getResult(2, 
        finderResult.getStatus(), context);
    }
    if (((context instanceof XACML3EvaluationCtx)) && 
      (((RequestCtx)context.getRequestCtx()).isReturnPolicyIdList()))
    {
      Set<PolicyReference> references = new HashSet();
      processPolicyReferences(finderResult.getPolicy(), references);
      ((XACML3EvaluationCtx)context).setPolicyReferences(references);
    }
    return finderResult.getPolicy().evaluate(context);
  }
  
  /**
   * @deprecated
   */
  public OutputStream evaluate(InputStream input)
  {
    AbstractRequestCtx request = null;
    ResponseCtx response = null;
    try
    {
      request = RequestCtxFactory.getFactory().getRequestCtx(input);
    }
    catch (Exception pe)
    {
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:syntax-error");
      Status status = new Status(code, "invalid request: " + pe.getMessage());
      
      response = new ResponseCtx(ResultFactory.getFactory()
        .getResult(2, status, 3));
    }
    if (response == null) {
      response = evaluate(request);
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    response.encode(out, new Indenter());
    
    return out;
  }
  
  private void processPolicyReferences(AbstractPolicy policy, Set<PolicyReference> references)
  {
    if ((policy instanceof Policy))
    {
      references.add(new PolicyReference(policy.getId(), 
        0, null, null));
    }
    else if ((policy instanceof PolicySet))
    {
      List<CombinerElement> elements = policy.getChildElements();
      if ((elements != null) && (elements.size() > 0)) {
        for (CombinerElement element : elements)
        {
          PolicyTreeElement treeElement = element.getElement();
          if ((treeElement instanceof AbstractPolicy)) {
            processPolicyReferences(policy, references);
          } else {
            references.add(new PolicyReference(policy.getId(), 
              1, null, null));
          }
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.PDP
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
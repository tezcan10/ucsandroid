package org.wso2.balana.ctx;

import java.net.URI;
import org.w3c.dom.Node;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.xacml3.MultipleCtxResult;

public abstract interface EvaluationCtx

{
  public abstract Node getRequestRoot();
  
  public abstract boolean isSearching();
  
  public abstract TimeAttribute getCurrentTime();
  
  public abstract DateAttribute getCurrentDate();
  
  public abstract DateTimeAttribute getCurrentDateTime();
  
  public abstract EvaluationResult getAttribute(URI paramURI1, URI paramURI2, String paramString, URI paramURI3);
  
  public abstract EvaluationResult getAttribute(String paramString1, URI paramURI1, URI paramURI2, URI paramURI3, String paramString2);
  
  public abstract int getXacmlVersion();
  
  public abstract AbstractRequestCtx getRequestCtx();
  
  public abstract MultipleCtxResult getMultipleEvaluationCtx();
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.EvaluationCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
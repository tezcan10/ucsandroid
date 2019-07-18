package org.wso2.balana;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract interface PolicyTreeElement
{
  public abstract List getChildren();
  
  public abstract String getDescription();
  
  public abstract URI getId();
  
  public abstract AbstractTarget getTarget();
  
  public abstract MatchResult match(EvaluationCtx paramEvaluationCtx);
  
  public abstract AbstractResult evaluate(EvaluationCtx paramEvaluationCtx);
  
  public abstract void encode(OutputStream paramOutputStream);
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
}

/* Location:
 * Qualified Name:     org.wso2.balana.PolicyTreeElement
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
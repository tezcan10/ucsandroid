package org.wso2.balana.cond;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import org.wso2.balana.Indenter;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract interface Function
  extends Expression
{
  public abstract EvaluationResult evaluate(List<Evaluatable> paramList, EvaluationCtx paramEvaluationCtx);
  
  public abstract URI getIdentifier();
  
  public abstract URI getReturnType();
  
  public abstract boolean returnsBag();
  
  public abstract void checkInputs(List paramList)
    throws IllegalArgumentException;
  
  public abstract void checkInputsNoBag(List paramList)
    throws IllegalArgumentException;
  
  public abstract void encode(OutputStream paramOutputStream);
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.Function
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
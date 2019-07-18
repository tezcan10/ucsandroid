package org.wso2.balana.ctx;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.wso2.balana.Indenter;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.xacml3.Advice;

public abstract class AbstractResult
{
  public static final int DECISION_PERMIT = 0;
  public static final int DECISION_DENY = 1;
  public static final int DECISION_INDETERMINATE = 2;
  public static final int DECISION_NOT_APPLICABLE = 3;
  public static final int DECISION_INDETERMINATE_DENY = 4;
  public static final int DECISION_INDETERMINATE_PERMIT = 5;
  public static final int DECISION_INDETERMINATE_DENY_OR_PERMIT = 6;
  public static final String[] DECISIONS = { "Permit", "Deny", "Indeterminate", "NotApplicable" };
  protected List<ObligationResult> obligations;
  protected List<Advice> advices;
  protected int decision = -1;
  protected Status status = null;
  
  public AbstractResult(int decision, Status status)
    throws IllegalArgumentException
  {
    if ((decision != 0) && (decision != 1) && 
      (decision != 2) && (decision != 3) && 
      (decision != 4) && (decision != 5) && 
      (decision != 6)) {
      throw new IllegalArgumentException("invalid decision value");
    }
    this.decision = decision;
    if (status == null) {
      this.status = Status.getOkInstance();
    } else {
      this.status = status;
    }
  }
  
  public AbstractResult(int decision, Status status, List<ObligationResult> obligationResults, List<Advice> advices)
    throws IllegalArgumentException
  {
    if ((decision != 0) && (decision != 1) && 
      (decision != 2) && (decision != 3) && 
      (decision != 4) && (decision != 5) && 
      (decision != 6)) {
      throw new IllegalArgumentException("invalid decision value");
    }
    this.decision = decision;
    if (obligationResults != null) {
      obligations = obligationResults;
    }
    if (advices != null) {
      this.advices = advices;
    }
    if (status == null) {
      this.status = Status.getOkInstance();
    } else {
      this.status = status;
    }
  }
  
  public List<ObligationResult> getObligations()
  {
    if (obligations == null) {
      obligations = new ArrayList();
    }
    return obligations;
  }
  
  public List<Advice> getAdvices()
  {
    if (advices == null) {
      advices = new ArrayList();
    }
    return advices;
  }
  
  public int getDecision()
  {
    return decision;
  }
  
  public Status getStatus()
  {
    return status;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.AbstractResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
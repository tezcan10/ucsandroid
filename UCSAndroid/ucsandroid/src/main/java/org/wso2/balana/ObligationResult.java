package org.wso2.balana;

import java.io.OutputStream;

public abstract interface ObligationResult
{
  public abstract void encode(OutputStream paramOutputStream);
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
}

/* Location:
 * Qualified Name:     org.wso2.balana.ObligationResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
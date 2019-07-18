package org.wso2.balana.cond;

import java.io.OutputStream;
import java.net.URI;
import org.wso2.balana.Indenter;

public abstract interface Expression
{
  public abstract URI getType();
  
  public abstract boolean returnsBag();
  
  public abstract void encode(OutputStream paramOutputStream);
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.Expression
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
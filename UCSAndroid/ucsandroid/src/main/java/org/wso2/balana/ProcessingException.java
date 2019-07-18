package org.wso2.balana;

public class ProcessingException
  extends RuntimeException
{
  public ProcessingException() {}
  
  public ProcessingException(String message)
  {
    super(message);
  }
  
  public ProcessingException(Throwable cause)
  {
    super(cause);
  }
  
  public ProcessingException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ProcessingException
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
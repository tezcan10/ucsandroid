package org.wso2.balana;

public class ParsingException
  extends Exception
{
  public ParsingException() {}
  
  public ParsingException(String message)
  {
    super(message);
  }
  
  public ParsingException(Throwable cause)
  {
    super(cause);
  }
  
  public ParsingException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ParsingException
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana;

import java.util.Arrays;

public class Indenter
{
  public static final int DEFAULT_WIDTH = 2;
  private int width;
  private int depth;
  
  public Indenter()
  {
    this(2);
  }
  
  public Indenter(int userWidth)
  {
    width = userWidth;
    depth = 0;
  }
  
  public void in()
  {
    depth += width;
  }
  
  public void out()
  {
    depth -= width;
  }
  
  public String makeString()
  {
    if (depth <= 0) {
      return "";
    }
    char[] array = new char[depth];
    Arrays.fill(array, ' ');
    
    return new String(array);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.Indenter
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
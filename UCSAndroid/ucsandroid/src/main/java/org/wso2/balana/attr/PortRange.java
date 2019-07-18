package org.wso2.balana.attr;

public class PortRange
{
  public static final int UNBOUND = -1;
  private int lowerBound;
  private int upperBound;
  
  public PortRange()
  {
    this(-1, -1);
  }
  
  public PortRange(int singlePort)
  {
    this(singlePort, singlePort);
  }
  
  public PortRange(int lowerBound, int upperBound)
  {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }
  
  public static PortRange getInstance(String value)
  {
    int lowerBound = -1;
    int upperBound = -1;
    if ((value.length() == 0) || (value.equals("-"))) {
      return new PortRange();
    }
    int dashPos = value.indexOf('-');
    if (dashPos == -1)
    {
      lowerBound = upperBound = Integer.parseInt(value);
    }
    else if (dashPos == 0)
    {
      upperBound = Integer.parseInt(value.substring(1));
    }
    else
    {
      lowerBound = Integer.parseInt(value.substring(0, dashPos));
      int len = value.length();
      if (dashPos != len - 1) {
        upperBound = Integer.parseInt(value.substring(dashPos + 1, len));
      }
    }
    return new PortRange(lowerBound, upperBound);
  }
  
  public int getLowerBound()
  {
    return lowerBound;
  }
  
  public int getUpperBound()
  {
    return upperBound;
  }
  
  public boolean isLowerBounded()
  {
    return lowerBound != -1;
  }
  
  public boolean isUpperBounded()
  {
    return upperBound != -1;
  }
  
  public boolean isSinglePort()
  {
    return (lowerBound == upperBound) && (lowerBound != -1);
  }
  
  public boolean isUnbound()
  {
    return (lowerBound == -1) && (upperBound == -1);
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof PortRange)) {
      return false;
    }
    PortRange other = (PortRange)o;
    if (lowerBound != lowerBound) {
      return false;
    }
    if (upperBound != upperBound) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = lowerBound;
    result = 31 * result + upperBound;
    return result;
  }
  
  public String encode()
  {
    if (isUnbound()) {
      return "";
    }
    if (isSinglePort()) {
      return String.valueOf(lowerBound);
    }
    if (!isLowerBounded()) {
      return "-" + String.valueOf(upperBound);
    }
    if (!isUpperBounded()) {
      return String.valueOf(lowerBound) + "-";
    }
    return String.valueOf(lowerBound) + "-" + String.valueOf(upperBound);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.PortRange
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
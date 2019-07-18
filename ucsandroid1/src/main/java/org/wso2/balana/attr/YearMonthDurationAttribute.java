package org.wso2.balana.attr;

import java.math.BigInteger;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class YearMonthDurationAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private static final String patternString = "(\\-)?P((\\d+)?Y)?((\\d+)?M)?";
  private static final int GROUP_SIGN = 1;
  private static final int GROUP_YEARS = 3;
  private static final int GROUP_MONTHS = 5;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  private static BigInteger big12 = BigInteger.valueOf(12L);
  private static BigInteger bigMaxLong = BigInteger.valueOf(Long.MAX_VALUE);
  private static Pattern pattern;
  private boolean negative;
  private long years;
  private long months;
  private long totalMonths;
  private String encodedValue = null;
  
  public YearMonthDurationAttribute(boolean negative, long years, long months)
    throws IllegalArgumentException
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.negative = negative;
    this.years = years;
    this.months = months;
    if ((years > 2147483647L) || (months > 2147483647L))
    {
      BigInteger bigMonths = BigInteger.valueOf(months);
      BigInteger bigYears = BigInteger.valueOf(years);
      
      BigInteger bigTotal = bigYears.multiply(big12).add(bigMonths);
      if (bigTotal.compareTo(bigMaxLong) == 1) {
        throw new IllegalArgumentException("total number of months exceeds Long.MAX_VALUE");
      }
      totalMonths = bigTotal.longValue();
      if (negative) {
        totalMonths = (-totalMonths);
      }
    }
    else
    {
      totalMonths = ((years * 12L + months) * (negative ? -1 : 1));
    }
  }
  
  public static YearMonthDurationAttribute getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  private static long parseGroup(Matcher matcher, int groupNumber)
    throws NumberFormatException
  {
    long groupLong = 0L;
    if (matcher.start(groupNumber) != -1)
    {
      String groupString = matcher.group(groupNumber);
      groupLong = Long.parseLong(groupString);
    }
    return groupLong;
  }
  
  public static YearMonthDurationAttribute getInstance(String value)
    throws ParsingException
  {
    boolean negative = false;
    long years = 0L;
    long months = 0L;
    if (pattern == null) {
      try
      {
        pattern = Pattern.compile("(\\-)?P((\\d+)?Y)?((\\d+)?M)?");
      }
      catch (PatternSyntaxException e)
      {
        throw new ParsingException("unexpected pattern syntax error");
      }
    }
    Matcher matcher = pattern.matcher(value);
    boolean matches = matcher.matches();
    if (!matches) {
      throw new ParsingException("Syntax error in yearMonthDuration");
    }
    if (matcher.start(1) != -1) {
      negative = true;
    }
    try
    {
      years = parseGroup(matcher, 3);
      
      months = parseGroup(matcher, 5);
    }
    catch (NumberFormatException e)
    {
      throw new ParsingException("Unable to handle number size");
    }
    return new YearMonthDurationAttribute(negative, years, months);
  }
  
  public boolean isNegative()
  {
    return negative;
  }
  
  public long getYears()
  {
    return years;
  }
  
  public long getMonths()
  {
    return months;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof YearMonthDurationAttribute)) {
      return false;
    }
    YearMonthDurationAttribute other = (YearMonthDurationAttribute)o;
    
    return totalMonths == totalMonths;
  }
  
  public int hashCode()
  {
    return (int)totalMonths ^ (int)(totalMonths >> 32);
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("YearMonthDurationAttribute: [\n");
    sb.append("  Negative: " + negative);
    sb.append("  Years: " + years);
    sb.append("  Months: " + months);
    sb.append("]");
    
    return sb.toString();
  }
  
  public String encode()
  {
    if (encodedValue != null) {
      return encodedValue;
    }
    StringBuffer buf = new StringBuffer(10);
    if (negative) {
      buf.append('-');
    }
    buf.append('P');
    if ((years != 0L) || (months == 0L))
    {
      buf.append(Long.toString(years));
      buf.append('Y');
    }
    if (months != 0L)
    {
      buf.append(Long.toString(months));
      buf.append('M');
    }
    encodedValue = buf.toString();
    
    return encodedValue;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.YearMonthDurationAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
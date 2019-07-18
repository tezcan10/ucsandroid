package org.wso2.balana.attr;

import java.math.BigInteger;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class DayTimeDurationAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private static final String patternString = "(\\-)?P((\\d+)?D)?(T((\\d+)?H)?((\\d+)?M)?((\\d+)?(.(\\d+)?)?S)?)?";
  private static final int GROUP_SIGN = 1;
  private static final int GROUP_DAYS = 3;
  private static final int GROUP_HOURS = 6;
  private static final int GROUP_MINUTES = 8;
  private static final int GROUP_SECONDS = 10;
  private static final int GROUP_NANOSECONDS = 12;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  private static BigInteger big24 = BigInteger.valueOf(24L);
  private static BigInteger big60 = BigInteger.valueOf(60L);
  private static BigInteger big1000 = BigInteger.valueOf(1000L);
  private static BigInteger bigMaxLong = BigInteger.valueOf(Long.MAX_VALUE);
  private static Pattern pattern;
  private boolean negative;
  private long days;
  private long hours;
  private long minutes;
  private long seconds;
  private int nanoseconds;
  private long totalMillis;
  private String encodedValue = null;
  
  public DayTimeDurationAttribute(boolean negative, long days, long hours, long minutes, long seconds, int nanoseconds)
    throws IllegalArgumentException
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
    this.negative = negative;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.nanoseconds = nanoseconds;
    if ((days > 2147483647L) || (hours > 2147483647L) || 
      (minutes > 2147483647L) || (seconds > 2147483647L))
    {
      BigInteger bigDays = BigInteger.valueOf(days);
      BigInteger bigHours = BigInteger.valueOf(hours);
      BigInteger bigMinutes = BigInteger.valueOf(minutes);
      BigInteger bigSeconds = BigInteger.valueOf(seconds);
      
      BigInteger bigTotal = bigDays.multiply(big24).add(bigHours).multiply(big60)
        .add(bigMinutes).multiply(big60).add(bigSeconds).multiply(big1000);
      if (bigTotal.compareTo(bigMaxLong) == 1) {
        throw new IllegalArgumentException("total number of milliseconds exceeds Long.MAX_VALUE");
      }
      totalMillis = bigTotal.longValue();
    }
    else
    {
      totalMillis = ((((days * 24L + hours) * 60L + minutes) * 60L + seconds) * 1000L);
    }
  }
  
  public static DayTimeDurationAttribute getInstance(Node root)
    throws ParsingException, NumberFormatException
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
  
  public static DayTimeDurationAttribute getInstance(String value)
    throws ParsingException, NumberFormatException
  {
    boolean negative = false;
    long days = 0L;
    long hours = 0L;
    long minutes = 0L;
    long seconds = 0L;
    int nanoseconds = 0;
    if (pattern == null) {
      try
      {
        pattern = Pattern.compile("(\\-)?P((\\d+)?D)?(T((\\d+)?H)?((\\d+)?M)?((\\d+)?(.(\\d+)?)?S)?)?");
      }
      catch (PatternSyntaxException e)
      {
        throw new ParsingException("unexpected pattern match error");
      }
    }
    Matcher matcher = pattern.matcher(value);
    boolean matches = matcher.matches();
    if (!matches) {
      throw new ParsingException("Syntax error in dayTimeDuration");
    }
    if (matcher.start(1) != -1) {
      negative = true;
    }
    try
    {
      days = parseGroup(matcher, 3);
      
      hours = parseGroup(matcher, 6);
      
      minutes = parseGroup(matcher, 8);
      
      seconds = parseGroup(matcher, 10);
      if (matcher.start(12) != -1)
      {
        String nanosecondString = matcher.group(12);
        if (nanosecondString.length() < 9)
        {
          StringBuffer buffer = new StringBuffer(nanosecondString);
          while (buffer.length() < 9) {
            buffer.append("0");
          }
          nanosecondString = buffer.toString();
        }
        if (nanosecondString.length() > 9) {
          nanosecondString = nanosecondString.substring(0, 9);
        }
        nanoseconds = Integer.parseInt(nanosecondString);
      }
    }
    catch (NumberFormatException e)
    {
      throw e;
    }
    if (value.charAt(value.length() - 1) == 'T') {
      throw new ParsingException("'T' must be absent if alltime items are absent");
    }
    return new DayTimeDurationAttribute(negative, days, hours, minutes, seconds, nanoseconds);
  }
  
  public boolean isNegative()
  {
    return negative;
  }
  
  public long getDays()
  {
    return days;
  }
  
  public long getHours()
  {
    return hours;
  }
  
  public long getMinutes()
  {
    return minutes;
  }
  
  public long getSeconds()
  {
    return seconds;
  }
  
  public int getNanoseconds()
  {
    return nanoseconds;
  }
  
  public long getTotalSeconds()
  {
    return totalMillis;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof DayTimeDurationAttribute)) {
      return false;
    }
    DayTimeDurationAttribute other = (DayTimeDurationAttribute)o;
    
    return (totalMillis == totalMillis) && (nanoseconds == nanoseconds) && (negative == negative);
  }
  
  public int hashCode()
  {
    int hashCode = (int)totalMillis ^ (int)(totalMillis >> 32);
    hashCode = 31 * hashCode + nanoseconds;
    if (negative) {
      hashCode = -hashCode;
    }
    return hashCode;
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append("DayTimeDurationAttribute: [\n");
    sb.append("  Negative: " + negative);
    sb.append("  Days: " + days);
    sb.append("  Hours: " + hours);
    sb.append("  Minutes: " + minutes);
    sb.append("  Seconds: " + seconds);
    sb.append("  Nanoseconds: " + nanoseconds);
    sb.append("  TotalSeconds: " + totalMillis);
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
    if (days != 0L)
    {
      buf.append(Long.toString(days));
      buf.append('D');
    }
    if ((hours != 0L) || (minutes != 0L) || (seconds != 0L) || (nanoseconds != 0)) {
      buf.append('T');
    } else if (days == 0L) {
      buf.append("0D");
    }
    if (hours != 0L)
    {
      buf.append(Long.toString(hours));
      buf.append('H');
    }
    if (minutes != 0L)
    {
      buf.append(Long.toString(minutes));
      buf.append('M');
    }
    if ((seconds != 0L) || (nanoseconds != 0))
    {
      buf.append(Long.toString(seconds));
      if (nanoseconds != 0)
      {
        buf.append('.');
        buf.append(DateAttribute.zeroPadInt(nanoseconds, 9));
      }
      buf.append('S');
    }
    encodedValue = buf.toString();
    
    return encodedValue;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.DayTimeDurationAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
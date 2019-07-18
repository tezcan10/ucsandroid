package org.wso2.balana.attr;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ProcessingException;

public class TimeAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#time";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  public static final int TZ_UNSPECIFIED = -1000000;
  private long timeGMT;
  private int nanoseconds;
  private int timeZone;
  private int defaultedTimeZone;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#time");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  private String encodedValue = null;
  
  public TimeAttribute()
  {
    this(new Date());
  }
  
  public TimeAttribute(Date time)
  {
    super(identifierURI);
    
    int currOffset = DateTimeAttribute.getDefaultTZOffset(time);
    init(time, 0, currOffset, currOffset);
  }
  
  public TimeAttribute(Date time, int nanoseconds, int timeZone, int defaultedTimeZone)
  {
    super(identifierURI);
    if ((timeZone == -1000000) && (defaultedTimeZone == -1000000)) {
      throw new ProcessingException("default timezone must be specifiedwhen a timezone is provided");
    }
    init(time, nanoseconds, timeZone, defaultedTimeZone);
  }
  
  private void init(Date date, int nanoseconds, int timeZone, int defaultedTimeZone)
  {
    if (earlyException != null) {
      throw earlyException;
    }
    Date tmpDate = (Date)date.clone();
    
    this.nanoseconds = DateTimeAttribute.combineNanos(tmpDate, nanoseconds);
    
    timeGMT = tmpDate.getTime();
    
    this.timeZone = timeZone;
    this.defaultedTimeZone = defaultedTimeZone;
    if ((timeGMT >= 86400000L) || (timeGMT < 0L))
    {
      timeGMT %= 86400000L;
      if (timeGMT < 0L) {
        timeGMT += 86400000L;
      }
    }
  }
  
  public static TimeAttribute getInstance(Node root)
    throws ParsingException, NumberFormatException, ParseException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static TimeAttribute getInstance(String value)
    throws ParsingException, NumberFormatException, ParseException
  {
    value = "1970-01-01T" + value;
    
    DateTimeAttribute dateTime = DateTimeAttribute.getInstance(value);
    
    Date dateValue = dateTime.getValue();
    int defaultedTimeZone = dateTime.getDefaultedTimeZone();
    if (dateTime.getTimeZone() == -1000000)
    {
      int newDefTimeZone = DateTimeAttribute.getDefaultTZOffset(new Date());
      dateValue = new Date(dateValue.getTime() - (newDefTimeZone - defaultedTimeZone) * 
        60000);
      defaultedTimeZone = newDefTimeZone;
    }
    return new TimeAttribute(dateValue, dateTime.getNanoseconds(), dateTime.getTimeZone(), 
      defaultedTimeZone);
  }
  
  public Date getValue()
  {
    return new Date(timeGMT);
  }
  
  public long getMilliseconds()
  {
    return timeGMT;
  }
  
  public int getNanoseconds()
  {
    return nanoseconds;
  }
  
  public int getTimeZone()
  {
    return timeZone;
  }
  
  public int getDefaultedTimeZone()
  {
    return defaultedTimeZone;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof TimeAttribute)) {
      return false;
    }
    TimeAttribute other = (TimeAttribute)o;
    
    return (timeGMT == timeGMT) && (nanoseconds == nanoseconds);
  }
  
  public int hashCode()
  {
    int hashCode = (int)(timeGMT ^ timeGMT >>> 32);
    
    hashCode = 31 * hashCode + nanoseconds;
    
    return hashCode;
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("TimeAttribute: [\n");
    
    long secsGMT = timeGMT / 1000L;
    long minsGMT = secsGMT / 60L;
    secsGMT %= 60L;
    long hoursGMT = minsGMT / 60L;
    minsGMT %= 60L;
    
    String hoursStr = hoursGMT;
    String minsStr = minsGMT;
    String secsStr = secsGMT;
    
    sb.append("  Time GMT: " + hoursStr + ":" + minsStr + ":" + secsStr);
    sb.append("  Nanoseconds: " + nanoseconds);
    sb.append("  TimeZone: " + timeZone);
    sb.append("  Defaulted TimeZone: " + defaultedTimeZone);
    sb.append("]");
    
    return sb.toString();
  }
  
  public String encode()
  {
    if (encodedValue != null) {
      return encodedValue;
    }
    StringBuffer buf = new StringBuffer(27);
    
    int millis = (int)timeGMT;
    if (timeZone == -1000000) {
      millis += defaultedTimeZone * 60000;
    } else {
      millis += timeZone * 60000;
    }
    if (millis < 0) {
      millis = (int)(millis + 86400000L);
    } else if (millis >= 86400000L) {
      millis = (int)(millis - 86400000L);
    }
    int hour = millis / 3600000;
    millis %= 3600000;
    buf.append(DateAttribute.zeroPadInt(hour, 2));
    buf.append(':');
    int minute = millis / 60000;
    millis %= 60000;
    buf.append(DateAttribute.zeroPadInt(minute, 2));
    buf.append(':');
    int second = millis / 1000;
    buf.append(DateAttribute.zeroPadInt(second, 2));
    if (nanoseconds != 0)
    {
      buf.append('.');
      buf.append(DateAttribute.zeroPadInt(nanoseconds, 9));
    }
    if (timeZone != -1000000)
    {
      int tzNoSign = timeZone;
      if (timeZone < 0)
      {
        tzNoSign = -tzNoSign;
        buf.append('-');
      }
      else
      {
        buf.append('+');
      }
      int tzHours = tzNoSign / 60;
      buf.append(DateAttribute.zeroPadInt(tzHours, 2));
      buf.append(':');
      int tzMinutes = tzNoSign % 60;
      buf.append(DateAttribute.zeroPadInt(tzMinutes, 2));
    }
    encodedValue = buf.toString();
    
    return encodedValue;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.TimeAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
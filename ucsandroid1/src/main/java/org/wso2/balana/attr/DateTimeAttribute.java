package org.wso2.balana.attr;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class DateTimeAttribute
  extends AttributeValue
{
  public static final String identifier = "http://www.w3.org/2001/XMLSchema#dateTime";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private static volatile DateFormat simpleParser;
  private static DateFormat zoneParser;
  private static volatile Calendar gmtCalendar;
  public static final int TZ_UNSPECIFIED = -1000000;
  private Date value;
  private int nanoseconds;
  private int timeZone;
  private int defaultedTimeZone;
  
  static
  {
    try
    {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#dateTime");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  private String encodedValue = null;
  
  public DateTimeAttribute()
  {
    this(new Date());
  }
  
  public DateTimeAttribute(Date dateTime)
  {
    super(identifierURI);
    
    int currOffset = getDefaultTZOffset(dateTime);
    init(dateTime, 0, currOffset, currOffset);
  }
  
  public DateTimeAttribute(Date dateTime, int nanoseconds, int timeZone, int defaultedTimeZone)
  {
    super(identifierURI);
    
    init(dateTime, nanoseconds, timeZone, defaultedTimeZone);
  }
  
  private void init(Date date, int nanoseconds, int timeZone, int defaultedTimeZone)
  {
    if (earlyException != null) {
      throw earlyException;
    }
    value = ((Date)date.clone());
    
    this.nanoseconds = combineNanos(value, nanoseconds);
    this.timeZone = timeZone;
    this.defaultedTimeZone = defaultedTimeZone;
  }
  
  public static DateTimeAttribute getInstance(Node root)
    throws ParsingException, NumberFormatException, ParseException
  {
    return getInstance(root.getFirstChild().getNodeValue());
  }
  
  public static DateTimeAttribute getInstance(String value)
    throws ParsingException, NumberFormatException, ParseException
  {
    Date dateValue = null;
    int nanoseconds = 0;
    
    initParsers();
    if (value.endsWith("Z")) {
      value = value.substring(0, value.length() - 1) + "+00:00";
    }
    int len = value.length();
    boolean hasTimeZone = (value.charAt(len - 3) == ':') && ((value.charAt(len - 6) == '-') || 
      (value.charAt(len - 6) == '+'));
    
    int dotIndex = value.indexOf('.');
    if (dotIndex != -1)
    {
      int secondsEnd = value.length();
      if (hasTimeZone) {
        secondsEnd -= 6;
      }
      String nanoString = value.substring(dotIndex + 1, secondsEnd);
      for (int i = nanoString.length() - 1; i >= 0; i--)
      {
        char c = nanoString.charAt(i);
        if ((c < '0') || (c > '9')) {
          throw new ParsingException("non-ascii digit found");
        }
      }
      if (nanoString.length() < 9)
      {
        StringBuffer buffer = new StringBuffer(nanoString);
        while (buffer.length() < 9) {
          buffer.append("0");
        }
        nanoString = buffer.toString();
      }
      if (nanoString.length() > 9) {
        nanoString = nanoString.substring(0, 9);
      }
      nanoseconds = Integer.parseInt(nanoString);
      
      value = value.substring(0, dotIndex) + value.substring(secondsEnd, value.length());
    }
    int defaultedTimeZone;
    int timeZone;
    int defaultedTimeZone;
    if (hasTimeZone)
    {
      len = value.length();
      
      Date gmtValue = strictParse(zoneParser, value.substring(0, len - 6) + "+0000");
      value = value.substring(0, len - 3) + value.substring(len - 2, len);
      dateValue = strictParse(zoneParser, value);
      int timeZone = (int)(gmtValue.getTime() - dateValue.getTime());
      timeZone /= 60000;
      defaultedTimeZone = timeZone;
    }
    else
    {
      dateValue = strictParse(simpleParser, value);
      timeZone = -1000000;
      
      Date gmtValue = strictParse(zoneParser, value + "+0000");
      defaultedTimeZone = (int)(gmtValue.getTime() - dateValue.getTime());
      defaultedTimeZone /= 60000;
    }
    DateTimeAttribute attr = new DateTimeAttribute(dateValue, nanoseconds, timeZone, 
      defaultedTimeZone);
    return attr;
  }
  
  private static Date strictParse(DateFormat parser, String str)
    throws ParseException
  {
    ParsePosition pos = new ParsePosition(0);
    Date ret;
    synchronized (parser)
    {
      ret = parser.parse(str, pos);
    }
    Date ret;
    if (pos.getIndex() != str.length()) {
      throw new ParseException("", 0);
    }
    return ret;
  }
  
  private static void initParsers()
  {
    if (simpleParser != null) {
      return;
    }
    if (earlyException != null) {
      throw earlyException;
    }
    synchronized (identifierURI)
    {
      simpleParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      simpleParser.setLenient(false);
      
      zoneParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
      zoneParser.setLenient(false);
    }
  }
  
  public Date getValue()
  {
    return (Date)value.clone();
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
    if (!(o instanceof DateTimeAttribute)) {
      return false;
    }
    DateTimeAttribute other = (DateTimeAttribute)o;
    
    return (value.equals(value)) && (nanoseconds == nanoseconds);
  }
  
  public int hashCode()
  {
    int hashCode = value.hashCode();
    hashCode = 31 * hashCode + nanoseconds;
    return hashCode;
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append("DateTimeAttribute: [\n");
    sb.append("  Date: " + value + " local time");
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
    if (timeZone == -1000000)
    {
      initParsers();
      synchronized (simpleParser)
      {
        encodedValue = simpleParser.format(value);
      }
      if (nanoseconds != 0) {
        encodedValue = (encodedValue + "." + DateAttribute.zeroPadInt(nanoseconds, 9));
      }
    }
    else
    {
      encodedValue = formatDateTimeWithTZ();
    }
    return encodedValue;
  }
  
  private String formatDateTimeWithTZ()
  {
    if (gmtCalendar == null)
    {
      TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
      
      gmtCalendar = Calendar.getInstance(gmtTimeZone, Locale.US);
    }
    StringBuffer buf = new StringBuffer(35);
    synchronized (gmtCalendar)
    {
      gmtCalendar.setTime(value);
      
      gmtCalendar.add(12, timeZone);
      
      int year = gmtCalendar.get(1);
      buf.append(DateAttribute.zeroPadInt(year, 4));
      buf.append('-');
      
      int month = gmtCalendar.get(2) + 1;
      buf.append(DateAttribute.zeroPadInt(month, 2));
      buf.append('-');
      int dom = gmtCalendar.get(5);
      buf.append(DateAttribute.zeroPadInt(dom, 2));
      buf.append('T');
      int hour = gmtCalendar.get(11);
      buf.append(DateAttribute.zeroPadInt(hour, 2));
      buf.append(':');
      int minute = gmtCalendar.get(12);
      buf.append(DateAttribute.zeroPadInt(minute, 2));
      buf.append(':');
      int second = gmtCalendar.get(13);
      buf.append(DateAttribute.zeroPadInt(second, 2));
    }
    if (nanoseconds != 0)
    {
      buf.append('.');
      buf.append(DateAttribute.zeroPadInt(nanoseconds, 9));
    }
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
    
    return buf.toString();
  }
  
  static int getDefaultTZOffset(Date date)
  {
    int offset = TimeZone.getDefault().getOffset(date.getTime());
    offset /= 60000;
    return offset;
  }
  
  static int combineNanos(Date date, int nanoseconds)
  {
    long millis = date.getTime();
    int milliCarry = (int)(millis % 1000L);
    if ((milliCarry == 0) && (nanoseconds > 0) && 
      (nanoseconds < 1000000000)) {
      return nanoseconds;
    }
    millis -= milliCarry;
    
    long nanoTemp = nanoseconds;
    nanoTemp += milliCarry * 1000000;
    
    int nanoResult = (int)(nanoTemp % 1000000000L);
    
    nanoTemp -= nanoResult;
    
    millis += nanoTemp / 1000000L;
    date.setTime(millis);
    
    return nanoResult;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.DateTimeAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
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

  public static DateTimeAttribute getInstance(String value) throws ParsingException,
          NumberFormatException, ParseException {
    Date dateValue = null;
    int nanoseconds = 0;
    int timeZone;
    int defaultedTimeZone;

    initParsers();

    // If string ends with Z, it's in GMT. Chop off the Z and
    // add +00:00 to make the time zone explicit.
    if (value.endsWith("Z"))
      value = value.substring(0, value.length() - 1) + "+00:00";

    // Figure out if the string has a time zone.
    // If string ends with +XX:XX or -XX:XX, it must have
    // a time zone or be invalid.
    int len = value.length(); // This variable is often not up-to-date
    boolean hasTimeZone = ((value.charAt(len - 3) == ':') && ((value.charAt(len - 6) == '-') || (value
            .charAt(len - 6) == '+')));

    // If string contains a period, it must have fractional
    // seconds (or be invalid). Strip them out and put the
    // value in nanoseconds.
    int dotIndex = value.indexOf('.');
    if (dotIndex != -1) {
      // Decide where fractional seconds end.
      int secondsEnd = value.length();
      if (hasTimeZone)
        secondsEnd -= 6;
      // Copy the fractional seconds out of the string.
      String nanoString = value.substring(dotIndex + 1, secondsEnd);
      // Check that all those characters are ASCII digits.
      for (int i = nanoString.length() - 1; i >= 0; i--) {
        char c = nanoString.charAt(i);
        if ((c < '0') || (c > '9'))
          throw new ParsingException("non-ascii digit found");
      }
      // If there are less than 9 digits in the fractional seconds,
      // pad with zeros on the right so it's nanoseconds.
      if (nanoString.length() < 9) {
        StringBuffer buffer = new StringBuffer(nanoString);
        while (buffer.length() < 9) {
          buffer.append("0");
        }
        nanoString = buffer.toString();
      }

      // If there are more than 9 digits in the fractional seconds,
      // drop the least significant digits.
      if (nanoString.length() > 9) {
        nanoString = nanoString.substring(0, 9);
      }
      // Parse the fractional seconds.
      nanoseconds = Integer.parseInt(nanoString);

      // Remove the fractional seconds from the string.
      value = value.substring(0, dotIndex) + value.substring(secondsEnd, value.length());
    }

    // this is the code that may trow a ParseException
    if (hasTimeZone) {
      // Strip off the purported time zone and make sure what's
      // left is a valid unzoned date and time (by parsing in GMT).
      // If so, reformat the time zone by stripping out the colon
      // and parse the revised string with the timezone parser.

      len = value.length();

      Date gmtValue = strictParse(zoneParser, value.substring(0, len - 6) + "+0000");
      value = value.substring(0, len - 3) + value.substring(len - 2, len);
      dateValue = strictParse(zoneParser, value);
      timeZone = (int) (gmtValue.getTime() - dateValue.getTime());
      timeZone = timeZone / 60000;
      defaultedTimeZone = timeZone;
    } else {
      // No funny business. This must be a simple date and time.
      dateValue = strictParse(simpleParser, value);
      timeZone = TZ_UNSPECIFIED;
      // Figure out what time zone was used.
      Date gmtValue = strictParse(zoneParser, value + "+0000");
      defaultedTimeZone = (int) (gmtValue.getTime() - dateValue.getTime());
      defaultedTimeZone = defaultedTimeZone / 60000;
    }

    // If parsing went OK, create a new DateTimeAttribute object and
    // return it.

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

  private String formatDateTimeWithTZ() {
    if (gmtCalendar == null) {
      TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

      // Locale doesn't make much difference here. We don't use
      // any of the strings in the Locale and we don't do anything
      // that depends on week count conventions. We use the US
      // locale because it's always around and it ensures that we
      // will always get a Gregorian calendar, which is necessary
      // for compliance with ISO 8501.
      gmtCalendar = Calendar.getInstance(gmtTimeZone, Locale.US);
    }

    // "YYYY-MM-DDThh:mm:ss.sssssssss+hh:mm".length() = 35
    // Length may be longer if years < -999 or > 9999
    StringBuffer buf = new StringBuffer(35);

    synchronized (gmtCalendar) {
      // Start with the proper time in GMT.
      gmtCalendar.setTime(value);
      // Bump by the timeZone, since we're going to be extracting
      // the value in GMT
      gmtCalendar.add(Calendar.MINUTE, timeZone);

      // Now, assemble the string
      int year = gmtCalendar.get(Calendar.YEAR);
      buf.append(DateAttribute.zeroPadInt(year, 4));
      buf.append('-');
      // JANUARY is 0
      int month = gmtCalendar.get(Calendar.MONTH) + 1;
      buf.append(DateAttribute.zeroPadInt(month, 2));
      buf.append('-');
      int dom = gmtCalendar.get(Calendar.DAY_OF_MONTH);
      buf.append(DateAttribute.zeroPadInt(dom, 2));
      buf.append('T');
      int hour = gmtCalendar.get(Calendar.HOUR_OF_DAY);
      buf.append(DateAttribute.zeroPadInt(hour, 2));
      buf.append(':');
      int minute = gmtCalendar.get(Calendar.MINUTE);
      buf.append(DateAttribute.zeroPadInt(minute, 2));
      buf.append(':');
      int second = gmtCalendar.get(Calendar.SECOND);
      buf.append(DateAttribute.zeroPadInt(second, 2));
    }

    if (nanoseconds != 0) {
      buf.append('.');
      buf.append(DateAttribute.zeroPadInt(nanoseconds, 9));
    }

    int tzNoSign = timeZone;
    if (timeZone < 0) {
      tzNoSign = -tzNoSign;
      buf.append('-');
    } else
      buf.append('+');
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
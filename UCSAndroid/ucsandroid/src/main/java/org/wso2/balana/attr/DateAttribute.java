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

public class DateAttribute extends AttributeValue {public static final String identifier = "http://www.w3.org/2001/XMLSchema#date";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  private static volatile DateFormat simpleParser;
  private static DateFormat zoneParser;
  private static volatile Calendar gmtCalendar;
  static final int NANOS_PER_MILLI = 1000000;
  static final int MILLIS_PER_SECOND = 1000;
  static final int SECONDS_PER_MINUTE = 60;
  static final int MINUTES_PER_HOUR = 60;
  static final int HOURS_PER_DAY = 24;
  static final int NANOS_PER_SECOND = 1000000000;
  static final int MILLIS_PER_MINUTE = 60000;
  static final int MILLIS_PER_HOUR = 3600000;
  static final long MILLIS_PER_DAY = 86400000L;
  public static final int TZ_UNSPECIFIED = -1000000;
  private Date value;
  private int timeZone;
  private int defaultedTimeZone;
  private String encodedValue;

  static {
    try {
      identifierURI = new URI("http://www.w3.org/2001/XMLSchema#date");
    } catch (Exception var1) {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(var1);
    }

  }

  public DateAttribute() {
    this(new Date());
  }

  public DateAttribute(Date date) {
    super(identifierURI);
    this.encodedValue = null;
    int currOffset = DateTimeAttribute.getDefaultTZOffset(date);
    long millis = date.getTime();
    millis += (long)(currOffset * '\uea60');
    millis -= millis % 86400000L;
    millis -= (long)(currOffset * '\uea60');
    date.setTime(millis);
    this.init(date, currOffset, currOffset);
  }

  public DateAttribute(Date date, int timeZone, int defaultedTimeZone) {
    super(identifierURI);
    this.encodedValue = null;
    this.init(date, timeZone, defaultedTimeZone);
  }

  private void init(Date date, int timeZone, int defaultedTimeZone) {
    if (earlyException != null) {
      throw earlyException;
    } else {
      this.value = (Date)date.clone();
      this.timeZone = timeZone;
      this.defaultedTimeZone = defaultedTimeZone;
    }
  }

  public static DateAttribute getInstance(Node root) throws ParseException {
    return getInstance(root.getFirstChild().getNodeValue());
  }

  public static DateAttribute getInstance(String value) throws ParseException {
    Date dateValue = null;
    if (simpleParser == null) {
      initParsers();
    }

    int timeZone;
    int defaultedTimeZone;
    if (value.endsWith("Z")) {
      value = value.substring(0, value.length() - 1) + "+0000";
      dateValue = strictParse(zoneParser, value);
      timeZone = 0;
      defaultedTimeZone = 0;
    } else {
      int len = value.length();
      Date gmtValue;
      if (len > 6 && value.charAt(len - 3) == ':') {
        gmtValue = strictParse(zoneParser, value.substring(0, len - 6) + "+0000");
        value = value.substring(0, len - 3) + value.substring(len - 2, len);
        dateValue = strictParse(zoneParser, value);
        timeZone = (int)(gmtValue.getTime() - dateValue.getTime());
        timeZone /= 60000;
        defaultedTimeZone = timeZone;
      } else {
        dateValue = strictParse(simpleParser, value);
        timeZone = -1000000;
        gmtValue = strictParse(zoneParser, value + "+0000");
        defaultedTimeZone = (int)(gmtValue.getTime() - dateValue.getTime());
        defaultedTimeZone /= 60000;
      }
    }

    DateAttribute attr = new DateAttribute(dateValue, timeZone, defaultedTimeZone);
    return attr;
  }

  private static Date strictParse(DateFormat parser, String str) throws ParseException {
    ParsePosition pos = new ParsePosition(0);
    Date ret;
    synchronized(parser) {
      ret = parser.parse(str, pos);
    }

    if (pos.getIndex() != str.length()) {
      throw new ParseException("", 0);
    } else {
      return ret;
    }
  }

  private static void initParsers() {
    if (simpleParser == null) {
      if (earlyException != null) {
        throw earlyException;
      } else {
        synchronized(identifierURI) {
          simpleParser = new SimpleDateFormat("yyyy-MM-dd");
          simpleParser.setLenient(false);
          zoneParser = new SimpleDateFormat("yyyy-MM-ddZ");
          zoneParser.setLenient(false);
        }
      }
    }
  }

  public Date getValue() {
    return (Date)this.value.clone();
  }

  public int getTimeZone() {
    return this.timeZone;
  }

  public int getDefaultedTimeZone() {
    return this.defaultedTimeZone;
  }

  public boolean equals(Object o) {
    if (!(o instanceof DateAttribute)) {
      return false;
    } else {
      DateAttribute other = (DateAttribute)o;
      return this.value.equals(other.value);
    }
  }

  public int hashCode() {
    return this.value.hashCode();
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("DateAttribute: [\n");
    sb.append("  Date: " + this.value + " local time");
    sb.append("  TimeZone: " + this.timeZone);
    sb.append("  Defaulted TimeZone: " + this.defaultedTimeZone);
    sb.append("]");
    return sb.toString();
  }

  public String encode() {
    if (this.encodedValue != null) {
      return this.encodedValue;
    } else {
      if (this.timeZone == -1000000) {
        initParsers();
        synchronized(simpleParser) {
          this.encodedValue = simpleParser.format(this.value);
        }
      } else {
        this.encodedValue = this.formatDateWithTZ();
      }

      return this.encodedValue;
    }
  }

  private String formatDateWithTZ() {
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

    // "YYYY-MM-DD+hh:mm".length() = 16
    // Length may be longer if years < -999 or > 9999
    StringBuffer buf = new StringBuffer(16);

    synchronized (gmtCalendar) {
      // Start with the GMT instant when the date started in the
      // specified time zone (would be 7:00 PM the preceding day
      // if the specified time zone was +0500).
      gmtCalendar.setTime(value);
      // Bump by the timeZone (so we get the right date/time that
      // that we want to format)
      gmtCalendar.add(Calendar.MINUTE, timeZone);

      // Now, assemble the string
      int year = gmtCalendar.get(Calendar.YEAR);
      buf.append(zeroPadInt(year, 4));
      buf.append('-');
      // JANUARY is 0
      int month = gmtCalendar.get(Calendar.MONTH) + 1;
      buf.append(zeroPadInt(month, 2));
      buf.append('-');
      int dom = gmtCalendar.get(Calendar.DAY_OF_MONTH);
      buf.append(zeroPadInt(dom, 2));
    }

    int tzNoSign = timeZone;
    if (timeZone < 0) {
      tzNoSign = -tzNoSign;
      buf.append('-');
    } else
      buf.append('+');
    int tzHours = tzNoSign / 60;
    buf.append(zeroPadInt(tzHours, 2));
    buf.append(':');
    int tzMinutes = tzNoSign % 60;
    buf.append(zeroPadInt(tzMinutes, 2));

    return buf.toString();
  }

  static String zeroPadIntString(String unpadded, int minDigits) {
    int len = unpadded.length();
    char sign = unpadded.charAt(0);
    if (sign != '-' && sign != '+') {
      sign = 0;
    }

    int minChars = minDigits;
    if (sign != 0) {
      minChars = minDigits + 1;
    }

    if (len >= minChars) {
      return unpadded;
    } else {
      StringBuffer buf = new StringBuffer();
      if (sign != 0) {
        buf.append(sign);
      }

      int var6 = minChars - len;

      while(var6-- != 0) {
        buf.append('0');
      }

      if (sign != 0) {
        buf.append(unpadded.substring(1, len));
      } else {
        buf.append(unpadded);
      }

      return buf.toString();
    }
  }

  static String zeroPadInt(int intValue, int minDigits) {
    return zeroPadIntString(Integer.toString(intValue), minDigits);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.DateAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.DayTimeDurationAttribute;
import org.wso2.balana.attr.YearMonthDurationAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class DateMathFunction
  extends FunctionBase
{
  public static final String NAME_DATETIME_ADD_DAYTIMEDURATION = "urn:oasis:names:tc:xacml:1.0:function:dateTime-add-dayTimeDuration";
  public static final String NAME_DATETIME_SUBTRACT_DAYTIMEDURATION = "urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-dayTimeDuration";
  public static final String NAME_DATETIME_ADD_YEARMONTHDURATION = "urn:oasis:names:tc:xacml:1.0:function:dateTime-add-yearMonthDuration";
  public static final String NAME_DATETIME_SUBTRACT_YEARMONTHDURATION = "urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-yearMonthDuration";
  public static final String NAME_DATE_ADD_YEARMONTHDURATION = "urn:oasis:names:tc:xacml:1.0:function:date-add-yearMonthDuration";
  public static final String NAME_DATE_SUBTRACT_YEARMONTHDURATION = "urn:oasis:names:tc:xacml:1.0:function:date-subtract-yearMonthDuration";
  private static final int ID_DATETIME_ADD_DAYTIMEDURATION = 0;
  private static final int ID_DATETIME_SUBTRACT_DAYTIMEDURATION = 1;
  private static final int ID_DATETIME_ADD_YEARMONTHDURATION = 2;
  private static final int ID_DATETIME_SUBTRACT_YEARMONTHDURATION = 3;
  private static final int ID_DATE_ADD_YEARMONTHDURATION = 4;
  private static final int ID_DATE_SUBTRACT_YEARMONTHDURATION = 5;
  private static final String[] dateTimeDayTimeDurationArgTypes = { "http://www.w3.org/2001/XMLSchema#dateTime", 
    "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration" };
  private static final String[] dateTimeYearMonthDurationArgTypes = {
    "http://www.w3.org/2001/XMLSchema#dateTime", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration" };
  private static final String[] dateYearMonthDurationArgTypes = { "http://www.w3.org/2001/XMLSchema#date", 
    "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration" };
  private static final boolean[] bagParams = new boolean[2];
  private static HashMap<String, Integer> idMap = new HashMap();
  private static HashMap<String, String[]> typeMap;
  
  static
  {
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-add-dayTimeDuration", 
      Integer.valueOf(0));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-dayTimeDuration", 
      Integer.valueOf(1));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-add-yearMonthDuration", 
      Integer.valueOf(2));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-yearMonthDuration", 
      Integer.valueOf(3));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:date-add-yearMonthDuration", Integer.valueOf(4));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:date-subtract-yearMonthDuration", 
      Integer.valueOf(5));
    
    typeMap = new HashMap();
    
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-add-dayTimeDuration", dateTimeDayTimeDurationArgTypes);
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-dayTimeDuration", dateTimeDayTimeDurationArgTypes);
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-add-yearMonthDuration", dateTimeYearMonthDurationArgTypes);
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-subtract-yearMonthDuration", dateTimeYearMonthDurationArgTypes);
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-add-yearMonthDuration", dateYearMonthDurationArgTypes);
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-subtract-yearMonthDuration", dateYearMonthDurationArgTypes);
  }
  
  public DateMathFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentTypes(functionName), bagParams, getReturnType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    Integer i = (Integer)idMap.get(functionName);
    if (i == null) {
      throw new IllegalArgumentException("unknown datemath function " + functionName);
    }
    return i.intValue();
  }
  
  private static String[] getArgumentTypes(String functionName)
  {
    return (String[])typeMap.get(functionName);
  }
  
  private static String getReturnType(String functionName)
  {
    if ((functionName.equals("urn:oasis:names:tc:xacml:1.0:function:date-add-yearMonthDuration")) || 
      (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:date-subtract-yearMonthDuration"))) {
      return "http://www.w3.org/2001/XMLSchema#date";
    }
    return "http://www.w3.org/2001/XMLSchema#dateTime";
  }
  
  public static Set getSupportedIdentifiers()
  {
    return Collections.unmodifiableSet(idMap.keySet());
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    AttributeValue attrResult = null;
    switch (getFunctionId())
    {
    case 0: 
    case 1: 
      DateTimeAttribute dateTime = (DateTimeAttribute)argValues[0];
      DayTimeDurationAttribute duration = (DayTimeDurationAttribute)argValues[1];
      
      int sign = 1;
      if (getFunctionId() == 1) {
        sign = -sign;
      }
      if (duration.isNegative()) {
        sign = -sign;
      }
      long millis = sign * duration.getTotalSeconds();
      long nanoseconds = dateTime.getNanoseconds();
      nanoseconds += sign * duration.getNanoseconds();
      if (nanoseconds >= 1000000000L)
      {
        nanoseconds -= 1000000000L;
        millis += 1000L;
      }
      if (nanoseconds < 0L)
      {
        nanoseconds += 1000000000L;
        millis -= 1000L;
      }
      millis += dateTime.getValue().getTime();
      
      attrResult = new DateTimeAttribute(new Date(millis), (int)nanoseconds, 
        dateTime.getTimeZone(), dateTime.getDefaultedTimeZone());
      
      break;
    case 2: 
    case 3: 
      DateTimeAttribute dateTime = (DateTimeAttribute)argValues[0];
      YearMonthDurationAttribute duration = (YearMonthDurationAttribute)argValues[1];
      
      int sign = 1;
      if (getFunctionId() == 3) {
        sign = -sign;
      }
      if (duration.isNegative()) {
        sign = -sign;
      }
      Calendar cal = new GregorianCalendar();
      cal.setTime(dateTime.getValue());
      long years = sign * duration.getYears();
      long months = sign * duration.getMonths();
      if ((years > 2147483647L) || (years < -2147483648L)) {
        return makeProcessingError("years too large");
      }
      if ((months > 2147483647L) || (months < -2147483648L)) {
        return makeProcessingError("months too large");
      }
      cal.add(1, (int)years);
      cal.add(2, (int)months);
      
      attrResult = new DateTimeAttribute(cal.getTime(), dateTime.getNanoseconds(), 
        dateTime.getTimeZone(), dateTime.getDefaultedTimeZone());
      
      break;
    case 4: 
    case 5: 
      DateAttribute date = (DateAttribute)argValues[0];
      YearMonthDurationAttribute duration = (YearMonthDurationAttribute)argValues[1];
      
      int sign = 1;
      if (getFunctionId() == 5) {
        sign = -sign;
      }
      if (duration.isNegative()) {
        sign = -sign;
      }
      Calendar cal = new GregorianCalendar();
      cal.setTime(date.getValue());
      long years = sign * duration.getYears();
      long months = sign * duration.getMonths();
      if ((years > 2147483647L) || (years < -2147483648L)) {
        return makeProcessingError("years too large");
      }
      if ((months > 2147483647L) || (months < -2147483648L)) {
        return makeProcessingError("months too large");
      }
      cal.add(1, (int)years);
      cal.add(2, (int)months);
      
      attrResult = new DateAttribute(cal.getTime(), date.getTimeZone(), 
        date.getDefaultedTimeZone());
    }
    return new EvaluationResult(attrResult);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.DateMathFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
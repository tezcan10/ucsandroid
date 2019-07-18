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
  /**
   * Standard identifier for the dateTime-add-dayTimeDuration function.
   */
  public static final String NAME_DATETIME_ADD_DAYTIMEDURATION = FUNCTION_NS
          + "dateTime-add-dayTimeDuration";

  /**
   * Standard identifier for the dateTime-subtract-dayTimeDuration function.
   */
  public static final String NAME_DATETIME_SUBTRACT_DAYTIMEDURATION = FUNCTION_NS
          + "dateTime-subtract-dayTimeDuration";

  /**
   * Standard identifier for the dateTime-add-yearMonthDuration function.
   */
  public static final String NAME_DATETIME_ADD_YEARMONTHDURATION = FUNCTION_NS
          + "dateTime-add-yearMonthDuration";

  /**
   * Standard identifier for the dateTime-subtract-yearMonthDuration function.
   */
  public static final String NAME_DATETIME_SUBTRACT_YEARMONTHDURATION = FUNCTION_NS
          + "dateTime-subtract-yearMonthDuration";

  /**
   * Standard identifier for the date-add-yearMonthDuration function.
   */
  public static final String NAME_DATE_ADD_YEARMONTHDURATION = FUNCTION_NS
          + "date-add-yearMonthDuration";

  /**
   * Standard identifier for the date-subtract-yearMonthDuration function.
   */
  public static final String NAME_DATE_SUBTRACT_YEARMONTHDURATION = FUNCTION_NS
          + "date-subtract-yearMonthDuration";

  // private identifiers for the supported functions
  private static final int ID_DATETIME_ADD_DAYTIMEDURATION = 0;
  private static final int ID_DATETIME_SUBTRACT_DAYTIMEDURATION = 1;
  private static final int ID_DATETIME_ADD_YEARMONTHDURATION = 2;
  private static final int ID_DATETIME_SUBTRACT_YEARMONTHDURATION = 3;
  private static final int ID_DATE_ADD_YEARMONTHDURATION = 4;
  private static final int ID_DATE_SUBTRACT_YEARMONTHDURATION = 5;

  // Argument types
  private static final String dateTimeDayTimeDurationArgTypes[] = { DateTimeAttribute.identifier,
          DayTimeDurationAttribute.identifier };
  private static final String dateTimeYearMonthDurationArgTypes[] = {
          DateTimeAttribute.identifier, YearMonthDurationAttribute.identifier };
  private static final String dateYearMonthDurationArgTypes[] = { DateAttribute.identifier,
          YearMonthDurationAttribute.identifier };

  // nothing here uses a bag
  private static final boolean bagParams[] = { false, false };

  // mapping from name to provide identifiers and argument types
  private static HashMap<String,Integer> idMap;
  private static HashMap<String,String[]> typeMap;

  /**
   * Static initializer to setup the id and type maps
   */
  static {
    idMap = new HashMap<String,Integer>();

    idMap.put(NAME_DATETIME_ADD_DAYTIMEDURATION,
            Integer.valueOf(ID_DATETIME_ADD_DAYTIMEDURATION));
    idMap.put(NAME_DATETIME_SUBTRACT_DAYTIMEDURATION,
            Integer.valueOf(ID_DATETIME_SUBTRACT_DAYTIMEDURATION));
    idMap.put(NAME_DATETIME_ADD_YEARMONTHDURATION,
            Integer.valueOf(ID_DATETIME_ADD_YEARMONTHDURATION));
    idMap.put(NAME_DATETIME_SUBTRACT_YEARMONTHDURATION,
            Integer.valueOf(ID_DATETIME_SUBTRACT_YEARMONTHDURATION));
    idMap.put(NAME_DATE_ADD_YEARMONTHDURATION, Integer.valueOf(ID_DATE_ADD_YEARMONTHDURATION));
    idMap.put(NAME_DATE_SUBTRACT_YEARMONTHDURATION,
            Integer.valueOf(ID_DATE_SUBTRACT_YEARMONTHDURATION));

    typeMap = new HashMap<String,String[]>();

    typeMap.put(NAME_DATETIME_ADD_DAYTIMEDURATION, dateTimeDayTimeDurationArgTypes);
    typeMap.put(NAME_DATETIME_SUBTRACT_DAYTIMEDURATION, dateTimeDayTimeDurationArgTypes);
    typeMap.put(NAME_DATETIME_ADD_YEARMONTHDURATION, dateTimeYearMonthDurationArgTypes);
    typeMap.put(NAME_DATETIME_SUBTRACT_YEARMONTHDURATION, dateTimeYearMonthDurationArgTypes);
    typeMap.put(NAME_DATE_ADD_YEARMONTHDURATION, dateYearMonthDurationArgTypes);
    typeMap.put(NAME_DATE_SUBTRACT_YEARMONTHDURATION, dateYearMonthDurationArgTypes);
  };

  /**
   * Creates a new <code>DateMathFunction</code> object.
   *
   * @param functionName the standard XACML name of the function to be handled by this object,
   *            including the full namespace
   *
   * @throws IllegalArgumentException if the function is unknown
   */
  public DateMathFunction(String functionName) {
    super(functionName, getId(functionName), getArgumentTypes(functionName), bagParams,
            getReturnType(functionName), false);
  }

  /**
   * Private helper that returns the internal identifier used for the given standard function.
   */
  private static int getId(String functionName) {
    Integer i = (Integer) (idMap.get(functionName));

    if (i == null)
      throw new IllegalArgumentException("unknown datemath function " + functionName);

    return i.intValue();
  }

  /**
   * Private helper that returns the types used for the given standard function. Note that this
   * doesn't check on the return value since the method always is called after getId, so we assume
   * that the function is present.
   */
  private static String[] getArgumentTypes(String functionName) {
    return (String[]) (typeMap.get(functionName));
  }

  /**
   * Private helper that returns the return type for the given standard function. Note that this
   * doesn't check on the return value since the method always is called after getId, so we assume
   * that the function is present.
   */
  private static String getReturnType(String functionName) {
    if (functionName.equals(NAME_DATE_ADD_YEARMONTHDURATION)
            || functionName.equals(NAME_DATE_SUBTRACT_YEARMONTHDURATION))
      return DateAttribute.identifier;
    else
      return DateTimeAttribute.identifier;
  }

  /**
   * Returns a <code>Set</code> containing all the function identifiers supported by this class.
   *
   * @return a <code>Set</code> of <code>String</code>s
   */
  public static Set getSupportedIdentifiers() {
    return Collections.unmodifiableSet(idMap.keySet());
  }

  /**
   * Evaluate the function, using the specified parameters.
   *
   * @param inputs a <code>List</code> of <code>Evaluatable</code> objects representing the
   *            arguments passed to the function
   * @param context an <code>EvaluationCtx</code> so that the <code>Evaluatable</code> objects can
   *            be evaluated
   * @return an <code>EvaluationResult</code> representing the function's result
   */
  public EvaluationResult evaluate(List inputs, EvaluationCtx context) {

    // Evaluate the arguments
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null)
      return result;

    // Now that we have real values, perform the date math operation.
    AttributeValue attrResult = null;

    switch (getFunctionId()) {
      // These two functions are basically the same except for sign.
      // And they both need to deal with sign anyway, so they share
      // their code.
      case ID_DATETIME_ADD_DAYTIMEDURATION:
      case ID_DATETIME_SUBTRACT_DAYTIMEDURATION: {
        DateTimeAttribute dateTime = (DateTimeAttribute) argValues[0];
        DayTimeDurationAttribute duration = (DayTimeDurationAttribute) argValues[1];

        // Decide what sign goes with duration
        int sign = 1;
        if (getFunctionId() == ID_DATETIME_SUBTRACT_DAYTIMEDURATION)
          sign = -sign;
        if (duration.isNegative())
          sign = -sign;
        long millis = sign * duration.getTotalSeconds();
        long nanoseconds = dateTime.getNanoseconds();
        nanoseconds = nanoseconds + (sign * duration.getNanoseconds());
        if (nanoseconds >= 1000000000) {
          nanoseconds -= 1000000000;
          millis += 1000;
        }
        if (nanoseconds < 0) {
          nanoseconds += 1000000000;
          millis -= 1000;
        }
        millis = millis + dateTime.getValue().getTime();

        attrResult = new DateTimeAttribute(new Date(millis), (int) nanoseconds,
                dateTime.getTimeZone(), dateTime.getDefaultedTimeZone());

        break;
      }
      case ID_DATETIME_ADD_YEARMONTHDURATION:
      case ID_DATETIME_SUBTRACT_YEARMONTHDURATION: {
        DateTimeAttribute dateTime = (DateTimeAttribute) argValues[0];
        YearMonthDurationAttribute duration = (YearMonthDurationAttribute) argValues[1];

        // Decide what sign goes with duration
        int sign = 1;
        if (getFunctionId() == ID_DATETIME_SUBTRACT_YEARMONTHDURATION)
          sign = -sign;
        if (duration.isNegative())
          sign = -sign;

        // Add (or subtract) the years and months.
        Calendar cal = new GregorianCalendar();
        cal.setTime(dateTime.getValue());
        long years = sign * duration.getYears();
        long months = sign * duration.getMonths();
        if ((years > Integer.MAX_VALUE) || (years < Integer.MIN_VALUE))
          return makeProcessingError("years too large");
        if ((months > Integer.MAX_VALUE) || (months < Integer.MIN_VALUE))
          return makeProcessingError("months too large");

        cal.add(Calendar.YEAR, (int) years);
        cal.add(Calendar.MONTH, (int) months);

        attrResult = new DateTimeAttribute(cal.getTime(), dateTime.getNanoseconds(),
                dateTime.getTimeZone(), dateTime.getDefaultedTimeZone());

        break;
      }
      case ID_DATE_ADD_YEARMONTHDURATION:
      case ID_DATE_SUBTRACT_YEARMONTHDURATION: {
        DateAttribute date = (DateAttribute) argValues[0];
        YearMonthDurationAttribute duration = (YearMonthDurationAttribute) argValues[1];

        // Decide what sign goes with duration
        int sign = 1;
        if (getFunctionId() == ID_DATE_SUBTRACT_YEARMONTHDURATION)
          sign = -sign;
        if (duration.isNegative())
          sign = -sign;

        // Add (or subtract) the years and months.
        Calendar cal = new GregorianCalendar();
        cal.setTime(date.getValue());
        long years = sign * duration.getYears();
        long months = sign * duration.getMonths();
        if ((years > Integer.MAX_VALUE) || (years < Integer.MIN_VALUE))
          return makeProcessingError("years too large");
        if ((months > Integer.MAX_VALUE) || (months < Integer.MIN_VALUE))
          return makeProcessingError("months too large");

        cal.add(Calendar.YEAR, (int) years);
        cal.add(Calendar.MONTH, (int) months);

        attrResult = new DateAttribute(cal.getTime(), date.getTimeZone(),
                date.getDefaultedTimeZone());

        break;
      }
    }

    return new EvaluationResult(attrResult);
  }
}


/* Location:
 * Qualified Name:     org.wso2.balana.cond.DateMathFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
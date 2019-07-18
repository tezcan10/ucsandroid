package org.wso2.balana.cond;

import java.util.List;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class TimeInRangeFunction
  extends FunctionBase
{
  public static final String NAME = "urn:oasis:names:tc:xacml:2.0:function:time-in-range";
  public static final long MILLIS_PER_MINUTE = 60000L;
  public static final long MILLIS_PER_DAY = 86400000L;
  
  public TimeInRangeFunction()
  {
    super("urn:oasis:names:tc:xacml:2.0:function:time-in-range", 0, "http://www.w3.org/2001/XMLSchema#time", false, 3, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    TimeAttribute attr = (TimeAttribute)argValues[0];
    long middleTime = attr.getMilliseconds();
    long minTime = resolveTime(attr, (TimeAttribute)argValues[1]);
    long maxTime = resolveTime(attr, (TimeAttribute)argValues[2]);
    if (minTime == maxTime) {
      return EvaluationResult.getInstance(middleTime == minTime);
    }
    long shiftSpan;
    if (minTime < maxTime) {
      shiftSpan = -minTime;
    } else {
      shiftSpan = 86400000L - minTime;
    }
    maxTime += shiftSpan;
    middleTime = handleWrap(middleTime + shiftSpan);
    
    return EvaluationResult.getInstance((middleTime >= 0L) && (middleTime <= maxTime));
  }
  
  private long resolveTime(TimeAttribute middleTime, TimeAttribute otherTime)
  {
    long time = otherTime.getMilliseconds();
    int tz = otherTime.getTimeZone();
    if (tz == -1000000)
    {
      int middleTz = middleTime.getTimeZone();
      
      tz = otherTime.getDefaultedTimeZone();
      if (middleTz == -1000000) {
        middleTz = middleTime.getDefaultedTimeZone();
      }
      if (middleTz != tz)
      {
        time -= (middleTz - tz) * 60000L;
        time = handleWrap(time);
      }
    }
    return time;
  }
  
  private long handleWrap(long time)
  {
    if (time < 0L) {
      return time + 86400000L;
    }
    if (time > 86400000L) {
      return time - 86400000L;
    }
    return time;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.TimeInRangeFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
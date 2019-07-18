package org.wso2.balana.cond;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class ComparisonFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:integer-greater-than";
  public static final String NAME_INTEGER_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:integer-greater-than-or-equal";
  public static final String NAME_INTEGER_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:integer-less-than";
  public static final String NAME_INTEGER_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:integer-less-than-or-equal";
  public static final String NAME_DOUBLE_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:double-greater-than";
  public static final String NAME_DOUBLE_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:double-greater-than-or-equal";
  public static final String NAME_DOUBLE_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:double-less-than";
  public static final String NAME_DOUBLE_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:double-less-than-or-equal";
  public static final String NAME_STRING_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:string-greater-than";
  public static final String NAME_STRING_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal";
  public static final String NAME_STRING_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:string-less-than";
  public static final String NAME_STRING_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal";
  public static final String NAME_TIME_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:time-greater-than";
  public static final String NAME_TIME_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal";
  public static final String NAME_TIME_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:time-less-than";
  public static final String NAME_TIME_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal";
  public static final String NAME_DATETIME_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than";
  public static final String NAME_DATETIME_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal";
  public static final String NAME_DATETIME_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than";
  public static final String NAME_DATETIME_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal";
  public static final String NAME_DATE_GREATER_THAN = "urn:oasis:names:tc:xacml:1.0:function:date-greater-than";
  public static final String NAME_DATE_GREATER_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal";
  public static final String NAME_DATE_LESS_THAN = "urn:oasis:names:tc:xacml:1.0:function:date-less-than";
  public static final String NAME_DATE_LESS_THAN_OR_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal";
  private static final int ID_INTEGER_GREATER_THAN = 0;
  private static final int ID_INTEGER_GREATER_THAN_OR_EQUAL = 1;
  private static final int ID_INTEGER_LESS_THAN = 2;
  private static final int ID_INTEGER_LESS_THAN_OR_EQUAL = 3;
  private static final int ID_DOUBLE_GREATER_THAN = 4;
  private static final int ID_DOUBLE_GREATER_THAN_OR_EQUAL = 5;
  private static final int ID_DOUBLE_LESS_THAN = 6;
  private static final int ID_DOUBLE_LESS_THAN_OR_EQUAL = 7;
  private static final int ID_STRING_GREATER_THAN = 8;
  private static final int ID_STRING_GREATER_THAN_OR_EQUAL = 9;
  private static final int ID_STRING_LESS_THAN = 10;
  private static final int ID_STRING_LESS_THAN_OR_EQUAL = 11;
  private static final int ID_TIME_GREATER_THAN = 12;
  private static final int ID_TIME_GREATER_THAN_OR_EQUAL = 13;
  private static final int ID_TIME_LESS_THAN = 14;
  private static final int ID_TIME_LESS_THAN_OR_EQUAL = 15;
  private static final int ID_DATE_GREATER_THAN = 16;
  private static final int ID_DATE_GREATER_THAN_OR_EQUAL = 17;
  private static final int ID_DATE_LESS_THAN = 18;
  private static final int ID_DATE_LESS_THAN_OR_EQUAL = 19;
  private static final int ID_DATETIME_GREATER_THAN = 20;
  private static final int ID_DATETIME_GREATER_THAN_OR_EQUAL = 21;
  private static final int ID_DATETIME_LESS_THAN = 22;
  private static final int ID_DATETIME_LESS_THAN_OR_EQUAL = 23;
  private static HashMap<String, Integer> idMap = new HashMap();
  private static HashMap<String, String> typeMap;
  
  static
  {
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than", Integer.valueOf(0));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than-or-equal", 
      Integer.valueOf(1));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-less-than", Integer.valueOf(2));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-less-than-or-equal", Integer.valueOf(3));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:double-greater-than", Integer.valueOf(4));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:double-greater-than-or-equal", 
      Integer.valueOf(5));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:double-less-than", Integer.valueOf(6));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:double-less-than-or-equal", Integer.valueOf(7));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:string-greater-than", Integer.valueOf(8));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal", 
      Integer.valueOf(9));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:string-less-than", Integer.valueOf(10));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal", Integer.valueOf(11));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:time-greater-than", Integer.valueOf(12));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal", Integer.valueOf(13));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:time-less-than", Integer.valueOf(14));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal", Integer.valueOf(15));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:date-greater-than", Integer.valueOf(16));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal", Integer.valueOf(17));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:date-less-than", Integer.valueOf(18));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal", Integer.valueOf(19));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than", Integer.valueOf(20));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal", 
      Integer.valueOf(21));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than", Integer.valueOf(22));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal", Integer.valueOf(23));
    
    typeMap = new HashMap();
    
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than", "http://www.w3.org/2001/XMLSchema#integer");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than-or-equal", "http://www.w3.org/2001/XMLSchema#integer");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-less-than", "http://www.w3.org/2001/XMLSchema#integer");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-less-than-or-equal", "http://www.w3.org/2001/XMLSchema#integer");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:double-greater-than", "http://www.w3.org/2001/XMLSchema#double");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:double-greater-than-or-equal", "http://www.w3.org/2001/XMLSchema#double");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:double-less-than", "http://www.w3.org/2001/XMLSchema#double");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:double-less-than-or-equal", "http://www.w3.org/2001/XMLSchema#double");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:string-greater-than", "http://www.w3.org/2001/XMLSchema#string");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal", "http://www.w3.org/2001/XMLSchema#string");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:string-less-than", "http://www.w3.org/2001/XMLSchema#string");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal", "http://www.w3.org/2001/XMLSchema#string");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:time-greater-than", "http://www.w3.org/2001/XMLSchema#time");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal", "http://www.w3.org/2001/XMLSchema#time");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:time-less-than", "http://www.w3.org/2001/XMLSchema#time");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal", "http://www.w3.org/2001/XMLSchema#time");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than", "http://www.w3.org/2001/XMLSchema#dateTime");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal", "http://www.w3.org/2001/XMLSchema#dateTime");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than", "http://www.w3.org/2001/XMLSchema#dateTime");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal", "http://www.w3.org/2001/XMLSchema#dateTime");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-greater-than", "http://www.w3.org/2001/XMLSchema#date");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal", "http://www.w3.org/2001/XMLSchema#date");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-less-than", "http://www.w3.org/2001/XMLSchema#date");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal", "http://www.w3.org/2001/XMLSchema#date");
  }
  
  public ComparisonFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 2, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  private static int getId(String functionName)
  {
    Integer i = (Integer)idMap.get(functionName);
    if (i == null) {
      throw new IllegalArgumentException("unknown comparison function " + functionName);
    }
    return i.intValue();
  }
  
  private static String getArgumentType(String functionName)
  {
    return (String)typeMap.get(functionName);
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
    boolean boolResult = false;
    switch (getFunctionId())
    {
    case 0: 
      long arg0 = ((IntegerAttribute)argValues[0]).getValue();
      long arg1 = ((IntegerAttribute)argValues[1]).getValue();
      
      boolResult = arg0 > arg1;
      
      break;
    case 1: 
      long arg0 = ((IntegerAttribute)argValues[0]).getValue();
      long arg1 = ((IntegerAttribute)argValues[1]).getValue();
      
      boolResult = arg0 >= arg1;
      
      break;
    case 2: 
      long arg0 = ((IntegerAttribute)argValues[0]).getValue();
      long arg1 = ((IntegerAttribute)argValues[1]).getValue();
      
      boolResult = arg0 < arg1;
      
      break;
    case 3: 
      long arg0 = ((IntegerAttribute)argValues[0]).getValue();
      long arg1 = ((IntegerAttribute)argValues[1]).getValue();
      
      boolResult = arg0 <= arg1;
      
      break;
    case 4: 
      double arg0 = ((DoubleAttribute)argValues[0]).getValue();
      double arg1 = ((DoubleAttribute)argValues[1]).getValue();
      
      boolResult = doubleCompare(arg0, arg1) > 0;
      
      break;
    case 5: 
      double arg0 = ((DoubleAttribute)argValues[0]).getValue();
      double arg1 = ((DoubleAttribute)argValues[1]).getValue();
      
      boolResult = doubleCompare(arg0, arg1) >= 0;
      
      break;
    case 6: 
      double arg0 = ((DoubleAttribute)argValues[0]).getValue();
      double arg1 = ((DoubleAttribute)argValues[1]).getValue();
      
      boolResult = doubleCompare(arg0, arg1) < 0;
      
      break;
    case 7: 
      double arg0 = ((DoubleAttribute)argValues[0]).getValue();
      double arg1 = ((DoubleAttribute)argValues[1]).getValue();
      
      boolResult = doubleCompare(arg0, arg1) <= 0;
      
      break;
    case 8: 
      String arg0 = ((StringAttribute)argValues[0]).getValue();
      String arg1 = ((StringAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) > 0;
      
      break;
    case 9: 
      String arg0 = ((StringAttribute)argValues[0]).getValue();
      String arg1 = ((StringAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) >= 0;
      
      break;
    case 10: 
      String arg0 = ((StringAttribute)argValues[0]).getValue();
      String arg1 = ((StringAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) < 0;
      
      break;
    case 11: 
      String arg0 = ((StringAttribute)argValues[0]).getValue();
      String arg1 = ((StringAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) <= 0;
      
      break;
    case 12: 
      TimeAttribute arg0 = (TimeAttribute)argValues[0];
      TimeAttribute arg1 = (TimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) > 0;
      
      break;
    case 13: 
      TimeAttribute arg0 = (TimeAttribute)argValues[0];
      TimeAttribute arg1 = (TimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) >= 0;
      
      break;
    case 14: 
      TimeAttribute arg0 = (TimeAttribute)argValues[0];
      TimeAttribute arg1 = (TimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) < 0;
      
      break;
    case 15: 
      TimeAttribute arg0 = (TimeAttribute)argValues[0];
      TimeAttribute arg1 = (TimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) <= 0;
      
      break;
    case 20: 
      DateTimeAttribute arg0 = (DateTimeAttribute)argValues[0];
      DateTimeAttribute arg1 = (DateTimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) > 0;
      
      break;
    case 21: 
      DateTimeAttribute arg0 = (DateTimeAttribute)argValues[0];
      DateTimeAttribute arg1 = (DateTimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) >= 0;
      
      break;
    case 22: 
      DateTimeAttribute arg0 = (DateTimeAttribute)argValues[0];
      DateTimeAttribute arg1 = (DateTimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) < 0;
      
      break;
    case 23: 
      DateTimeAttribute arg0 = (DateTimeAttribute)argValues[0];
      DateTimeAttribute arg1 = (DateTimeAttribute)argValues[1];
      
      boolResult = dateCompare(arg0.getValue(), arg0.getNanoseconds(), arg1.getValue(), 
        arg1.getNanoseconds()) <= 0;
      
      break;
    case 16: 
      Date arg0 = ((DateAttribute)argValues[0]).getValue();
      Date arg1 = ((DateAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) > 0;
      
      break;
    case 17: 
      Date arg0 = ((DateAttribute)argValues[0]).getValue();
      Date arg1 = ((DateAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) >= 0;
      
      break;
    case 18: 
      Date arg0 = ((DateAttribute)argValues[0]).getValue();
      Date arg1 = ((DateAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) < 0;
      
      break;
    case 19: 
      Date arg0 = ((DateAttribute)argValues[0]).getValue();
      Date arg1 = ((DateAttribute)argValues[1]).getValue();
      
      boolResult = arg0.compareTo(arg1) <= 0;
    }
    return EvaluationResult.getInstance(boolResult);
  }
  
  private int doubleCompare(double d1, double d2)
  {
    if (d1 == d2)
    {
      if (d1 != 0.0D) {
        return 0;
      }
      return Double.toString(d1).compareTo(Double.toString(d2));
    }
    if (Double.isNaN(d1))
    {
      if (Double.isNaN(d2)) {
        return 0;
      }
      return 1;
    }
    if (Double.isNaN(d2)) {
      return -1;
    }
    return d1 > d2 ? 1 : -1;
  }
  
  private int dateCompare(Date d1, int n1, Date d2, int n2)
  {
    int compareResult = d1.compareTo(d2);
    if (compareResult != 0) {
      return compareResult;
    }
    if (n1 == n2) {
      return 0;
    }
    return n1 > n2 ? 1 : -1;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.ComparisonFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
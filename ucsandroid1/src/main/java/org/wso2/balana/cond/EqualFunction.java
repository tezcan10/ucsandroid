package org.wso2.balana.cond;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class EqualFunction
  extends FunctionBase
{
  public static final String NAME_STRING_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-equal";
  public static final String NAME_BOOLEAN_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:boolean-equal";
  public static final String NAME_INTEGER_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:integer-equal";
  public static final String NAME_DOUBLE_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:double-equal";
  public static final String NAME_DATE_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:date-equal";
  public static final String NAME_TIME_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:time-equal";
  public static final String NAME_DATETIME_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:dateTime-equal";
  public static final String NAME_DAYTIME_DURATION_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-equal";
  public static final String NAME_YEARMONTH_DURATION_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-equal";
  public static final String NAME_ANYURI_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:anyURI-equal";
  public static final String NAME_X500NAME_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:x500Name-equal";
  public static final String NAME_RFC822NAME_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-equal";
  public static final String NAME_HEXBINARY_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:hexBinary-equal";
  public static final String NAME_BASE64BINARY_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:base64Binary-equal";
  public static final String NAME_IPADDRESS_EQUAL = "urn:oasis:names:tc:xacml:2.0:function:ipAddress-equal";
  public static final String NAME_DNSNAME_EQUAL = "urn:oasis:names:tc:xacml:2.0:function:dnsName-equal";
  private static HashMap typeMap = new HashMap();
  
  static
  {
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:string-equal", "http://www.w3.org/2001/XMLSchema#string");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:boolean-equal", "http://www.w3.org/2001/XMLSchema#boolean");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:integer-equal", "http://www.w3.org/2001/XMLSchema#integer");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:double-equal", "http://www.w3.org/2001/XMLSchema#double");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:date-equal", "http://www.w3.org/2001/XMLSchema#date");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:time-equal", "http://www.w3.org/2001/XMLSchema#time");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-equal", "http://www.w3.org/2001/XMLSchema#dateTime");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-equal", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-equal", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-equal", "http://www.w3.org/2001/XMLSchema#anyURI");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-equal", "urn:oasis:names:tc:xacml:1.0:data-type:x500Name");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-equal", "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-equal", "http://www.w3.org/2001/XMLSchema#hexBinary");
    typeMap.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-equal", "http://www.w3.org/2001/XMLSchema#base64Binary");
    typeMap.put("urn:oasis:names:tc:xacml:2.0:function:ipAddress-equal", "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress");
    typeMap.put("urn:oasis:names:tc:xacml:2.0:function:dnsName-equal", "urn:oasis:names:tc:xacml:2.0:data-type:dnsName");
  }
  
  public static EqualFunction getEqualInstance(String functionName, String argumentType)
  {
    return new EqualFunction(functionName, argumentType);
  }
  
  public EqualFunction(String functionName)
  {
    this(functionName, getArgumentType(functionName));
  }
  
  public EqualFunction(String functionName, String argumentType)
  {
    super(functionName, 0, argumentType, false, 2, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  private static String getArgumentType(String functionName)
  {
    String datatype = (String)typeMap.get(functionName);
    if (datatype == null) {
      throw new IllegalArgumentException("not a standard function: " + functionName);
    }
    return datatype;
  }
  
  public static Set getSupportedIdentifiers()
  {
    return Collections.unmodifiableSet(typeMap.keySet());
  }
  
  public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    if (((argValues[1] instanceof StringAttribute)) && 
      ("Any".equals(((StringAttribute)argValues[1]).getValue()))) {
      return EvaluationResult.getInstance(true);
    }
    return EvaluationResult.getInstance(argValues[0].equals(argValues[1]));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.EqualFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
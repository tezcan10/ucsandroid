package org.wso2.balana.cond.xacml3;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

public class StringCreationFunction
  extends FunctionBase
{
  public static final String NAME_STRING_FROM_BOOLEAN = "urn:oasis:names:tc:xacml:3.0:function:string-from-boolean";
  public static final String NAME_STRING_FROM_DOUBLE = "urn:oasis:names:tc:xacml:3.0:function:string-from-double";
  public static final String NAME_STRING_FROM_TIME = "urn:oasis:names:tc:xacml:3.0:function:string-from-time";
  public static final String NAME_STRING_FROM_DATE_TIME = "urn:oasis:names:tc:xacml:3.0:function:string-from-date";
  public static final String NAME_STRING_FROM_DATE = "urn:oasis:names:tc:xacml:3.0:function:dateTime-from-string";
  public static final String NAME_STRING_FROM_INTEGER = "urn:oasis:names:tc:xacml:3.0:function:string-from-integer";
  public static final String NAME_STRING_FROM_URI = "urn:oasis:names:tc:xacml:3.0:function:string-from-anyURI";
  public static final String NAME_STRING_FROM_DAYTIME_DURATION = "urn:oasis:names:tc:xacml:3.0:function:string-from-dayTimeDuration";
  public static final String NAME_STRING_FROM_YEAR_MONTH_DURATION = "urn:oasis:names:tc:xacml:3.0:function:string-from-yearMonthDuration";
  public static final String NAME_STRING_FROM_X500NAME = "urn:oasis:names:tc:xacml:3.0:function:string-from-x500Name";
  public static final String NAME_STRING_FROM_RFC822NAME = "urn:oasis:names:tc:xacml:3.0:function:string-from-rfc822Name";
  public static final String NAME_STRING_FROM_DNS = "urn:oasis:names:tc:xacml:3.0:function:string-from-dnsName";
  public static final String NAME_STRING_FROM_IP_ADDRESS = "urn:oasis:names:tc:xacml:3.0:function:string-from-ipAddress";
  private static Map<String, String> dataTypeMap = new HashMap();
  
  static
  {
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-boolean", "http://www.w3.org/2001/XMLSchema#boolean");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-integer", "http://www.w3.org/2001/XMLSchema#integer");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-double", "http://www.w3.org/2001/XMLSchema#double");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-from-string", "http://www.w3.org/2001/XMLSchema#date");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-time", "http://www.w3.org/2001/XMLSchema#time");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-date", "http://www.w3.org/2001/XMLSchema#dateTime");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-dayTimeDuration", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-yearMonthDuration", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-anyURI", "http://www.w3.org/2001/XMLSchema#anyURI");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-x500Name", "urn:oasis:names:tc:xacml:1.0:data-type:x500Name");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-rfc822Name", "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-ipAddress", "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:string-from-dnsName", "urn:oasis:names:tc:xacml:2.0:data-type:dnsName");
  }
  
  public StringCreationFunction(String functionName)
  {
    super(functionName, 0, getArgumentType(functionName), false, 1, "http://www.w3.org/2001/XMLSchema#string", false);
  }
  
  private static String getArgumentType(String functionName)
  {
    return (String)dataTypeMap.get(functionName);
  }
  
  public static Set<String> getSupportedIdentifiers()
  {
    return Collections.unmodifiableSet(dataTypeMap.keySet());
  }
  
  public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    return new EvaluationResult(new StringAttribute(argValues[0].encode()));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.xacml3.StringCreationFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
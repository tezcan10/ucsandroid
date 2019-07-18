package org.wso2.balana.cond.xacml3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.wso2.balana.Balana;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class StringConversionFunction
  extends FunctionBase
{
  public static final String NAME_BOOLEAN_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:boolean-from-string";
  public static final String NAME_INTEGER_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:integer-from-string";
  public static final String NAME_DOUBLE_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:double-from-string";
  public static final String NAME_TIME_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:time-from-string";
  public static final String NAME_DATE_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:date-from-string";
  public static final String NAME_DATE_TIME_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:dateTime-from-string";
  public static final String NAME_URI_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:anyURI-from-string";
  public static final String NAME_DAYTIME_DURATION_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-from-string";
  public static final String NAME_YEAR_MONTH_DURATION_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-from-string";
  public static final String NAME_X500NAME_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:x500Name-from-string";
  public static final String NAME_RFC822_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:rfc822Name-from-string";
  public static final String NAME_IP_ADDRESS_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:ipAddress-from-string";
  public static final String NAME_DNS_FROM_STRING = "urn:oasis:names:tc:xacml:3.0:function:dnsName-from-string";
  private static Map<String, String> dataTypeMap = new HashMap();
  
  static
  {
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:boolean-from-string", "http://www.w3.org/2001/XMLSchema#boolean");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:integer-from-string", "http://www.w3.org/2001/XMLSchema#integer");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:double-from-string", "http://www.w3.org/2001/XMLSchema#double");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:date-from-string", "http://www.w3.org/2001/XMLSchema#date");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:time-from-string", "http://www.w3.org/2001/XMLSchema#time");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-from-string", "http://www.w3.org/2001/XMLSchema#dateTime");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-from-string", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-from-string", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:anyURI-from-string", "http://www.w3.org/2001/XMLSchema#anyURI");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:x500Name-from-string", "urn:oasis:names:tc:xacml:1.0:data-type:x500Name");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:rfc822Name-from-string", "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:ipAddress-from-string", "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress");
    dataTypeMap.put("urn:oasis:names:tc:xacml:3.0:function:dnsName-from-string", "urn:oasis:names:tc:xacml:2.0:data-type:dnsName");
  }
  
  public StringConversionFunction(String functionName)
  {
    super(functionName, 0, "http://www.w3.org/2001/XMLSchema#string", false, 1, getReturnType(functionName), false);
  }
  
  private static String getReturnType(String functionName)
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
    try
    {
      URI dataType = new URI((String)dataTypeMap.get(getFunctionName()));
      AttributeValue value = Balana.getInstance().getAttributeFactory().createValue(dataType, 
        argValues[0].encode());
      return new EvaluationResult(value);
    }
    catch (URISyntaxException e)
    {
      List<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      return new EvaluationResult(new Status(code, e.getMessage()));
    }
    catch (ParsingException e)
    {
      List<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      return new EvaluationResult(new Status(code, e.getMessage()));
    }
    catch (UnknownIdentifierException e)
    {
      List<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      return new EvaluationResult(new Status(code, e.getMessage()));
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.xacml3.StringConversionFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
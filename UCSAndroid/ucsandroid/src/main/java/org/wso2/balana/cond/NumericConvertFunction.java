package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class NumericConvertFunction
  extends FunctionBase
{
  public static final String NAME_DOUBLE_TO_INTEGER = "urn:oasis:names:tc:xacml:1.0:function:double-to-integer";
  public static final String NAME_INTEGER_TO_DOUBLE = "urn:oasis:names:tc:xacml:1.0:function:integer-to-double";
  private static final int ID_DOUBLE_TO_INTEGER = 0;
  private static final int ID_INTEGER_TO_DOUBLE = 1;
  
  public NumericConvertFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 1, getReturnType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-to-integer")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-to-double")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown convert function " + functionName);
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:double-to-integer");
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-to-double");
    
    return set;
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-to-integer")) {
      return "http://www.w3.org/2001/XMLSchema#double";
    }
    return "http://www.w3.org/2001/XMLSchema#integer";
  }
  
  private static String getReturnType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-to-integer")) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#double";
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    switch (getFunctionId())
    {
    case 0: 
      double arg0 = ((DoubleAttribute)argValues[0]).getValue();
      long longValue = arg0;
      
      result = new EvaluationResult(new IntegerAttribute(longValue));
      break;
    case 1: 
      long arg0 = ((IntegerAttribute)argValues[0]).getValue();
      double doubleValue = arg0;
      
      result = new EvaluationResult(new DoubleAttribute(doubleValue));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.NumericConvertFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
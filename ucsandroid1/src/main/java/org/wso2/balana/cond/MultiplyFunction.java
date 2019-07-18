package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class MultiplyFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_MULTIPLY = "urn:oasis:names:tc:xacml:1.0:function:integer-multiply";
  public static final String NAME_DOUBLE_MULTIPLY = "urn:oasis:names:tc:xacml:1.0:function:double-multiply";
  private static final int ID_INTEGER_MULTIPLY = 0;
  private static final int ID_DOUBLE_MULTIPLY = 1;
  
  public MultiplyFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 2, getArgumentType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-multiply")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-multiply")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown multiply function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-multiply")) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#double";
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-multiply");
    set.add("urn:oasis:names:tc:xacml:1.0:function:double-multiply");
    
    return set;
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
      long arg0 = ((IntegerAttribute)argValues[0]).getValue();
      long arg1 = ((IntegerAttribute)argValues[1]).getValue();
      long product = arg0 * arg1;
      
      result = new EvaluationResult(new IntegerAttribute(product));
      break;
    case 1: 
      double arg0 = ((DoubleAttribute)argValues[0]).getValue();
      double arg1 = ((DoubleAttribute)argValues[1]).getValue();
      double product = arg0 * arg1;
      
      result = new EvaluationResult(new DoubleAttribute(product));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.MultiplyFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
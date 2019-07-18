package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class AddFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_ADD = "urn:oasis:names:tc:xacml:1.0:function:integer-add";
  public static final String NAME_DOUBLE_ADD = "urn:oasis:names:tc:xacml:1.0:function:double-add";
  private static final int ID_INTEGER_ADD = 0;
  private static final int ID_DOUBLE_ADD = 1;
  
  public AddFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, -1, 2, getArgumentType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-add")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-add")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown add function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-add")) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#double";
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-add");
    set.add("urn:oasis:names:tc:xacml:1.0:function:double-add");
    
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
      long sum = 0L;
      for (int index = 0; index < argValues.length; index++)
      {
        long arg = ((IntegerAttribute)argValues[index]).getValue();
        sum += arg;
      }
      result = new EvaluationResult(new IntegerAttribute(sum));
      break;
    case 1: 
      double sum1 = 0.0D;
      for (int index = 0; index < argValues.length; index++)
      {
        double arg = ((DoubleAttribute)argValues[index]).getValue();
        sum1 += arg;
      }
      double lower = Math.floor(sum1);
      double higher = lower + 1.0D;
      if (sum1 - lower == higher - sum1) {
        if (lower % 2.0D == 0.0D) {
          sum1 = lower;
        } else {
          sum1 = higher;
        }
      }
      result = new EvaluationResult(new DoubleAttribute(sum1));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.AddFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
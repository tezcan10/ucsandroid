package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class SubtractFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_SUBTRACT = "urn:oasis:names:tc:xacml:1.0:function:integer-subtract";
  public static final String NAME_DOUBLE_SUBTRACT = "urn:oasis:names:tc:xacml:1.0:function:double-subtract";
  private static final int ID_INTEGER_SUBTRACT = 0;
  private static final int ID_DOUBLE_SUBTRACT = 1;
  
  public SubtractFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 2, getArgumentType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-subtract")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-subtract")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown subtract function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-subtract")) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#double";
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-subtract");
    set.add("urn:oasis:names:tc:xacml:1.0:function:double-subtract");
    
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
      long difference = arg0 - arg1;
      
      result = new EvaluationResult(new IntegerAttribute(difference));
      break;
    case 1: 
      double double0 = ((DoubleAttribute)argValues[0]).getValue();
      double double1 = ((DoubleAttribute)argValues[1]).getValue();
      double difference1 = double0 - double1;
      
      result = new EvaluationResult(new DoubleAttribute(difference1));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.SubtractFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
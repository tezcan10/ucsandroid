package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class DivideFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_DIVIDE = "urn:oasis:names:tc:xacml:1.0:function:integer-divide";
  public static final String NAME_DOUBLE_DIVIDE = "urn:oasis:names:tc:xacml:1.0:function:double-divide";
  private static final int ID_INTEGER_DIVIDE = 0;
  private static final int ID_DOUBLE_DIVIDE = 1;
  
  public DivideFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 2, getArgumentType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-divide")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-divide")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown divide function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-divide")) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#double";
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-divide");
    set.add("urn:oasis:names:tc:xacml:1.0:function:double-divide");
    
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
      long dividend = ((IntegerAttribute)argValues[0]).getValue();
      long divisor = ((IntegerAttribute)argValues[1]).getValue();
      if (divisor == 0L)
      {
        result = makeProcessingError("divide by zero");
      }
      else
      {
        long quotient = dividend / divisor;
        
        result = new EvaluationResult(new IntegerAttribute(quotient));
      }
      break;
    case 1: 
      double dividend = ((DoubleAttribute)argValues[0]).getValue();
      double divisor = ((DoubleAttribute)argValues[1]).getValue();
      if (divisor == 0.0D)
      {
        result = makeProcessingError("divide by zero");
      }
      else
      {
        double quotient = dividend / divisor;
        
        result = new EvaluationResult(new DoubleAttribute(quotient));
      }
      break;
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.DivideFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
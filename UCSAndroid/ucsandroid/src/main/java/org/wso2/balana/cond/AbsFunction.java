package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class AbsFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_ABS = "urn:oasis:names:tc:xacml:1.0:function:integer-abs";
  public static final String NAME_DOUBLE_ABS = "urn:oasis:names:tc:xacml:1.0:function:double-abs";
  private static final int ID_INTEGER_ABS = 0;
  private static final int ID_DOUBLE_ABS = 1;
  
  public AbsFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 1, getArgumentType(functionName), false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-abs")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:double-abs")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown abs function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-abs")) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#double";
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-abs");
    set.add("urn:oasis:names:tc:xacml:1.0:function:double-abs");
    
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
      long arg = ((IntegerAttribute)argValues[0]).getValue();
      long absValue = Math.abs(arg);
      
      result = new EvaluationResult(new IntegerAttribute(absValue));
      break;
    case 1: 
      double arg1 = ((DoubleAttribute)argValues[0]).getValue();
      double absValue1 = Math.abs(arg1);
      
      result = new EvaluationResult(new DoubleAttribute(absValue1));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.AbsFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class LogicalFunction
  extends FunctionBase
{
  public static final String NAME_OR = "urn:oasis:names:tc:xacml:1.0:function:or";
  public static final String NAME_AND = "urn:oasis:names:tc:xacml:1.0:function:and";
  private static final int ID_OR = 0;
  private static final int ID_AND = 1;
  
  public LogicalFunction(String functionName)
  {
    super(functionName, getId(functionName), "http://www.w3.org/2001/XMLSchema#boolean", false, -1, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:or")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:and")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown logical function: " + functionName);
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:or");
    set.add("urn:oasis:names:tc:xacml:1.0:function:and");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    Iterator it = inputs.iterator();
    while (it.hasNext())
    {
      Evaluatable eval = (Evaluatable)it.next();
      
      EvaluationResult result = eval.evaluate(context);
      if (result.indeterminate()) {
        return result;
      }
      AttributeValue value = result.getAttributeValue();
      boolean argBooleanValue = ((BooleanAttribute)value).getValue();
      switch (getFunctionId())
      {
      case 0: 
        if (argBooleanValue) {
          return EvaluationResult.getTrueInstance();
        }
        break;
      case 1: 
        if (!argBooleanValue) {
          return EvaluationResult.getFalseInstance();
        }
        break;
      }
    }
    if (getFunctionId() == 0) {
      return EvaluationResult.getFalseInstance();
    }
    return EvaluationResult.getTrueInstance();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.LogicalFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class NotFunction
  extends FunctionBase
{
  public static final String NAME_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";
  
  public NotFunction(String functionName)
  {
    super("urn:oasis:names:tc:xacml:1.0:function:not", 0, "http://www.w3.org/2001/XMLSchema#boolean", false, 1, "http://www.w3.org/2001/XMLSchema#boolean", false);
    if (!functionName.equals("urn:oasis:names:tc:xacml:1.0:function:not")) {
      throw new IllegalArgumentException("unknown not function: " + functionName);
    }
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:not");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    boolean arg = ((BooleanAttribute)argValues[0]).getValue();
    return EvaluationResult.getInstance(!arg);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.NotFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
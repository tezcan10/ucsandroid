package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class FloorFunction
  extends FunctionBase
{
  public static final String NAME_FLOOR = "urn:oasis:names:tc:xacml:1.0:function:floor";
  
  public FloorFunction(String functionName)
  {
    super("urn:oasis:names:tc:xacml:1.0:function:floor", 0, "http://www.w3.org/2001/XMLSchema#double", false, 1, "http://www.w3.org/2001/XMLSchema#double", false);
    if (!functionName.equals("urn:oasis:names:tc:xacml:1.0:function:floor")) {
      throw new IllegalArgumentException("unknown floor function: " + functionName);
    }
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:floor");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    double arg = ((DoubleAttribute)argValues[0]).getValue();
    
    return new EvaluationResult(new DoubleAttribute(Math.floor(arg)));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.FloorFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
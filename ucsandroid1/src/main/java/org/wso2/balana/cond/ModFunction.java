package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class ModFunction
  extends FunctionBase
{
  public static final String NAME_INTEGER_MOD = "urn:oasis:names:tc:xacml:1.0:function:integer-mod";
  
  public ModFunction(String functionName)
  {
    super("urn:oasis:names:tc:xacml:1.0:function:integer-mod", 0, "http://www.w3.org/2001/XMLSchema#integer", false, 2, "http://www.w3.org/2001/XMLSchema#integer", false);
    if (!functionName.equals("urn:oasis:names:tc:xacml:1.0:function:integer-mod")) {
      throw new IllegalArgumentException("unknown mod function: " + functionName);
    }
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:integer-mod");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    long arg0 = ((IntegerAttribute)argValues[0]).getValue();
    long arg1 = ((IntegerAttribute)argValues[1]).getValue();
    
    return new EvaluationResult(new IntegerAttribute(arg0 % arg1));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.ModFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
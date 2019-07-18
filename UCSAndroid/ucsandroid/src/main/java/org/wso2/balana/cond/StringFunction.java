package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class StringFunction
  extends FunctionBase
{
  public static final String NAME_STRING_CONCATENATE = "urn:oasis:names:tc:xacml:2.0:function:string-concatenate";
  private static final int ID_STRING_CONCATENATE = 0;
  
  public StringFunction(String functionName)
  {
    super(functionName, 0, "http://www.w3.org/2001/XMLSchema#string", false, -1, 2, "http://www.w3.org/2001/XMLSchema#string", false);
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:2.0:function:string-concatenate");
    
    return set;
  }
  
  public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    switch (getFunctionId())
    {
    case 0: 
      String str = ((StringAttribute)argValues[0]).getValue();
      StringBuffer buffer = new StringBuffer(str);
      for (int i = 1; i < argValues.length; i++) {
        buffer.append(((StringAttribute)argValues[i]).getValue());
      }
      result = new EvaluationResult(new StringAttribute(buffer.toString()));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.StringFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
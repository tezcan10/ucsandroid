package org.wso2.balana.cond;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class ConditionBagFunction
  extends BagFunction
{
  private static HashMap argMap = new HashMap();
  
  static
  {
    for (int i = 0; i < baseTypes.length; i++)
    {
      String[] args = { baseTypes[i], baseTypes[i] };
      
      argMap.put("urn:oasis:names:tc:xacml:1.0:function:" + simpleTypes[i] + "-is-in", args);
    }
    for (int i = 0; i < baseTypes2.length; i++)
    {
      String[] args = { baseTypes2[i], baseTypes2[i] };
      
      argMap.put("urn:oasis:names:tc:xacml:2.0:function:" + simpleTypes2[i] + "-is-in", args);
    }
  }
  
  public ConditionBagFunction(String functionName)
  {
    super(functionName, 0, getArguments(functionName));
  }
  
  public ConditionBagFunction(String functionName, String datatype)
  {
    super(functionName, 0, new String[] { datatype, datatype });
  }
  
  private static String[] getArguments(String functionName)
  {
    String[] args = (String[])argMap.get(functionName);
    if (args == null) {
      throw new IllegalArgumentException("unknown bag function: " + functionName);
    }
    return args;
  }
  
  public static Set getSupportedIdentifiers()
  {
    return Collections.unmodifiableSet(argMap.keySet());
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    AttributeValue item = argValues[0];
    BagAttribute bag = (BagAttribute)argValues[1];
    
    return new EvaluationResult(BooleanAttribute.getInstance(bag.contains(item)));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.ConditionBagFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
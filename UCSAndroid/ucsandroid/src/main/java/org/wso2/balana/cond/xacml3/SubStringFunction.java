package org.wso2.balana.cond.xacml3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

public class SubStringFunction
  extends FunctionBase
{
  public static final String NAME_STRING_SUB_STRING = "urn:oasis:names:tc:xacml:3.0:function:string-substring";
  public static final String NAME_ANY_URI_SUB_STRING = "urn:oasis:names:tc:xacml:3.0:function:anyURI-substring";
  private static final int ID_STRING_SUB_STRING = 0;
  private static final int ID_ANY_URI_SUB_STRING = 1;
  
  public SubStringFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 3, "http://www.w3.org/2001/XMLSchema#string", false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-substring")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:anyURI-substring")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown divide function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-substring")) {
      return "http://www.w3.org/2001/XMLSchema#string";
    }
    return "http://www.w3.org/2001/XMLSchema#anyURI";
  }
  
  public static Set<String> getSupportedIdentifiers()
  {
    Set<String> set = new HashSet();
    set.add("urn:oasis:names:tc:xacml:3.0:function:string-substring");
    set.add("urn:oasis:names:tc:xacml:3.0:function:anyURI-substring");
    return set;
  }
  
  public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    String processedString = argValues[0].encode().substring(Integer.parseInt(argValues[1].encode()), 
      Integer.parseInt(argValues[2].encode()));
    
    return new EvaluationResult(new StringAttribute(processedString));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.xacml3.SubStringFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond.xacml3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

public class StringComparingFunction
  extends FunctionBase
{
  public static final String NAME_STRING_START_WITH = "urn:oasis:names:tc:xacml:3.0:function:string-starts-with";
  public static final String NAME_ANY_URI_START_WITH = "urn:oasis:names:tc:xacml:3.0:function:anyURI-starts-with";
  public static final String NAME_STRING_ENDS_WITH = "urn:oasis:names:tc:xacml:3.0:function:string-ends-with";
  public static final String NAME_ANY_URI_ENDS_WITH = "urn:oasis:names:tc:xacml:3.0:function:anyURI-ends-with";
  public static final String NAME_STRING_CONTAIN = "urn:oasis:names:tc:xacml:3.0:function:string-contains";
  public static final String NAME_ANY_URI_CONTAIN = "urn:oasis:names:tc:xacml:3.0:function:anyURI-contains";
  private static final int ID_STRING_START_WITH = 0;
  private static final int ID_ANY_URI_START_WITH = 1;
  private static final int ID_STRING_ENDS_WITH = 2;
  private static final int ID_ANY_URI_ENDS_WITH = 3;
  private static final int ID_STRING_CONTAIN = 4;
  private static final int ID_ANY_URI_CONTAIN = 5;
  
  public StringComparingFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 2, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-starts-with")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:anyURI-starts-with")) {
      return 1;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-ends-with")) {
      return 2;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:anyURI-ends-with")) {
      return 3;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-contains")) {
      return 4;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:anyURI-contains")) {
      return 5;
    }
    throw new IllegalArgumentException("unknown start-with function " + functionName);
  }
  
  private static String getArgumentType(String functionName)
  {
    if ((functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-starts-with")) || (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-ends-with")) || 
      (functionName.equals("urn:oasis:names:tc:xacml:3.0:function:string-contains"))) {
      return "http://www.w3.org/2001/XMLSchema#integer";
    }
    return "http://www.w3.org/2001/XMLSchema#anyURI";
  }
  
  public static Set<String> getSupportedIdentifiers()
  {
    Set<String> set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:3.0:function:string-starts-with");
    set.add("urn:oasis:names:tc:xacml:3.0:function:anyURI-starts-with");
    set.add("urn:oasis:names:tc:xacml:3.0:function:string-ends-with");
    set.add("urn:oasis:names:tc:xacml:3.0:function:anyURI-ends-with");
    set.add("urn:oasis:names:tc:xacml:3.0:function:string-contains");
    set.add("urn:oasis:names:tc:xacml:3.0:function:anyURI-contains");
    
    return set;
  }
  
  public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    int id = getFunctionId();
    if ((id == 0) || (id == 1)) {
      return EvaluationResult.getInstance(argValues[1].encode()
        .startsWith(argValues[0].encode()));
    }
    if ((id == 2) || (id == 3)) {
      return EvaluationResult.getInstance(argValues[1].encode()
        .endsWith(argValues[0].encode()));
    }
    return EvaluationResult.getInstance(argValues[1].encode()
      .contains(argValues[0].encode()));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.xacml3.StringComparingFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class StringNormalizeFunction
  extends FunctionBase
{
  public static final String NAME_STRING_NORMALIZE_SPACE = "urn:oasis:names:tc:xacml:1.0:function:string-normalize-space";
  public static final String NAME_STRING_NORMALIZE_TO_LOWER_CASE = "urn:oasis:names:tc:xacml:1.0:function:string-normalize-to-lower-case";
  private static final int ID_STRING_NORMALIZE_SPACE = 0;
  private static final int ID_STRING_NORMALIZE_TO_LOWER_CASE = 1;
  
  public StringNormalizeFunction(String functionName)
  {
    super(functionName, getId(functionName), "http://www.w3.org/2001/XMLSchema#string", false, 1, "http://www.w3.org/2001/XMLSchema#string", false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:string-normalize-space")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:string-normalize-to-lower-case")) {
      return 1;
    }
    throw new IllegalArgumentException("unknown normalize function " + functionName);
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:string-normalize-space");
    set.add("urn:oasis:names:tc:xacml:1.0:function:string-normalize-to-lower-case");
    
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
      String str = ((StringAttribute)argValues[0]).getValue();
      
      int startIndex = 0;
      int endIndex = str.length() - 1;
      do
      {
        startIndex++;
        if (startIndex > endIndex) {
          break;
        }
      } while (Character.isWhitespace(str.charAt(startIndex)));
      while ((startIndex <= endIndex) && (Character.isWhitespace(str.charAt(endIndex)))) {
        endIndex--;
      }
      String strResult = str.substring(startIndex, endIndex + 1);
      
      result = new EvaluationResult(new StringAttribute(strResult));
      break;
    case 1: 
      String string = ((StringAttribute)argValues[0]).getValue();
      
      String stringResult = string.toLowerCase();
      
      result = new EvaluationResult(new StringAttribute(stringResult));
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.StringNormalizeFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
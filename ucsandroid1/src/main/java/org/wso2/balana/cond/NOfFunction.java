package org.wso2.balana.cond;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class NOfFunction
  extends FunctionBase
{
  public static final String NAME_N_OF = "urn:oasis:names:tc:xacml:1.0:function:n-of";
  
  public NOfFunction(String functionName)
  {
    super("urn:oasis:names:tc:xacml:1.0:function:n-of", 0, "http://www.w3.org/2001/XMLSchema#boolean", false);
    if (!functionName.equals("urn:oasis:names:tc:xacml:1.0:function:n-of")) {
      throw new IllegalArgumentException("unknown nOf function: " + functionName);
    }
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:n-of");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    Iterator it = inputs.iterator();
    Evaluatable eval = (Evaluatable)it.next();
    
    EvaluationResult result = eval.evaluate(context);
    if (result.indeterminate()) {
      return result;
    }
    long n = ((IntegerAttribute)result.getAttributeValue()).getValue();
    if (n < 0L) {
      return makeProcessingError("First argument to " + getFunctionName() + 
        " cannot be negative.");
    }
    if (n == 0L) {
      return EvaluationResult.getTrueInstance();
    }
    long remainingArgs = inputs.size() - 1;
    if (n > remainingArgs) {
      return makeProcessingError("not enough arguments to n-of to find " + n + 
        " true values");
    }
    while (remainingArgs >= n)
    {
      eval = (Evaluatable)it.next();
      
      result = eval.evaluate(context);
      if (result.indeterminate()) {
        return result;
      }
      if (((BooleanAttribute)result.getAttributeValue()).getValue()) {
        if (--n == 0L) {
          return EvaluationResult.getTrueInstance();
        }
      }
      remainingArgs -= 1L;
    }
    return EvaluationResult.getFalseInstance();
  }
  
  public void checkInputs(List inputs)
    throws IllegalArgumentException
  {
    Object[] list = inputs.toArray();
    for (int i = 0; i < list.length; i++) {
      if (((Evaluatable)list[i]).returnsBag()) {
        throw new IllegalArgumentException("n-of can't use bags");
      }
    }
    checkInputsNoBag(inputs);
  }
  
  public void checkInputsNoBag(List inputs)
    throws IllegalArgumentException
  {
    Object[] list = inputs.toArray();
    if (list.length == 0) {
      throw new IllegalArgumentException("n-of requires an argument");
    }
    Evaluatable eval = (Evaluatable)list[0];
    if (!eval.getType().toString().equals("http://www.w3.org/2001/XMLSchema#integer")) {
      throw new IllegalArgumentException("first argument to n-of must be an integer");
    }
    for (int i = 1; i < list.length; i++) {
      if (!((Evaluatable)list[i]).getType().toString().equals("http://www.w3.org/2001/XMLSchema#boolean")) {
        throw new IllegalArgumentException("invalid parameter in n-of: expected boolean");
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.NOfFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
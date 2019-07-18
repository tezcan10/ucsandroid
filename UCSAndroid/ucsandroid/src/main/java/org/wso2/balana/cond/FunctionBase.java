package org.wso2.balana.cond;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.Indenter;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public abstract class FunctionBase
  implements Function
{
  public static final String FUNCTION_NS = "urn:oasis:names:tc:xacml:1.0:function:";
  public static final String FUNCTION_NS_2 = "urn:oasis:names:tc:xacml:2.0:function:";
  public static final String FUNCTION_NS_3 = "urn:oasis:names:tc:xacml:3.0:function:";
  private static List processingErrList = null;
  private String functionName;
  private int functionId;
  private String returnType;
  private boolean returnsBag;
  private boolean singleType;
  private String paramType;
  private boolean paramIsBag;
  private int numParams;
  private int minParams;
  private String[] paramTypes;
  private boolean[] paramsAreBags;
  
  public FunctionBase(String functionName, int functionId, String paramType, boolean paramIsBag, int numParams, String returnType, boolean returnsBag)
  {
    this(functionName, functionId, returnType, returnsBag);
    
    singleType = true;
    
    this.paramType = paramType;
    this.paramIsBag = paramIsBag;
    this.numParams = numParams;
    minParams = 0;
  }
  
  public FunctionBase(String functionName, int functionId, String paramType, boolean paramIsBag, int numParams, int minParams, String returnType, boolean returnsBag)
  {
    this(functionName, functionId, returnType, returnsBag);
    
    singleType = true;
    
    this.paramType = paramType;
    this.paramIsBag = paramIsBag;
    this.numParams = numParams;
    this.minParams = minParams;
  }
  
  public FunctionBase(String functionName, int functionId, String[] paramTypes, boolean[] paramIsBag, String returnType, boolean returnsBag)
  {
    this(functionName, functionId, returnType, returnsBag);
    
    singleType = false;
    
    this.paramTypes = paramTypes;
    paramsAreBags = paramIsBag;
  }
  
  public FunctionBase(String functionName, int functionId, String returnType, boolean returnsBag)
  {
    this.functionName = functionName;
    this.functionId = functionId;
    this.returnType = returnType;
    this.returnsBag = returnsBag;
  }
  
  public URI getIdentifier()
  {
    try
    {
      return new URI(functionName);
    }
    catch (URISyntaxException use)
    {
      throw new IllegalArgumentException("invalid URI");
    }
  }
  
  public String getFunctionName()
  {
    return functionName;
  }
  
  public int getFunctionId()
  {
    return functionId;
  }
  
  public URI getType()
  {
    return getReturnType();
  }
  
  public URI getReturnType()
  {
    try
    {
      return new URI(returnType);
    }
    catch (Exception e) {}
    return null;
  }
  
  public boolean returnsBag()
  {
    return returnsBag;
  }
  
  public String getReturnTypeAsString()
  {
    return returnType;
  }
  
  protected static EvaluationResult makeProcessingError(String message)
  {
    if (processingErrList == null)
    {
      String[] errStrings = { "urn:oasis:names:tc:xacml:1.0:status:processing-error" };
      processingErrList = Arrays.asList(errStrings);
    }
    Status errStatus = new Status(processingErrList, message);
    EvaluationResult processingError = new EvaluationResult(errStatus);
    
    return processingError;
  }
  
  protected EvaluationResult evalArgs(List<Evaluatable> params, EvaluationCtx context, AttributeValue[] args)
  {
    Iterator it = params.iterator();
    int index = 0;
    while (it.hasNext())
    {
      Evaluatable eval = (Evaluatable)it.next();
      EvaluationResult result = eval.evaluate(context);
      if (result.indeterminate()) {
        return result;
      }
      args[(index++)] = result.getAttributeValue();
    }
    return null;
  }
  
  public void checkInputs(List inputs)
    throws IllegalArgumentException
  {
    if (singleType)
    {
      if (numParams != -1)
      {
        if (inputs.size() != numParams) {
          throw new IllegalArgumentException("wrong number of args to " + 
            functionName);
        }
      }
      else if (inputs.size() < minParams) {
        throw new IllegalArgumentException("not enough args to " + functionName);
      }
      Iterator it = inputs.iterator();
      while (it.hasNext())
      {
        Evaluatable eval = (Evaluatable)it.next();
        if ((!eval.getType().toString().equals(paramType)) || 
          (eval.returnsBag() != paramIsBag)) {
          throw new IllegalArgumentException("illegal parameter");
        }
      }
    }
    else
    {
      if (paramTypes.length != inputs.size()) {
        throw new IllegalArgumentException("wrong number of args to " + functionName);
      }
      Iterator it = inputs.iterator();
      int i = 0;
      while (it.hasNext())
      {
        Evaluatable eval = (Evaluatable)it.next();
        if ((!eval.getType().toString().equals(paramTypes[i])) || 
          (eval.returnsBag() != paramsAreBags[i])) {
          throw new IllegalArgumentException("illegal parameter");
        }
        i++;
      }
    }
  }
  
  public void checkInputsNoBag(List inputs)
    throws IllegalArgumentException
  {
    if (singleType)
    {
      if (paramIsBag) {
        throw new IllegalArgumentException(functionName + "needs" + "bags on input");
      }
      if (numParams != -1)
      {
        if (inputs.size() != numParams) {
          throw new IllegalArgumentException("wrong number of args to " + 
            functionName);
        }
      }
      else if (inputs.size() < minParams) {
        throw new IllegalArgumentException("not enough args to " + functionName);
      }
      Iterator it = inputs.iterator();
      while (it.hasNext())
      {
        Evaluatable eval = (Evaluatable)it.next();
        if (!eval.getType().toString().equals(paramType)) {
          throw new IllegalArgumentException("illegal parameter");
        }
      }
    }
    else
    {
      if (paramTypes.length != inputs.size()) {
        throw new IllegalArgumentException("wrong number of args to " + functionName);
      }
      Iterator it = inputs.iterator();
      int i = 0;
      while (it.hasNext())
      {
        Evaluatable eval = (Evaluatable)it.next();
        if ((!eval.getType().toString().equals(paramTypes[i])) || (paramsAreBags[i])) {
          throw new IllegalArgumentException("illegal parameter");
        }
        i++;
      }
    }
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    out.println(indenter.makeString() + "<Function FunctionId=\"" + getFunctionName() + "\"/>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.FunctionBase
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
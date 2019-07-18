package org.wso2.balana.cond;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class GeneralBagFunction
  extends BagFunction
{
  private static final int ID_BASE_ONE_AND_ONLY = 0;
  private static final int ID_BASE_BAG_SIZE = 1;
  private static final int ID_BASE_BAG = 2;
  private static HashMap paramMap = new HashMap();
  private static Set supportedIds;
  
  static
  {
    for (int i = 0; i < baseTypes.length; i++)
    {
      String baseType = baseTypes[i];
      String functionBaseName = "urn:oasis:names:tc:xacml:1.0:function:" + simpleTypes[i];
      
      paramMap.put(functionBaseName + "-one-and-only", new BagParameters(
        0, baseType, true, 1, baseType, false));
      
      paramMap.put(functionBaseName + "-bag-size", new BagParameters(1, 
        baseType, true, 1, "http://www.w3.org/2001/XMLSchema#integer", false));
      
      paramMap.put(functionBaseName + "-bag", new BagParameters(2, baseType, 
        false, -1, baseType, true));
    }
    for (int i = 0; i < baseTypes2.length; i++)
    {
      String baseType = baseTypes2[i];
      String functionBaseName = "urn:oasis:names:tc:xacml:2.0:function:" + simpleTypes2[i];
      
      paramMap.put(functionBaseName + "-one-and-only", new BagParameters(
        0, baseType, true, 1, baseType, false));
      
      paramMap.put(functionBaseName + "-bag-size", new BagParameters(1, 
        baseType, true, 1, "http://www.w3.org/2001/XMLSchema#integer", false));
      
      paramMap.put(functionBaseName + "-bag", new BagParameters(2, baseType, 
        false, -1, baseType, true));
    }
    supportedIds = Collections.unmodifiableSet(new HashSet(paramMap.keySet()));
    
    paramMap.put("-one-and-only", new BagParameters(0, null, true, 1, 
      null, false));
    paramMap.put("-bag-size", new BagParameters(1, null, true, 1, 
      "http://www.w3.org/2001/XMLSchema#integer", false));
    paramMap.put("-bag", new BagParameters(2, null, false, -1, null, true));
  }
  
  public GeneralBagFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), getIsBag(functionName), getNumArgs(functionName), getReturnType(functionName), getReturnsBag(functionName));
  }
  
  public GeneralBagFunction(String functionName, String datatype, String functionType)
  {
    super(functionName, getId(functionType), datatype, getIsBag(functionType), getNumArgs(functionType), getCustomReturnType(functionType, datatype), getReturnsBag(functionType));
  }
  
  private static int getId(String functionName)
  {
    BagParameters params = (BagParameters)paramMap.get(functionName);
    if (params == null) {
      throw new IllegalArgumentException("unknown bag function: " + functionName);
    }
    return id;
  }
  
  private static String getArgumentType(String functionName)
  {
    return paramMapgetarg;
  }
  
  private static boolean getIsBag(String functionName)
  {
    return paramMapgetargIsBag;
  }
  
  private static int getNumArgs(String functionName)
  {
    return paramMapgetparams;
  }
  
  private static String getReturnType(String functionName)
  {
    return paramMapgetreturnType;
  }
  
  private static boolean getReturnsBag(String functionName)
  {
    return paramMapgetreturnsBag;
  }
  
  private static String getCustomReturnType(String functionType, String datatype)
  {
    String ret = paramMapgetreturnType;
    if (ret == null) {
      return datatype;
    }
    return ret;
  }
  
  public static Set getSupportedIdentifiers()
  {
    return supportedIds;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    AttributeValue attrResult = null;
    switch (getFunctionId())
    {
    case 0: 
      BagAttribute bag = (BagAttribute)argValues[0];
      if (bag.size() != 1) {
        return makeProcessingError(getFunctionName() + " expects " + 
          "a bag that contains a single " + "element, got a bag with " + bag.size() + 
          " elements");
      }
      attrResult = (AttributeValue)bag.iterator().next();
      break;
    case 1: 
      BagAttribute bag = (BagAttribute)argValues[0];
      
      attrResult = new IntegerAttribute(bag.size());
      break;
    case 2: 
      List argsList = Arrays.asList(argValues);
      
      attrResult = new BagAttribute(getReturnType(), argsList);
    }
    return new EvaluationResult(attrResult);
  }
  
  private static class BagParameters
  {
    public int id;
    public String arg;
    public boolean argIsBag;
    public int params;
    public String returnType;
    public boolean returnsBag;
    
    public BagParameters(int id, String arg, boolean argIsBag, int params, String returnType, boolean returnsBag)
    {
      this.id = id;
      this.arg = arg;
      this.argIsBag = argIsBag;
      this.params = params;
      this.returnType = returnType;
      this.returnsBag = returnsBag;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.GeneralBagFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
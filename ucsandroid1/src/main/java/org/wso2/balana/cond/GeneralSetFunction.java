package org.wso2.balana.cond;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class GeneralSetFunction
  extends SetFunction
{
  private static final int ID_BASE_INTERSECTION = 0;
  private static final int ID_BASE_UNION = 1;
  private static HashMap<String, Integer> idMap = new HashMap();
  private static HashMap<String, String> typeMap = new HashMap();
  
  static
  {
    idMap.put("-intersection", Integer.valueOf(0));
    idMap.put("-union", Integer.valueOf(1));
    for (int i = 0; i < baseTypes.length; i++)
    {
      String baseName = "urn:oasis:names:tc:xacml:1.0:function:" + simpleTypes[i];
      String baseType = baseTypes[i];
      
      idMap.put(baseName + "-intersection", Integer.valueOf(0));
      idMap.put(baseName + "-union", Integer.valueOf(1));
      
      typeMap.put(baseName + "-intersection", baseType);
      typeMap.put(baseName + "-union", baseType);
    }
    for (int i = 0; i < baseTypes2.length; i++)
    {
      String baseName = "urn:oasis:names:tc:xacml:2.0:function:" + simpleTypes2[i];
      String baseType = baseTypes2[i];
      
      idMap.put(baseName + "-intersection", Integer.valueOf(0));
      idMap.put(baseName + "-union", Integer.valueOf(1));
      
      typeMap.put(baseName + "-intersection", baseType);
      typeMap.put(baseName + "-union", baseType);
    }
  }
  
  public GeneralSetFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), getArgumentType(functionName), true);
  }
  
  public GeneralSetFunction(String functionName, String datatype, String functionType)
  {
    super(functionName, getId(functionType), datatype, datatype, true);
  }
  
  private static int getId(String functionName)
  {
    Integer id = (Integer)idMap.get(functionName);
    if (id == null) {
      throw new IllegalArgumentException("unknown set function " + functionName);
    }
    return id.intValue();
  }
  
  private static String getArgumentType(String functionName)
  {
    return (String)typeMap.get(functionName);
  }
  
  public static Set getSupportedIdentifiers()
  {
    return Collections.unmodifiableSet(idMap.keySet());
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult evalResult = evalArgs(inputs, context, argValues);
    if (evalResult != null) {
      return evalResult;
    }
    BagAttribute[] bags = new BagAttribute[2];
    bags[0] = ((BagAttribute)argValues[0]);
    bags[1] = ((BagAttribute)argValues[1]);
    
    AttributeValue result = null;
    Set set = new HashSet();
    switch (getFunctionId())
    {
    case 0: 
      Iterator it = bags[0].iterator();
      while (it.hasNext())
      {
        AttributeValue value = (AttributeValue)it.next();
        if (bags[1].contains(value)) {
          set.add(value);
        }
      }
      result = new BagAttribute(bags[0].getType(), set);
      
      break;
    case 1: 
      Iterator it0 = bags[0].iterator();
      while (it0.hasNext()) {
        set.add(it0.next());
      }
      Iterator it1 = bags[1].iterator();
      while (it1.hasNext()) {
        set.add(it1.next());
      }
      result = new BagAttribute(bags[0].getType(), set);
    }
    return new EvaluationResult(result);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.GeneralSetFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
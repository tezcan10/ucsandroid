package org.wso2.balana.cond;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class ConditionSetFunction
  extends SetFunction
{
  private static final int ID_BASE_AT_LEAST_ONE_MEMBER_OF = 0;
  private static final int ID_BASE_SUBSET = 1;
  private static final int ID_BASE_SET_EQUALS = 2;
  private static HashMap<String, Integer> idMap = new HashMap();
  private static HashMap<String, String> typeMap = new HashMap();
  private static Set<String> supportedIds;
  
  static
  {
    for (int i = 0; i < baseTypes.length; i++)
    {
      String baseName = "urn:oasis:names:tc:xacml:1.0:function:" + simpleTypes[i];
      String baseType = baseTypes[i];
      
      idMap.put(baseName + "-at-least-one-member-of", 
        Integer.valueOf(0));
      idMap.put(baseName + "-subset", Integer.valueOf(1));
      idMap.put(baseName + "-set-equals", Integer.valueOf(2));
      
      typeMap.put(baseName + "-at-least-one-member-of", baseType);
      typeMap.put(baseName + "-subset", baseType);
      typeMap.put(baseName + "-set-equals", baseType);
    }
    for (int i = 0; i < baseTypes2.length; i++)
    {
      String baseName = "urn:oasis:names:tc:xacml:2.0:function:" + simpleTypes2[i];
      String baseType = baseTypes2[i];
      
      idMap.put(baseName + "-at-least-one-member-of", 
        Integer.valueOf(0));
      idMap.put(baseName + "-subset", Integer.valueOf(1));
      idMap.put(baseName + "-set-equals", Integer.valueOf(2));
      
      typeMap.put(baseName + "-at-least-one-member-of", baseType);
      typeMap.put(baseName + "-subset", baseType);
      typeMap.put(baseName + "-set-equals", baseType);
    }
    supportedIds = Collections.unmodifiableSet(new HashSet(idMap.keySet()));
    
    idMap.put("-at-least-one-member-of", Integer.valueOf(0));
    idMap.put("-subset", Integer.valueOf(1));
    idMap.put("-set-equals", Integer.valueOf(2));
  }
  
  public ConditionSetFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentType(functionName), "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  public ConditionSetFunction(String functionName, String datatype, String functionType)
  {
    super(functionName, getId(functionName), datatype, "http://www.w3.org/2001/XMLSchema#boolean", false);
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
    return supportedIds;
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
    switch (getFunctionId())
    {
    case 0: 
      result = BooleanAttribute.getFalseInstance();
      Iterator it = bags[0].iterator();
      while (it.hasNext()) {
        if (bags[1].contains((AttributeValue)it.next()))
        {
          result = BooleanAttribute.getTrueInstance();
          break;
        }
      }
      break;
    case 1: 
      boolean subset = bags[1].containsAll(bags[0]);
      result = BooleanAttribute.getInstance(subset);
      
      break;
    case 2: 
      boolean equals = (bags[1].containsAll(bags[0])) && (bags[0].containsAll(bags[1]));
      result = BooleanAttribute.getInstance(equals);
    }
    return new EvaluationResult(result);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.ConditionSetFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
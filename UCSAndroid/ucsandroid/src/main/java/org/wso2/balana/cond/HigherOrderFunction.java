package org.wso2.balana.cond;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.wso2.balana.Indenter;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class HigherOrderFunction
  implements Function
{
  public static final String NAME_ANY_OF = "urn:oasis:names:tc:xacml:1.0:function:any-of";
  public static final String NAME_ALL_OF = "urn:oasis:names:tc:xacml:1.0:function:all-of";
  public static final String NAME_ANY_OF_ANY = "urn:oasis:names:tc:xacml:1.0:function:any-of-any";
  public static final String NAME_ALL_OF_ANY = "urn:oasis:names:tc:xacml:1.0:function:all-of-any";
  public static final String NAME_ANY_OF_ALL = "urn:oasis:names:tc:xacml:1.0:function:any-of-all";
  public static final String NAME_ALL_OF_ALL = "urn:oasis:names:tc:xacml:1.0:function:all-of-all";
  private static final int ID_ANY_OF = 0;
  private static final int ID_ALL_OF = 1;
  private static final int ID_ANY_OF_ANY = 2;
  private static final int ID_ALL_OF_ANY = 3;
  private static final int ID_ANY_OF_ALL = 4;
  private static final int ID_ALL_OF_ALL = 5;
  private static HashMap<String, Integer> idMap;
  private int functionId;
  private URI identifier;
  private boolean secondIsBag;
  private static URI returnTypeURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      returnTypeURI = new URI("http://www.w3.org/2001/XMLSchema#boolean");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
    idMap = new HashMap();
    
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:any-of", Integer.valueOf(0));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:all-of", Integer.valueOf(1));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:any-of-any", Integer.valueOf(2));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:all-of-any", Integer.valueOf(3));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:any-of-all", Integer.valueOf(4));
    idMap.put("urn:oasis:names:tc:xacml:1.0:function:all-of-all", Integer.valueOf(5));
  }
  
  public HigherOrderFunction(String functionName)
  {
    Integer i = (Integer)idMap.get(functionName);
    if (i == null) {
      throw new IllegalArgumentException("unknown function: " + functionName);
    }
    functionId = i.intValue();
    try
    {
      identifier = new URI(functionName);
    }
    catch (URISyntaxException use)
    {
      throw new IllegalArgumentException("invalid URI");
    }
    if ((functionId != 0) && (functionId != 1)) {
      secondIsBag = true;
    } else {
      secondIsBag = false;
    }
  }
  
  public static Set getSupportedIdentifiers()
  {
    return Collections.unmodifiableSet(idMap.keySet());
  }
  
  public URI getIdentifier()
  {
    return identifier;
  }
  
  public URI getType()
  {
    return getReturnType();
  }
  
  public URI getReturnType()
  {
    if (earlyException != null) {
      throw earlyException;
    }
    return returnTypeURI;
  }
  
  public boolean returnsBag()
  {
    return false;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    Iterator iterator = inputs.iterator();
    
    Expression xpr = (Expression)iterator.next();
    Function function = null;
    if ((xpr instanceof Function)) {
      function = (Function)xpr;
    } else {
      function = (Function)((VariableReference)xpr).getReferencedDefinition()
        .getExpression();
    }
    AttributeValue[] args = new AttributeValue[2];
    
    Evaluatable eval = (Evaluatable)iterator.next();
    EvaluationResult result = eval.evaluate(context);
    if (result.indeterminate()) {
      return result;
    }
    args[0] = result.getAttributeValue();
    
    eval = (Evaluatable)iterator.next();
    result = eval.evaluate(context);
    if (result.indeterminate()) {
      return result;
    }
    args[1] = result.getAttributeValue();
    
    result = null;
    switch (functionId)
    {
    case 0: 
      result = any(args[0], (BagAttribute)args[1], function, context, false);
      break;
    case 1: 
      result = all(args[0], (BagAttribute)args[1], function, context);
      break;
    case 2: 
      result = new EvaluationResult(BooleanAttribute.getInstance(false));
      Iterator it = ((BagAttribute)args[0]).iterator();
      BagAttribute bag = (BagAttribute)args[1];
      while (it.hasNext())
      {
        AttributeValue value = (AttributeValue)it.next();
        result = any(value, bag, function, context, false);
        if (result.indeterminate()) {
          return result;
        }
        if (((BooleanAttribute)result.getAttributeValue()).getValue()) {
          break;
        }
      }
      break;
    case 3: 
      result = allOfAny((BagAttribute)args[1], (BagAttribute)args[0], function, context);
      break;
    case 4: 
      result = anyOfAll((BagAttribute)args[0], (BagAttribute)args[1], function, context);
      break;
    case 5: 
      result = new EvaluationResult(BooleanAttribute.getInstance(true));
      Iterator it1 = ((BagAttribute)args[0]).iterator();
      BagAttribute bag1 = (BagAttribute)args[1];
      while (it1.hasNext())
      {
        AttributeValue value = (AttributeValue)it1.next();
        result = all(value, bag1, function, context);
        if (result.indeterminate()) {
          return result;
        }
        if (!((BooleanAttribute)result.getAttributeValue()).getValue()) {
          break;
        }
      }
    }
    return result;
  }
  
  public void checkInputs(List inputs)
    throws IllegalArgumentException
  {
    Object[] list = inputs.toArray();
    if (list.length != 3) {
      throw new IllegalArgumentException("requires three inputs");
    }
    Function function = null;
    if ((list[0] instanceof Function))
    {
      function = (Function)list[0];
    }
    else if ((list[0] instanceof VariableReference))
    {
      Expression xpr = ((VariableReference)list[0]).getReferencedDefinition()
        .getExpression();
      if ((xpr instanceof Function)) {
        function = (Function)xpr;
      }
    }
    if (function == null) {
      throw new IllegalArgumentException("first arg to higher-order  function must be a function");
    }
    if (!function.getReturnType().toString().equals("http://www.w3.org/2001/XMLSchema#boolean")) {
      throw new IllegalArgumentException("higher-order function must use a boolean function");
    }
    Evaluatable eval1 = (Evaluatable)list[1];
    Evaluatable eval2 = (Evaluatable)list[2];
    if ((secondIsBag) && (!eval1.returnsBag())) {
      throw new IllegalArgumentException("first arg has to be a bag");
    }
    if (!eval2.returnsBag()) {
      throw new IllegalArgumentException("second arg has to be a bag");
    }
    List args = new ArrayList();
    args.add(eval1);
    args.add(eval2);
    function.checkInputsNoBag(args);
  }
  
  public void checkInputsNoBag(List inputs)
    throws IllegalArgumentException
  {
    throw new IllegalArgumentException("higher-order functions require use of bags");
  }
  
  private EvaluationResult any(AttributeValue value, BagAttribute bag, Function function, EvaluationCtx context, boolean argumentsAreSwapped)
  {
    return anyAndAllHelper(value, bag, function, context, false, argumentsAreSwapped);
  }
  
  private EvaluationResult all(AttributeValue value, BagAttribute bag, Function function, EvaluationCtx context)
  {
    return anyAndAllHelper(value, bag, function, context, true, false);
  }
  
  private EvaluationResult anyAndAllHelper(AttributeValue value, BagAttribute bag, Function function, EvaluationCtx context, boolean allFunction, boolean argumentsAreSwapped)
  {
    BooleanAttribute attr = BooleanAttribute.getInstance(allFunction);
    Iterator it = bag.iterator();
    while (it.hasNext())
    {
      List params = new ArrayList();
      if (!argumentsAreSwapped)
      {
        params.add(value);
        params.add((AttributeValue)it.next());
      }
      else
      {
        params.add((AttributeValue)it.next());
        params.add(value);
      }
      EvaluationResult result = function.evaluate(params, context);
      if (result.indeterminate()) {
        return result;
      }
      BooleanAttribute bool = (BooleanAttribute)result.getAttributeValue();
      if (bool.getValue() != allFunction)
      {
        attr = bool;
        break;
      }
    }
    return new EvaluationResult(attr);
  }
  
  private EvaluationResult anyOfAll(BagAttribute anyBag, BagAttribute allBag, Function function, EvaluationCtx context)
  {
    return allAnyHelper(anyBag, allBag, function, context, true);
  }
  
  private EvaluationResult allOfAny(BagAttribute anyBag, BagAttribute allBag, Function function, EvaluationCtx context)
  {
    return allAnyHelper(anyBag, allBag, function, context, false);
  }
  
  private EvaluationResult allAnyHelper(BagAttribute anyBag, BagAttribute allBag, Function function, EvaluationCtx context, boolean argumentsAreSwapped)
  {
    Iterator it = allBag.iterator();
    while (it.hasNext())
    {
      AttributeValue value = (AttributeValue)it.next();
      EvaluationResult result = any(value, anyBag, function, context, argumentsAreSwapped);
      if (result.indeterminate()) {
        return result;
      }
      if (!((BooleanAttribute)result.getAttributeValue()).getValue()) {
        return result;
      }
    }
    return new EvaluationResult(BooleanAttribute.getTrueInstance());
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    out.println(indenter.makeString() + "<Function FunctionId=\"" + getIdentifier().toString() + 
      "\"/>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.HigherOrderFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
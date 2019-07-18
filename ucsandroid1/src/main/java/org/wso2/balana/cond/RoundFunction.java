package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class RoundFunction
  extends FunctionBase
{
  public static final String NAME_ROUND = "urn:oasis:names:tc:xacml:1.0:function:round";
  
  public RoundFunction(String functionName)
  {
    super("urn:oasis:names:tc:xacml:1.0:function:round", 0, "http://www.w3.org/2001/XMLSchema#double", false, 1, "http://www.w3.org/2001/XMLSchema#double", false);
    if (!functionName.equals("urn:oasis:names:tc:xacml:1.0:function:round")) {
      throw new IllegalArgumentException("unknown round function: " + functionName);
    }
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:round");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    double arg = ((DoubleAttribute)argValues[0]).getValue();
    double roundValue = Math.round(arg);
    
    double lower = Math.floor(arg);
    double higher = lower + 1.0D;
    if (arg - lower == higher - arg) {
      if (lower % 2.0D == 0.0D) {
        roundValue = lower;
      } else {
        roundValue = higher;
      }
    }
    return new EvaluationResult(new DoubleAttribute(roundValue));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.RoundFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
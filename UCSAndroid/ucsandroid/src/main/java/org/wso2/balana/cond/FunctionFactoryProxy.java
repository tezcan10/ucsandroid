package org.wso2.balana.cond;

public abstract interface FunctionFactoryProxy
{
  public abstract FunctionFactory getTargetFactory();
  
  public abstract FunctionFactory getConditionFactory();
  
  public abstract FunctionFactory getGeneralFactory();
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.FunctionFactoryProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
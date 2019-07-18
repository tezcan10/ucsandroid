package org.wso2.balana.cond;

public class BasicFunctionFactoryProxy
  implements FunctionFactoryProxy
{
  private FunctionFactory targetFactory;
  private FunctionFactory conditionFactory;
  private FunctionFactory generalFactory;
  
  public BasicFunctionFactoryProxy(FunctionFactory targetFactory, FunctionFactory conditionFactory, FunctionFactory generalFactory)
  {
    this.targetFactory = targetFactory;
    this.conditionFactory = conditionFactory;
    this.generalFactory = generalFactory;
  }
  
  public FunctionFactory getTargetFactory()
  {
    return targetFactory;
  }
  
  public FunctionFactory getConditionFactory()
  {
    return conditionFactory;
  }
  
  public FunctionFactory getGeneralFactory()
  {
    return generalFactory;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.BasicFunctionFactoryProxy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
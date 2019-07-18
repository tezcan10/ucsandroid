package org.wso2.balana.cond;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.cond.cluster.AbsFunctionCluster;
import org.wso2.balana.cond.cluster.AddFunctionCluster;
import org.wso2.balana.cond.cluster.ComparisonFunctionCluster;
import org.wso2.balana.cond.cluster.ConditionBagFunctionCluster;
import org.wso2.balana.cond.cluster.ConditionSetFunctionCluster;
import org.wso2.balana.cond.cluster.DateMathFunctionCluster;
import org.wso2.balana.cond.cluster.DivideFunctionCluster;
import org.wso2.balana.cond.cluster.EqualFunctionCluster;
import org.wso2.balana.cond.cluster.FloorFunctionCluster;
import org.wso2.balana.cond.cluster.GeneralBagFunctionCluster;
import org.wso2.balana.cond.cluster.GeneralSetFunctionCluster;
import org.wso2.balana.cond.cluster.HigherOrderFunctionCluster;
import org.wso2.balana.cond.cluster.LogicalFunctionCluster;
import org.wso2.balana.cond.cluster.MatchFunctionCluster;
import org.wso2.balana.cond.cluster.ModFunctionCluster;
import org.wso2.balana.cond.cluster.MultiplyFunctionCluster;
import org.wso2.balana.cond.cluster.NOfFunctionCluster;
import org.wso2.balana.cond.cluster.NotFunctionCluster;
import org.wso2.balana.cond.cluster.NumericConvertFunctionCluster;
import org.wso2.balana.cond.cluster.RoundFunctionCluster;
import org.wso2.balana.cond.cluster.StringFunctionCluster;
import org.wso2.balana.cond.cluster.StringNormalizeFunctionCluster;
import org.wso2.balana.cond.cluster.SubtractFunctionCluster;
import org.wso2.balana.cond.cluster.xacml3.StringComparingFunctionCluster;
import org.wso2.balana.cond.cluster.xacml3.StringConversionFunctionCluster;
import org.wso2.balana.cond.cluster.xacml3.StringCreationFunctionCluster;
import org.wso2.balana.cond.cluster.xacml3.SubStringFunctionCluster;

public class StandardFunctionFactory
  extends BaseFunctionFactory
{
  private static volatile StandardFunctionFactory targetFactory = null;
  private static volatile StandardFunctionFactory conditionFactory = null;
  private static volatile StandardFunctionFactory generalFactory = null;
  private static Set<Function> targetFunctions = null;
  private static Set<Function> conditionFunctions = null;
  private static Set<Function> generalFunctions = null;
  private static Map<URI, FunctionProxy> targetAbstractFunctions = null;
  private static Map<URI, FunctionProxy> conditionAbstractFunctions = null;
  private static Map<URI, FunctionProxy> generalAbstractFunctions = null;
  private static Set supportedV1Functions;
  private static Set supportedV2Functions;
  private Set supportedFunctions = null;
  private Map supportedAbstractFunctions = null;
  private static Log logger = LogFactory.getLog(StandardFunctionFactory.class);
  
  private StandardFunctionFactory(Set supportedFunctions, Map supportedAbstractFunctions)
  {
    super(supportedFunctions, supportedAbstractFunctions);
    
    this.supportedFunctions = supportedFunctions;
    this.supportedAbstractFunctions = supportedAbstractFunctions;
  }
  
  private static void initTargetFunctions()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Initializing standard Target functions");
    }
    targetFunctions = new HashSet();
    
    targetFunctions.addAll(new EqualFunctionCluster().getSupportedFunctions());
    
    targetFunctions.addAll(new LogicalFunctionCluster().getSupportedFunctions());
    
    targetFunctions.addAll(new NOfFunctionCluster().getSupportedFunctions());
    
    targetFunctions.addAll(new NotFunctionCluster().getSupportedFunctions());
    
    targetFunctions.addAll(new ComparisonFunctionCluster().getSupportedFunctions());
    
    targetFunctions.addAll(new MatchFunctionCluster().getSupportedFunctions());
    
    targetAbstractFunctions = new HashMap();
  }
  
  private static void initConditionFunctions()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Initializing standard Condition functions");
    }
    if (targetFunctions == null) {
      initTargetFunctions();
    }
    conditionFunctions = new HashSet(targetFunctions);
    
    conditionFunctions.add(new TimeInRangeFunction());
    
    conditionFunctions.addAll(new ConditionBagFunctionCluster().getSupportedFunctions());
    
    conditionFunctions.addAll(new ConditionSetFunctionCluster().getSupportedFunctions());
    
    conditionFunctions.addAll(new HigherOrderFunctionCluster().getSupportedFunctions());
    
    conditionAbstractFunctions = new HashMap(targetAbstractFunctions);
  }
  
  private static void initGeneralFunctions()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Initializing standard General functions");
    }
    if (conditionFunctions == null) {
      initConditionFunctions();
    }
    generalFunctions = new HashSet(conditionFunctions);
    
    generalFunctions.addAll(new AddFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new SubtractFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new MultiplyFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new DivideFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new ModFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new AbsFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new RoundFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new FloorFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new DateMathFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new GeneralBagFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new NumericConvertFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new StringNormalizeFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new GeneralSetFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new StringFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new StringComparingFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new StringConversionFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new SubStringFunctionCluster().getSupportedFunctions());
    
    generalFunctions.addAll(new StringCreationFunctionCluster().getSupportedFunctions());
    
    generalAbstractFunctions = new HashMap(conditionAbstractFunctions);
    try
    {
      generalAbstractFunctions.put(new URI("urn:oasis:names:tc:xacml:1.0:function:map"), new MapFunctionProxy());
    }
    catch (URISyntaxException e)
    {
      throw new IllegalArgumentException("invalid function name");
    }
  }
  
  public static StandardFunctionFactory getTargetFactory()
  {
    if (targetFactory == null) {
      synchronized (StandardFunctionFactory.class)
      {
        if (targetFunctions == null) {
          initTargetFunctions();
        }
        if (targetFactory == null) {
          targetFactory = new StandardFunctionFactory(targetFunctions, 
            targetAbstractFunctions);
        }
      }
    }
    return targetFactory;
  }
  
  public static StandardFunctionFactory getConditionFactory()
  {
    if (conditionFactory == null) {
      synchronized (StandardFunctionFactory.class)
      {
        if (conditionFunctions == null) {
          initConditionFunctions();
        }
        if (conditionFactory == null) {
          conditionFactory = new StandardFunctionFactory(conditionFunctions, 
            conditionAbstractFunctions);
        }
      }
    }
    return conditionFactory;
  }
  
  public static StandardFunctionFactory getGeneralFactory()
  {
    if (generalFactory == null) {
      synchronized (StandardFunctionFactory.class)
      {
        if (generalFunctions == null)
        {
          initGeneralFunctions();
          generalFactory = new StandardFunctionFactory(generalFunctions, 
            generalAbstractFunctions);
        }
      }
    }
    return generalFactory;
  }
  
  public static Set getStandardFunctions(String xacmlVersion)
  {
    throw new RuntimeException("This method isn't implemented yet.");
  }
  
  public static Map getStandardAbstractFunctions(String xacmlVersion)
  {
    throw new RuntimeException("This method isn't implemented yet.");
  }
  
  public static FunctionFactoryProxy getNewFactoryProxy()
  {
    getGeneralFactory();
    
    FunctionFactory newGeneral = new BaseFunctionFactory(generalFunctions, 
      generalAbstractFunctions);
    
    FunctionFactory newCondition = new BaseFunctionFactory(newGeneral, conditionFunctions, 
      conditionAbstractFunctions);
    
    FunctionFactory newTarget = new BaseFunctionFactory(newCondition, targetFunctions, 
      targetAbstractFunctions);
    
    return new BasicFunctionFactoryProxy(newTarget, newCondition, newGeneral);
  }
  
  public void addFunction(Function function)
    throws IllegalArgumentException
  {
    throw new UnsupportedOperationException("a standard factory cannot support new functions");
  }
  
  public void addAbstractFunction(FunctionProxy proxy, URI identity)
    throws IllegalArgumentException
  {
    throw new UnsupportedOperationException("a standard factory cannot support new functions");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.StandardFunctionFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
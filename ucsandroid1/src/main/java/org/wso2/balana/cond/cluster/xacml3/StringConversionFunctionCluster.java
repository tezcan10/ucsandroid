package org.wso2.balana.cond.cluster.xacml3;

import java.util.HashSet;
import java.util.Set;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.cluster.FunctionCluster;
import org.wso2.balana.cond.xacml3.StringConversionFunction;

public class StringConversionFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    for (String identifier : StringConversionFunction.getSupportedIdentifiers()) {
      set.add(new StringConversionFunction(identifier));
    }
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.xacml3.StringConversionFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
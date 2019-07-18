package org.wso2.balana.cond.cluster.xacml3;

import java.util.HashSet;
import java.util.Set;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.cluster.FunctionCluster;
import org.wso2.balana.cond.xacml3.StringComparingFunction;

public class StringComparingFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    for (String identifier : StringComparingFunction.getSupportedIdentifiers()) {
      set.add(new StringComparingFunction(identifier));
    }
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.xacml3.StringComparingFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
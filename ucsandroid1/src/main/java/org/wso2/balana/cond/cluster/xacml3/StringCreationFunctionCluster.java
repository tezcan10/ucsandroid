package org.wso2.balana.cond.cluster.xacml3;

import java.util.HashSet;
import java.util.Set;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.cluster.FunctionCluster;
import org.wso2.balana.cond.xacml3.StringCreationFunction;

public class StringCreationFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    for (String identifier : StringCreationFunction.getSupportedIdentifiers()) {
      set.add(new StringCreationFunction(identifier));
    }
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.xacml3.StringCreationFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond.cluster;

import java.util.Set;
import org.wso2.balana.cond.Function;

public abstract interface FunctionCluster
{
  public abstract Set<Function> getSupportedFunctions();
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.FunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
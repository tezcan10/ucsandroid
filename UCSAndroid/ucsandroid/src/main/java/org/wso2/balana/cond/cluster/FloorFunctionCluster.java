package org.wso2.balana.cond.cluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.wso2.balana.cond.FloorFunction;
import org.wso2.balana.cond.Function;

public class FloorFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    Iterator it = FloorFunction.getSupportedIdentifiers().iterator();
    while (it.hasNext()) {
      set.add(new FloorFunction((String)it.next()));
    }
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.FloorFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
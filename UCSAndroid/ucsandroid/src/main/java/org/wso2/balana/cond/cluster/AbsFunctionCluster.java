package org.wso2.balana.cond.cluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.wso2.balana.cond.AbsFunction;
import org.wso2.balana.cond.Function;

public class AbsFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    Iterator it = AbsFunction.getSupportedIdentifiers().iterator();
    while (it.hasNext()) {
      set.add(new AbsFunction((String)it.next()));
    }
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.AbsFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
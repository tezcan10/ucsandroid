package org.wso2.balana.cond.cluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.GeneralSetFunction;

public class GeneralSetFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    Iterator it = GeneralSetFunction.getSupportedIdentifiers().iterator();
    while (it.hasNext()) {
      set.add(new GeneralSetFunction((String)it.next()));
    }
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.GeneralSetFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
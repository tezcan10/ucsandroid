package org.wso2.balana.cond.cluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.StringFunction;
import org.wso2.balana.cond.URLStringCatFunction;

public class StringFunctionCluster
  implements FunctionCluster
{
  public Set<Function> getSupportedFunctions()
  {
    Set<Function> set = new HashSet();
    Iterator it = StringFunction.getSupportedIdentifiers()
      .iterator();
    while (it.hasNext()) {
      set.add(new StringFunction((String)it.next()));
    }
    set.add(new URLStringCatFunction());
    
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.cluster.StringFunctionCluster
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
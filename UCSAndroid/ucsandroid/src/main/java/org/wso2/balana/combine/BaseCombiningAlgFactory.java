package org.wso2.balana.combine;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.wso2.balana.UnknownIdentifierException;

public class BaseCombiningAlgFactory
  extends CombiningAlgFactory
{
  private HashMap algMap;
  
  public BaseCombiningAlgFactory()
  {
    algMap = new HashMap();
  }
  
  public BaseCombiningAlgFactory(Set algorithms)
  {
    algMap = new HashMap();
    
    Iterator it = algorithms.iterator();
    while (it.hasNext()) {
      try
      {
        CombiningAlgorithm alg = (CombiningAlgorithm)it.next();
        algMap.put(alg.getIdentifier().toString(), alg);
      }
      catch (ClassCastException cce)
      {
        throw new IllegalArgumentException("an element of the set was not an instance of CombiningAlgorithm");
      }
    }
  }
  
  public void addAlgorithm(CombiningAlgorithm alg)
  {
    String algId = alg.getIdentifier().toString();
    if (algMap.containsKey(algId)) {
      throw new IllegalArgumentException("algorithm already registered: " + algId);
    }
    algMap.put(algId, alg);
  }
  
  public Set getSupportedAlgorithms()
  {
    return Collections.unmodifiableSet(algMap.keySet());
  }
  
  public CombiningAlgorithm createAlgorithm(URI algId)
    throws UnknownIdentifierException
  {
    String id = algId.toString();
    if (algMap.containsKey(id)) {
      return (CombiningAlgorithm)algMap.get(algId.toString());
    }
    throw new UnknownIdentifierException("unknown combining algId: " + id);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.BaseCombiningAlgFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
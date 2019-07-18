package org.wso2.balana.finder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;

public class ResourceFinderResult
{
  private Set<AttributeValue> resources;
  private Map failures;
  private boolean empty;
  
  public ResourceFinderResult()
  {
    resources = Collections.unmodifiableSet(new HashSet());
    failures = Collections.unmodifiableMap(new HashMap());
    empty = true;
  }
  
  public ResourceFinderResult(Set resources)
  {
    this(resources, new HashMap());
  }
  
  public ResourceFinderResult(HashMap failures)
  {
    this(new HashSet(), failures);
  }
  
  public ResourceFinderResult(Set resources, Map failures)
  {
    this.resources = Collections.unmodifiableSet(new HashSet(resources));
    this.failures = Collections.unmodifiableMap(new HashMap(failures));
    empty = false;
  }
  
  public boolean isEmpty()
  {
    return empty;
  }
  
  public Set<AttributeValue> getResources()
  {
    return resources;
  }
  
  public Map getFailures()
  {
    return failures;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.ResourceFinderResult
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
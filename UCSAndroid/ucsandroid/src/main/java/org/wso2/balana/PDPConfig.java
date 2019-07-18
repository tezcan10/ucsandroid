package org.wso2.balana;

import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.ResourceFinder;

public class PDPConfig
{
  private AttributeFinder attributeFinder;
  private PolicyFinder policyFinder;
  private ResourceFinder resourceFinder;
  private boolean multipleRequestHandle;
  
  public PDPConfig(AttributeFinder attributeFinder, PolicyFinder policyFinder, ResourceFinder resourceFinder)
  {
    this(attributeFinder, policyFinder, resourceFinder, true);
  }
  
  public PDPConfig(AttributeFinder attributeFinder, PolicyFinder policyFinder, ResourceFinder resourceFinder, boolean multipleRequestHandle)
  {
    if (attributeFinder != null) {
      this.attributeFinder = attributeFinder;
    } else {
      this.attributeFinder = new AttributeFinder();
    }
    if (policyFinder != null) {
      this.policyFinder = policyFinder;
    } else {
      this.policyFinder = new PolicyFinder();
    }
    if (resourceFinder != null) {
      this.resourceFinder = resourceFinder;
    } else {
      this.resourceFinder = new ResourceFinder();
    }
    this.multipleRequestHandle = multipleRequestHandle;
  }
  
  public AttributeFinder getAttributeFinder()
  {
    return attributeFinder;
  }
  
  public PolicyFinder getPolicyFinder()
  {
    return policyFinder;
  }
  
  public ResourceFinder getResourceFinder()
  {
    return resourceFinder;
  }
  
  public boolean isMultipleRequestHandle()
  {
    return multipleRequestHandle;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.PDPConfig
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
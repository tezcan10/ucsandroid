package org.wso2.balana.attr;

import java.net.URI;
import org.wso2.balana.cond.Evaluatable;

public abstract class AbstractAttributeSelector
  implements Evaluatable
{
  protected URI type;
  protected boolean mustBePresent;
  protected String xpathVersion;
  
  public URI getType()
  {
    return type;
  }
  
  public boolean isMustBePresent()
  {
    return mustBePresent;
  }
  
  public String getXPathVersion()
  {
    return xpathVersion;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AbstractAttributeSelector
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
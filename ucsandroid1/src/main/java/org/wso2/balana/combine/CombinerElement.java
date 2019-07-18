package org.wso2.balana.combine;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.wso2.balana.Indenter;
import org.wso2.balana.PolicyTreeElement;

public abstract class CombinerElement
{
  private PolicyTreeElement element;
  private List parameters;
  
  public CombinerElement(PolicyTreeElement element)
  {
    this(element, null);
  }
  
  public CombinerElement(PolicyTreeElement element, List parameters)
  {
    this.element = element;
    if (parameters == null) {
      this.parameters = Collections.unmodifiableList(new ArrayList());
    } else {
      this.parameters = Collections.unmodifiableList(new ArrayList(parameters));
    }
  }
  
  public PolicyTreeElement getElement()
  {
    return element;
  }
  
  public List getParameters()
  {
    return parameters;
  }
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.CombinerElement
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
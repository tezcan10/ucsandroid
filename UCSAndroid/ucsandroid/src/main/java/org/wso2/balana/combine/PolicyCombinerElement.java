package org.wso2.balana.combine;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Indenter;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.PolicySet;

public class PolicyCombinerElement
  extends CombinerElement
{
  public PolicyCombinerElement(AbstractPolicy policy)
  {
    super(policy);
  }
  
  public PolicyCombinerElement(AbstractPolicy policy, List parameters)
  {
    super(policy, parameters);
  }
  
  public AbstractPolicy getPolicy()
  {
    return (AbstractPolicy)getElement();
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    if (!getParameters().isEmpty())
    {
      AbstractPolicy policy = getPolicy();
      if ((policy instanceof Policy))
      {
        encodeParamaters(output, indenter, "Policy", policy.getId().toString());
      }
      else if ((policy instanceof PolicySet))
      {
        encodeParamaters(output, indenter, "PolicySet", policy.getId().toString());
      }
      else
      {
        PolicyReference ref = (PolicyReference)policy;
        if (ref.getReferenceType() == 0) {
          encodeParamaters(output, indenter, "Policy", ref.getReference().toString());
        } else {
          encodeParamaters(output, indenter, "PolicySet", ref.getReference().toString());
        }
      }
    }
    getPolicy().encode(output, indenter);
  }
  
  private void encodeParamaters(OutputStream output, Indenter indenter, String prefix, String id)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    Iterator it = getParameters().iterator();
    
    out.println(indent + "<" + prefix + "CombinerParameters " + prefix + "IdRef=\"" + id + 
      "\">");
    indenter.in();
    while (it.hasNext())
    {
      CombinerParameter param = (CombinerParameter)it.next();
      param.encode(output, indenter);
    }
    out.println(indent + "</" + prefix + "CombinerParameters>");
    indenter.out();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.PolicyCombinerElement
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
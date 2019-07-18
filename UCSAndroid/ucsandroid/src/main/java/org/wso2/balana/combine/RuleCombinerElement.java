package org.wso2.balana.combine;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.Indenter;
import org.wso2.balana.Rule;

public class RuleCombinerElement
  extends CombinerElement
{
  public RuleCombinerElement(Rule rule)
  {
    super(rule);
  }
  
  public RuleCombinerElement(Rule rule, List parameters)
  {
    super(rule, parameters);
  }
  
  public Rule getRule()
  {
    return (Rule)getElement();
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    Iterator it = getParameters().iterator();
    if (it.hasNext())
    {
      PrintStream out = new PrintStream(output);
      String indent = indenter.makeString();
      
      out.println(indent + "<RuleCombinerParameters RuleIdRef=\"" + getRule().getId() + "\">");
      indenter.in();
      while (it.hasNext())
      {
        CombinerParameter param = (CombinerParameter)it.next();
        param.encode(output, indenter);
      }
      out.println(indent + "</RuleCombinerParameters>");
      indenter.out();
    }
    getRule().encode(output, indenter);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.RuleCombinerElement
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
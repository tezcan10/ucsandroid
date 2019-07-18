package org.wso2.balana.attr;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.wso2.balana.Indenter;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;

public abstract class AttributeValue
  implements Evaluatable
{
  private URI type;
  
  protected AttributeValue(URI type)
  {
    this.type = type;
  }
  
  public URI getType()
  {
    return type;
  }
  
  public final boolean returnsBag()
  {
    return isBag();
  }
  
  /**
   * @deprecated
   */
  public final boolean evaluatesToBag()
  {
    return isBag();
  }
  
  public List getChildren()
  {
    return Collections.EMPTY_LIST;
  }
  
  public boolean isBag()
  {
    return false;
  }
  
  public EvaluationResult evaluate(EvaluationCtx context)
  {
    return new EvaluationResult(this);
  }
  
  public abstract String encode();
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    out.println(indenter.makeString() + encodeWithTags(true));
  }
  
  public String encodeWithTags(boolean includeType)
  {
    if ((includeType) && (type != null)) {
      return 
        "<AttributeValue DataType=\"" + type.toString() + "\">" + encode() + "</AttributeValue>";
    }
    return "<AttributeValue>" + encode() + "</AttributeValue>";
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeValue
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.cond;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.wso2.balana.attr.AnyURIAttribute;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class URLStringCatFunction
  extends FunctionBase
{
  public static final String NAME_URI_STRING_CONCATENATE = "urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate";
  
  public URLStringCatFunction()
  {
    super("urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate", 0, "http://www.w3.org/2001/XMLSchema#anyURI", false);
  }
  
  public void checkInputs(List inputs)
    throws IllegalArgumentException
  {
    Iterator it = inputs.iterator();
    while (it.hasNext()) {
      if (((Expression)it.next()).returnsBag()) {
        throw new IllegalArgumentException("urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate doesn't accept bags");
      }
    }
    checkInputsNoBag(inputs);
  }
  
  public void checkInputsNoBag(List inputs)
    throws IllegalArgumentException
  {
    if (inputs.size() < 2) {
      throw new IllegalArgumentException("not enough args to urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate");
    }
    Iterator it = inputs.iterator();
    if (!((Expression)it.next()).getType().toString().equals("http://www.w3.org/2001/XMLSchema#anyURI")) {
      throw new IllegalArgumentException("illegal parameter");
    }
    while (it.hasNext()) {
      if (!((Expression)it.next()).getType().toString().equals("http://www.w3.org/2001/XMLSchema#string")) {
        throw new IllegalArgumentException("illegal parameter");
      }
    }
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    String str = ((AnyURIAttribute)argValues[0]).getValue().toString();
    
    StringBuffer buffer = new StringBuffer(str);
    for (int i = 1; i < argValues.length; i++) {
      buffer.append(((StringAttribute)argValues[i]).getValue());
    }
    try
    {
      return new EvaluationResult(new AnyURIAttribute(new URI(str)));
    }
    catch (URISyntaxException use)
    {
      List code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      String message = "urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate didn't produce a valid URI: " + 
        str;
      
      return new EvaluationResult(new Status(code, message));
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.URLStringCatFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
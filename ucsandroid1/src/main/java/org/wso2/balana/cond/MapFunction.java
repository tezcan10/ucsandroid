package org.wso2.balana.cond;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

class MapFunction
  implements Function
{
  public static final String NAME_MAP = "urn:oasis:names:tc:xacml:1.0:function:map";
  private URI returnType;
  private static URI identifier;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifier = new URI("urn:oasis:names:tc:xacml:1.0:function:map");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(e);
    }
  }
  
  public MapFunction(URI returnType)
  {
    this.returnType = returnType;
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:map");
    
    return set;
  }
  
  public static MapFunction getInstance(Node root)
    throws ParsingException
  {
    URI returnType = null;
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Function"))
      {
        String funcName = node.getAttributes().getNamedItem("FunctionId").getNodeValue();
        FunctionFactory factory = FunctionFactory.getGeneralInstance();
        try
        {
          Function function = factory.createFunction(funcName);
          returnType = function.getReturnType();
        }
        catch (FunctionTypeException fte)
        {
          try
          {
            Function function = factory.createAbstractFunction(funcName, root);
            returnType = function.getReturnType();
          }
          catch (Exception e)
          {
            throw new ParsingException("invalid abstract map", e);
          }
        }
        catch (Exception e)
        {
          throw new ParsingException("couldn't parse map body", e);
        }
      }
    }
    if (returnType == null) {
      throw new ParsingException("couldn't find the return type");
    }
    return new MapFunction(returnType);
  }
  
  public URI getIdentifier()
  {
    if (earlyException != null) {
      throw earlyException;
    }
    return identifier;
  }
  
  public URI getType()
  {
    return getReturnType();
  }
  
  public URI getReturnType()
  {
    return returnType;
  }
  
  public boolean returnsBag()
  {
    return true;
  }
  
  private static EvaluationResult makeProcessingError(String message)
  {
    ArrayList code = new ArrayList();
    code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
    return new EvaluationResult(new Status(code, message));
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    Iterator iterator = inputs.iterator();
    Function function = null;
    
    Expression xpr = (Expression)iterator.next();
    if ((xpr instanceof Function)) {
      function = (Function)xpr;
    } else {
      function = (Function)((VariableReference)xpr).getReferencedDefinition()
        .getExpression();
    }
    Evaluatable eval = (Evaluatable)iterator.next();
    EvaluationResult result = eval.evaluate(context);
    if (result.indeterminate()) {
      return result;
    }
    BagAttribute bag = (BagAttribute)result.getAttributeValue();
    
    Iterator it = bag.iterator();
    List outputs = new ArrayList();
    while (it.hasNext())
    {
      List params = new ArrayList();
      params.add(it.next());
      result = function.evaluate(params, context);
      if (result.indeterminate()) {
        return result;
      }
      outputs.add(result.getAttributeValue());
    }
    return new EvaluationResult(new BagAttribute(returnType, outputs));
  }
  
  public void checkInputs(List inputs)
    throws IllegalArgumentException
  {
    Object[] list = inputs.toArray();
    if (list.length != 2) {
      throw new IllegalArgumentException("map requires two inputs");
    }
    Function function = null;
    if ((list[0] instanceof Function))
    {
      function = (Function)list[0];
    }
    else if ((list[0] instanceof VariableReference))
    {
      Expression xpr = ((VariableReference)list[0]).getReferencedDefinition()
        .getExpression();
      if ((xpr instanceof Function)) {
        function = (Function)xpr;
      }
    }
    if (function == null) {
      throw new IllegalArgumentException("first argument to map must be a Function");
    }
    Evaluatable eval = (Evaluatable)list[1];
    if (!eval.returnsBag()) {
      throw new IllegalArgumentException("second argument to map must be a bag");
    }
    List input = new ArrayList();
    input.add(list[1]);
    function.checkInputsNoBag(input);
  }
  
  public void checkInputsNoBag(List inputs)
    throws IllegalArgumentException
  {
    throw new IllegalArgumentException("map requires a bag");
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    out.println(indenter.makeString() + "<Function FunctionId=\"" + "urn:oasis:names:tc:xacml:1.0:function:map" + "\"/>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.MapFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
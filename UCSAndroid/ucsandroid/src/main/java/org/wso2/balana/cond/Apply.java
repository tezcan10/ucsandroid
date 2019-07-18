package org.wso2.balana.cond;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ctx.EvaluationCtx;

public class Apply
  implements Evaluatable
{
  private Function function;
  private List xprs;
  
  public Apply(Function function, List xprs)
    throws IllegalArgumentException
  {
    function.checkInputs(xprs);
    
    this.function = function;
    this.xprs = Collections.unmodifiableList(new ArrayList(xprs));
  }
  
  /**
   * @deprecated
   */
  public Apply(Function function, List xprs, boolean isCondition)
    throws IllegalArgumentException
  {
    if (isCondition) {
      throw new IllegalArgumentException("As of version 2.0 an Apply may not represent a Condition");
    }
    function.checkInputs(xprs);
    
    this.function = function;
    this.xprs = Collections.unmodifiableList(new ArrayList(xprs));
  }
  
  public static Apply getConditionInstance(Node root, String xpathVersion, VariableManager manager)
    throws ParsingException
  {
    return getInstance(root, FunctionFactory.getConditionInstance(), new PolicyMetaData(
      "urn:oasis:names:tc:xacml:1.0:policy", xpathVersion), manager);
  }
  
  /**
   * @deprecated
   */
  public static Apply getConditionInstance(Node root, String xpathVersion)
    throws ParsingException
  {
    return getInstance(root, FunctionFactory.getConditionInstance(), new PolicyMetaData(
      "urn:oasis:names:tc:xacml:1.0:policy", xpathVersion), null);
  }
  
  public static Apply getInstance(Node root, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    return getInstance(root, FunctionFactory.getGeneralInstance(), metaData, manager);
  }
  
  /**
   * @deprecated
   */
  public static Apply getInstance(Node root, String xpathVersion)
    throws ParsingException
  {
    return getInstance(root, FunctionFactory.getGeneralInstance(), new PolicyMetaData(
      "urn:oasis:names:tc:xacml:1.0:policy", xpathVersion), null);
  }
  
  private static Apply getInstance(Node root, FunctionFactory factory, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    Function function = ExpressionHandler.getFunction(root, metaData, factory);
    List xprs = new ArrayList();
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Expression xpr = ExpressionHandler.parseExpression(nodes.item(i), metaData, manager);
      if (xpr != null) {
        xprs.add(xpr);
      }
    }
    return new Apply(function, xprs);
  }
  
  public Function getFunction()
  {
    return function;
  }
  
  public List getChildren()
  {
    return xprs;
  }
  
  /**
   * @deprecated
   */
  public boolean isCondition()
  {
    return false;
  }
  
  public EvaluationResult evaluate(EvaluationCtx context)
  {
    return function.evaluate(xprs, context);
  }
  
  public URI getType()
  {
    return function.getReturnType();
  }
  
  public boolean returnsBag()
  {
    return function.returnsBag();
  }
  
  /**
   * @deprecated
   */
  public boolean evaluatesToBag()
  {
    return function.returnsBag();
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<Apply FunctionId=\"" + function.getIdentifier() + "\">");
    indenter.in();
    
    Iterator it = xprs.iterator();
    while (it.hasNext())
    {
      Expression xpr = (Expression)it.next();
      xpr.encode(output, indenter);
    }
    indenter.out();
    out.println(indent + "</Apply>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.Apply
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
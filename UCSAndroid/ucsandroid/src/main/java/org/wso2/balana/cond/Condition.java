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

public class Condition
  implements Evaluatable
{
  private static URI booleanIdentifier;
  private List children;
  private Expression expression;
  private Function function;
  private boolean isVersionOne;
  
  static
  {
    try
    {
      booleanIdentifier = new URI("http://www.w3.org/2001/XMLSchema#boolean");
    }
    catch (Exception e)
    {
      booleanIdentifier = null;
    }
  }
  
  public Condition(Function function, List expressions)
    throws IllegalArgumentException
  {
    isVersionOne = true;
    
    checkExpression(function);
    
    expression = new Apply(function, expressions);
    
    this.function = function;
    children = ((Apply)expression).getChildren();
  }
  
  public Condition(Expression expression)
    throws IllegalArgumentException
  {
    isVersionOne = false;
    
    checkExpression(expression);
    
    this.expression = expression;
    
    function = null;
    
    List list = new ArrayList();
    list.add(this.expression);
    children = Collections.unmodifiableList(list);
  }
  
  private void checkExpression(Expression xpr)
  {
    if (!xpr.getType().equals(booleanIdentifier)) {
      throw new IllegalArgumentException("A Condition must return a boolean...cannot create with " + 
        xpr.getType());
    }
    if (xpr.returnsBag()) {
      throw new IllegalArgumentException("A Condition must not return a Bag");
    }
  }
  
  public static Condition getInstance(Node root, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    if (metaData.getXACMLVersion() < 2)
    {
      Apply cond = Apply.getConditionInstance(root, metaData.getXPathIdentifier(), manager);
      return new Condition(cond.getFunction(), cond.getChildren());
    }
    Expression xpr = null;
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeType() == 1)
      {
        xpr = ExpressionHandler.parseExpression(nodes.item(i), metaData, manager);
        break;
      }
    }
    return new Condition(xpr);
  }
  
  public Function getFunction()
  {
    return function;
  }
  
  public List getChildren()
  {
    return children;
  }
  
  public URI getType()
  {
    return booleanIdentifier;
  }
  
  public boolean returnsBag()
  {
    return false;
  }
  
  /**
   * @deprecated
   */
  public boolean evaluatesToBag()
  {
    return false;
  }
  
  public EvaluationResult evaluate(EvaluationCtx context)
  {
    return ((Evaluatable)expression).evaluate(context);
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    if (isVersionOne)
    {
      out.println(indent + "<Condition FunctionId=\"" + function.getIdentifier() + "\">");
      indenter.in();
      
      Iterator it = children.iterator();
      while (it.hasNext())
      {
        Expression xpr = (Expression)it.next();
        xpr.encode(output, indenter);
      }
    }
    else
    {
      out.println(indent + "<Condition>");
      indenter.in();
      
      expression.encode(output, indenter);
    }
    indenter.out();
    out.println(indent + "</Condition>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.Condition
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
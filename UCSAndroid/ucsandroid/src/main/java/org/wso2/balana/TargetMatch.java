package org.wso2.balana;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.attr.AttributeDesignatorFactory;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeSelectorFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.FunctionFactory;
import org.wso2.balana.cond.FunctionTypeException;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class TargetMatch
{
  public static final int SUBJECT = 0;
  public static final int RESOURCE = 1;
  public static final int ACTION = 2;
  public static final int ENVIRONMENT = 3;
  public static final String[] NAMES = { "Subject", "Resource", "Action", "Environment" };
  private int type;
  private Function function;
  private Evaluatable eval;
  private AttributeValue attrValue;
  
  public TargetMatch(int type, Function function, Evaluatable eval, AttributeValue attrValue)
    throws IllegalArgumentException
  {
    if ((type != 0) && (type != 1) && (type != 2) && (type != 3)) {
      throw new IllegalArgumentException("Unknown TargetMatch type");
    }
    this.type = type;
    this.function = function;
    this.eval = eval;
    this.attrValue = attrValue;
  }
  
  /**
   * @deprecated
   */
  public static TargetMatch getInstance(Node root, String prefix, String xpathVersion)
    throws ParsingException, IllegalArgumentException
  {
    int i = 0;
    while ((i < NAMES.length) && (!NAMES[i].equals(prefix))) {
      i++;
    }
    if (i == NAMES.length) {
      throw new IllegalArgumentException("Unknown TargetMatch type");
    }
    return getInstance(root, i, new PolicyMetaData("urn:oasis:names:tc:xacml:1.0:policy", 
      xpathVersion));
  }
  
  public static TargetMatch getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    return getInstance(root, 0, metaData);
  }
  
  public static TargetMatch getInstance(Node root, int matchType, PolicyMetaData metaData)
    throws ParsingException
  {
    Evaluatable eval = null;
    AttributeValue attrValue = null;
    
    AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
    
    String funcName = root.getAttributes().getNamedItem("MatchId").getNodeValue();
    FunctionFactory factory = FunctionFactory.getTargetInstance();
    try
    {
      URI funcId = new URI(funcName);
      function = factory.createFunction(funcId);
    }
    catch (URISyntaxException use)
    {
      Function function;
      throw new ParsingException("Error parsing TargetMatch", use);
    }
    catch (UnknownIdentifierException uie)
    {
      throw new ParsingException("Unknown MatchId", uie);
    }
    catch (FunctionTypeException fte)
    {
      try
      {
        URI funcId = new URI(funcName);
        function = factory.createAbstractFunction(funcId, root);
      }
      catch (Exception e)
      {
        Function function;
        throw new ParsingException("invalid abstract function", e);
      }
    }
    Function function;
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      String name = node.getNodeName();
      if ((3 == metaData.getXACMLVersion()) && 
        ("AttributeDesignator".equals(name))) {
        eval = AttributeDesignatorFactory.getFactory().getAbstractDesignator(node, metaData);
      } else if ((3 != metaData.getXACMLVersion()) && 
        ((NAMES[matchType] + "AttributeDesignator").equals(name))) {
        eval = AttributeDesignatorFactory.getFactory().getAbstractDesignator(node, metaData);
      } else if (name.equals("AttributeSelector")) {
        eval = AttributeSelectorFactory.getFactory().getAbstractSelector(node, metaData);
      } else if (name.equals("AttributeValue")) {
        try
        {
          attrValue = attrFactory.createValue(node);
        }
        catch (UnknownIdentifierException uie)
        {
          throw new ParsingException("Unknown Attribute Type", uie);
        }
      }
    }
    List<Evaluatable> inputs = new ArrayList();
    inputs.add(attrValue);
    inputs.add(eval);
    function.checkInputsNoBag(inputs);
    
    return new TargetMatch(matchType, function, eval, attrValue);
  }
  
  public int getType()
  {
    return type;
  }
  
  public Function getMatchFunction()
  {
    return function;
  }
  
  public AttributeValue getMatchValue()
  {
    return attrValue;
  }
  
  public Evaluatable getMatchEvaluatable()
  {
    return eval;
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    EvaluationResult result = eval.evaluate(context);
    if (result.indeterminate()) {
      return new MatchResult(2, result.getStatus());
    }
    BagAttribute bag = (BagAttribute)result.getAttributeValue();
    if (!bag.isEmpty())
    {
      Iterator it = bag.iterator();
      boolean atLeastOneError = false;
      Status firstIndeterminateStatus = null;
      while (it.hasNext())
      {
        ArrayList<Evaluatable> inputs = new ArrayList();
        
        inputs.add(attrValue);
        inputs.add((Evaluatable)it.next());
        
        MatchResult match = evaluateMatch(inputs, context);
        if (match.getResult() == 0)
        {
          if ((attrValue instanceof StringAttribute)) {
            match.setPolicyValue(((StringAttribute)attrValue).getValue());
          }
          return match;
        }
        if (match.getResult() == 2)
        {
          atLeastOneError = true;
          if (firstIndeterminateStatus == null) {
            firstIndeterminateStatus = match.getStatus();
          }
        }
      }
      if (atLeastOneError) {
        return new MatchResult(2, firstIndeterminateStatus);
      }
      return new MatchResult(1);
    }
    return new MatchResult(1);
  }
  
  private MatchResult evaluateMatch(List inputs, EvaluationCtx context)
  {
    EvaluationResult result = function.evaluate(inputs, context);
    if (result.indeterminate()) {
      return new MatchResult(2, result.getStatus());
    }
    BooleanAttribute bool = (BooleanAttribute)result.getAttributeValue();
    if (bool.getValue()) {
      return new MatchResult(0);
    }
    return new MatchResult(1);
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    String tagName = NAMES[type] + "Match";
    
    out.println(indent + "<" + tagName + " MatchId=\"" + function.getIdentifier().toString() + 
      "\">");
    indenter.in();
    
    attrValue.encode(output, indenter);
    eval.encode(output, indenter);
    
    indenter.out();
    out.println(indent + "</" + tagName + ">");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.TargetMatch
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
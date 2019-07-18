package org.wso2.balana;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.cond.Apply;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.VariableManager;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.xacml3.Advice;
import org.wso2.balana.xacml3.AdviceExpression;

public class Rule
  implements PolicyTreeElement
{
  private URI idAttr;
  private int effectAttr;
  private Set<AbstractObligation> obligationExpressions;
  private Set<AdviceExpression> adviceExpressions;
  private String description = null;
  private AbstractTarget target = null;
  private Condition condition = null;
  private int xacmlVersion;
  
  public Rule(URI id, int effect, String description, AbstractTarget target, Condition condition, Set<AbstractObligation> obligationExpressions, Set<AdviceExpression> adviceExpressions, int xacmlVersion)
  {
    idAttr = id;
    effectAttr = effect;
    this.description = description;
    this.target = target;
    this.condition = condition;
    this.adviceExpressions = adviceExpressions;
    this.obligationExpressions = obligationExpressions;
    this.xacmlVersion = xacmlVersion;
  }
  
  /**
   * @deprecated
   */
  public Rule(URI id, int effect, String description, AbstractTarget target, Apply condition, int xacmlVersion)
  {
    idAttr = id;
    effectAttr = effect;
    this.description = description;
    this.target = target;
    this.condition = new Condition(condition.getFunction(), condition.getChildren());
    this.xacmlVersion = xacmlVersion;
  }
  
  /**
   * @deprecated
   */
  public Rule(URI id, int effect, String description, AbstractTarget target, Condition condition)
  {
    idAttr = id;
    effectAttr = effect;
    this.description = description;
    this.target = target;
    this.condition = new Condition(condition.getFunction(), condition.getChildren());
  }
  
  /**
   * @deprecated
   */
  public static Rule getInstance(Node root, String xpathVersion)
    throws ParsingException
  {
    return getInstance(root, new PolicyMetaData("urn:oasis:names:tc:xacml:1.0:policy", 
      xpathVersion), null);
  }
  
  public static Rule getInstance(Node root, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    URI id = null;
    String name = null;
    int effect = 0;
    String description = null;
    AbstractTarget target = null;
    Condition condition = null;
    Set<AbstractObligation> obligationExpressions = new HashSet();
    Set<AdviceExpression> adviceExpressions = new HashSet();
    
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      id = new URI(attrs.getNamedItem("RuleId").getNodeValue());
    }
    catch (URISyntaxException use)
    {
      throw new ParsingException("Error parsing required attribute RuleId", use);
    }
    String str = attrs.getNamedItem("Effect").getNodeValue();
    if (str.equals("Permit")) {
      effect = 0;
    } else if (str.equals("Deny")) {
      effect = 1;
    } else {
      throw new ParsingException("Invalid Effect: " + effect);
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String cname = child.getNodeName();
      if (cname.equals("Description"))
      {
        description = child.getFirstChild().getNodeValue();
      }
      else if (cname.equals("Target"))
      {
        target = TargetFactory.getFactory().getTarget(child, metaData);
      }
      else if (cname.equals("Condition"))
      {
        condition = Condition.getInstance(child, metaData, manager);
      }
      else if ("ObligationExpressions".equals(cname))
      {
        NodeList nodes = child.getChildNodes();
        for (int j = 0; j < nodes.getLength(); j++)
        {
          Node node = nodes.item(j);
          if ("ObligationExpression".equals(node.getNodeName())) {
            obligationExpressions.add(ObligationFactory.getFactory()
              .getObligation(node, metaData));
          }
        }
      }
      else if ("AdviceExpressions".equals(cname))
      {
        NodeList nodes = child.getChildNodes();
        for (int j = 0; j < nodes.getLength(); j++)
        {
          Node node = nodes.item(j);
          if ("AdviceExpression".equals(node.getNodeName())) {
            adviceExpressions.add(AdviceExpression.getInstance(node, metaData));
          }
        }
      }
    }
    return new Rule(id, effect, description, target, condition, obligationExpressions, 
      adviceExpressions, metaData.getXACMLVersion());
  }
  
  public int getEffect()
  {
    return effectAttr;
  }
  
  public URI getId()
  {
    return idAttr;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public AbstractTarget getTarget()
  {
    return target;
  }
  
  public List getChildren()
  {
    return Collections.EMPTY_LIST;
  }
  
  public Condition getCondition()
  {
    return condition;
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    if (target == null)
    {
      ArrayList code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      Status status = new Status(code, "no target available for matching a rule");
      
      return new MatchResult(2, status);
    }
    return target.match(context);
  }
  
  public AbstractResult evaluate(EvaluationCtx context)
  {
    MatchResult match = null;
    if (target != null)
    {
      match = target.match(context);
      int result = match.getResult();
      if (result == 1) {
        return ResultFactory.getFactory().getResult(3, context);
      }
      if (result == 2)
      {
        if (xacmlVersion == 3)
        {
          if (effectAttr == 0) {
            return ResultFactory.getFactory().getResult(5, 
              match.getStatus(), context);
          }
          return ResultFactory.getFactory().getResult(4, 
            match.getStatus(), context);
        }
        return ResultFactory.getFactory().getResult(2, 
          match.getStatus(), context);
      }
    }
    if (condition == null) {
      return ResultFactory.getFactory().getResult(effectAttr, processObligations(context), 
        processAdvices(context), context);
    }
    EvaluationResult result = condition.evaluate(context);
    if (result.indeterminate())
    {
      if (xacmlVersion == 3)
      {
        if (effectAttr == 0) {
          return ResultFactory.getFactory().getResult(5, 
            result.getStatus(), context);
        }
        return ResultFactory.getFactory().getResult(4, 
          result.getStatus(), context);
      }
      return ResultFactory.getFactory().getResult(2, 
        result.getStatus(), context);
    }
    BooleanAttribute bool = (BooleanAttribute)result.getAttributeValue();
    if (bool.getValue()) {
      return ResultFactory.getFactory().getResult(effectAttr, processObligations(context), 
        processAdvices(context), context);
    }
    return ResultFactory.getFactory().getResult(3, context);
  }
  
  private List<ObligationResult> processObligations(EvaluationCtx evaluationCtx)
  {
    if ((obligationExpressions != null) && (obligationExpressions.size() > 0))
    {
      List<ObligationResult> results = new ArrayList();
      for (AbstractObligation obligationExpression : obligationExpressions) {
        if (obligationExpression.getFulfillOn() == effectAttr) {
          results.add(obligationExpression.evaluate(evaluationCtx));
        }
      }
      return results;
    }
    return null;
  }
  
  private List<Advice> processAdvices(EvaluationCtx evaluationCtx)
  {
    if ((adviceExpressions != null) && (adviceExpressions.size() > 0))
    {
      List<Advice> advices = new ArrayList();
      for (AdviceExpression adviceExpression : adviceExpressions) {
        if (adviceExpression.getAppliesTo() == effectAttr) {
          advices.add(adviceExpression.evaluate(evaluationCtx));
        }
      }
      return advices;
    }
    return null;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.print(indent + "<Rule RuleId=\"" + idAttr.toString() + "\" Effect=\"" + 
      org.wso2.balana.ctx.xacml2.Result.DECISIONS[effectAttr] + "\"");
    if ((description != null) || (target != null) || (condition != null))
    {
      out.println(">");
      
      indenter.in();
      String nextIndent = indenter.makeString();
      if (description != null) {
        out.println(nextIndent + "<Description>" + description + "</Description>");
      }
      if (condition != null) {
        condition.encode(output, indenter);
      }
      indenter.out();
      out.println(indent + "</Rule>");
    }
    else
    {
      out.println("/>");
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.Rule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
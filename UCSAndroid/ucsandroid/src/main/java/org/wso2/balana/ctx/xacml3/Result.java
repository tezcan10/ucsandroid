package org.wso2.balana.ctx.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.xacml3.Advice;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.balana.xacml3.Obligation;

public class Result
  extends AbstractResult
{
  Set<PolicyReference> policyReferences;
  Set<Attributes> attributes;
  
  public Result(int decision, Status status)
  {
    super(decision, status);
  }
  
  public Result(int decision, Status status, List<ObligationResult> obligationResults, List<Advice> advices, EvaluationCtx evaluationCtx)
    throws IllegalArgumentException
  {
    super(decision, status, obligationResults, advices);
    if (evaluationCtx != null)
    {
      XACML3EvaluationCtx ctx = (XACML3EvaluationCtx)evaluationCtx;
      policyReferences = ctx.getPolicyReferences();
      attributes = ctx.getAttributesSet();
    }
  }
  
  public Result(int decision, Status status, List<ObligationResult> obligationResults, List<Advice> advices, Set<PolicyReference> policyReferences, Set<Attributes> attributes)
    throws IllegalArgumentException
  {
    super(decision, status, obligationResults, advices);
    this.policyReferences = policyReferences;
    this.attributes = attributes;
  }
  
  public static AbstractResult getInstance(Node root)
    throws ParsingException
  {
    int decision = -1;
    Status status = null;
    List<ObligationResult> obligations = null;
    List<Advice> advices = null;
    Set<PolicyReference> policyReferences = null;
    Set<Attributes> attributes = null;
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      String name = node.getNodeName();
      if (name.equals("Decision"))
      {
        String type = node.getFirstChild().getNodeValue();
        for (int j = 0; j < DECISIONS.length; j++) {
          if (DECISIONS[j].equals(type))
          {
            decision = j;
            break;
          }
        }
        if (decision == -1) {
          throw new ParsingException("Unknown Decision: " + type);
        }
      }
      else if (name.equals("Status"))
      {
        if (status == null) {
          status = Status.getInstance(node);
        } else {
          throw new ParsingException("More than one StatusType defined");
        }
      }
      else if (name.equals("Obligations"))
      {
        if (obligations == null) {
          obligations = parseObligations(node);
        } else {
          throw new ParsingException("More than one ObligationsType defined");
        }
      }
      else if (name.equals("AssociatedAdvice"))
      {
        if (advices == null) {
          advices = parseAdvices(node);
        } else {
          throw new ParsingException("More than one AssociatedAdviceType defined");
        }
      }
      else if (name.equals("PolicyIdentifierList"))
      {
        if (policyReferences == null) {
          policyReferences = parsePolicyReferences(node);
        } else {
          throw new ParsingException("More than one PolicyIdentifierListType defined");
        }
      }
      else if (name.equals("Attributes"))
      {
        if (attributes == null) {
          attributes = new HashSet();
        }
        attributes.add(Attributes.getInstance(node));
      }
    }
    return new Result(decision, status, obligations, advices, policyReferences, attributes);
  }
  
  private static List<ObligationResult> parseObligations(Node root)
    throws ParsingException
  {
    List<ObligationResult> list = new ArrayList();
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Obligation")) {
        list.add(Obligation.getInstance(node));
      }
    }
    if (list.size() == 0) {
      throw new ParsingException("ObligationsType must not be empty");
    }
    return list;
  }
  
  private static List<Advice> parseAdvices(Node root)
    throws ParsingException
  {
    List<Advice> list = new ArrayList();
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Advice")) {
        list.add(Advice.getInstance(node));
      }
    }
    if (list.size() == 0) {
      throw new ParsingException("AssociatedAdviceType must not be empty");
    }
    return list;
  }
  
  private static Set<PolicyReference> parsePolicyReferences(Node root)
    throws ParsingException
  {
    Set<PolicyReference> set = new HashSet();
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      set.add(PolicyReference.getInstance(node, null, null));
    }
    return set;
  }
  
  public Set<Attributes> getAttributes()
  {
    return attributes;
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    indenter.in();
    String indentNext = indenter.makeString();
    out.println(indent + "<Result>");
    if ((decision == 4) || (decision == 5) || (decision == 6)) {
      out.println(indentNext + "<Decision>" + DECISIONS[2] + "</Decision>");
    } else {
      out.println(indentNext + "<Decision>" + DECISIONS[decision] + "</Decision>");
    }
    if (status != null) {
      status.encode(output, indenter);
    }
    if ((obligations != null) && (obligations.size() != 0))
    {
      out.println(indentNext + "<Obligations>");
      
      Iterator it = obligations.iterator();
      indenter.in();
      while (it.hasNext())
      {
        Obligation obligation = (Obligation)it.next();
        obligation.encode(out, indenter);
      }
      indenter.out();
      out.println(indentNext + "</Obligations>");
    }
    Advice advice;
    if ((advices != null) && (advices.size() != 0))
    {
      out.println(indentNext + "<AssociatedAdvice>");
      
      Iterator it = advices.iterator();
      indenter.in();
      while (it.hasNext())
      {
        advice = (Advice)it.next();
        advice.encode(out, indenter);
      }
      indenter.out();
      out.println(indentNext + "</AssociatedAdvice>");
    }
    if ((policyReferences != null) && (policyReferences.size() != 0))
    {
      out.println(indentNext + "<ReturnPolicyIdList>");
      indenter.in();
      for (PolicyReference reference : policyReferences) {
        reference.encode(out, indenter);
      }
      indenter.out();
      out.println(indentNext + "</ReturnPolicyIdList>");
    }
    if ((attributes != null) && (attributes.size() != 0)) {
      for (Attributes attribute : attributes) {
        attribute.encodeIncludeAttribute(out, indenter);
      }
    }
    indenter.out();
    
    out.println(indent + "</Result>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml3.Result
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
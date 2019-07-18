package org.wso2.balana;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.combine.CombinerElement;
import org.wso2.balana.combine.CombinerParameter;
import org.wso2.balana.combine.CombiningAlgFactory;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.xacml2.Obligation;
import org.wso2.balana.xacml3.Advice;
import org.wso2.balana.xacml3.AdviceExpression;

public abstract class AbstractPolicy
  implements PolicyTreeElement
{
  private URI idAttr;
  private String version;
  private CombiningAlgorithm combiningAlg;
  private String description;
  private AbstractTarget target;
  private String defaultVersion;
  private PolicyMetaData metaData;
  private List children;
  private List childElements;
  private Set<AbstractObligation> obligationExpressions;
  private Set<AdviceExpression> adviceExpressions;
  private List parameters;
  private String subjectPolicyValue;
  private String resourcePolicyValue;
  private String actionPolicyValue;
  private String envPolicyValue;
  private static Log logger = LogFactory.getLog(AbstractPolicy.class);
  
  protected AbstractPolicy() {}
  
  protected AbstractPolicy(URI id, String version, CombiningAlgorithm combiningAlg, String description, AbstractTarget target)
  {
    this(id, version, combiningAlg, description, target, null);
  }
  
  protected AbstractPolicy(URI id, String version, CombiningAlgorithm combiningAlg, String description, AbstractTarget target, String defaultVersion)
  {
    this(id, version, combiningAlg, description, target, defaultVersion, null, null, null);
  }
  
  protected AbstractPolicy(URI id, String version, CombiningAlgorithm combiningAlg, String description, AbstractTarget target, String defaultVersion, Set obligationExpressions, Set adviceExpressions, List parameters)
  {
    idAttr = id;
    this.combiningAlg = combiningAlg;
    this.description = description;
    this.target = target;
    this.defaultVersion = defaultVersion;
    if (version == null) {
      this.version = "1.0";
    } else {
      this.version = version;
    }
    metaData = null;
    if (obligationExpressions == null) {
      this.obligationExpressions = Collections.EMPTY_SET;
    } else {
      this.obligationExpressions = Collections.unmodifiableSet(new HashSet(obligationExpressions));
    }
    if (adviceExpressions == null) {
      this.adviceExpressions = Collections.EMPTY_SET;
    } else {
      this.adviceExpressions = Collections.unmodifiableSet(new HashSet(adviceExpressions));
    }
    if (parameters == null) {
      this.parameters = Collections.EMPTY_LIST;
    } else {
      this.parameters = Collections.unmodifiableList(new ArrayList(parameters));
    }
  }
  
  protected AbstractPolicy(Node root, String policyPrefix, String combiningName)
    throws ParsingException
  {
    NamedNodeMap attrs = root.getAttributes();
    try
    {
      idAttr = new URI(attrs.getNamedItem(policyPrefix + "Id").getNodeValue());
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing required attribute " + policyPrefix + "Id", e);
    }
    Node versionNode = attrs.getNamedItem("Version");
    if (versionNode != null) {
      version = versionNode.getNodeValue();
    } else {
      version = "1.0";
    }
    try
    {
      URI algId = new URI(attrs.getNamedItem(combiningName).getNodeValue());
      CombiningAlgFactory factory = Balana.getInstance().getCombiningAlgFactory();
      combiningAlg = factory.createAlgorithm(algId);
    }
    catch (Exception e)
    {
      throw new ParsingException("Error parsing combining algorithm in " + policyPrefix, 
        e);
    }
    if (policyPrefix.equals("Policy"))
    {
      if (!(combiningAlg instanceof RuleCombiningAlgorithm)) {
        throw new ParsingException("Policy must use a Rule Combining Algorithm");
      }
    }
    else if (!(combiningAlg instanceof PolicyCombiningAlgorithm)) {
      throw new ParsingException("PolicySet must use a Policy Combining Algorithm");
    }
    NodeList children = root.getChildNodes();
    String xpathVersion = null;
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeName().equals(policyPrefix + "Defaults")) {
        handleDefaults(child);
      }
    }
    metaData = new PolicyMetaData(root.getNamespaceURI(), defaultVersion);
    
    obligationExpressions = new HashSet();
    adviceExpressions = new HashSet();
    parameters = new ArrayList();
    children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String cname = child.getNodeName();
      if (cname.equals("Description"))
      {
        if (child.hasChildNodes()) {
          description = child.getFirstChild().getNodeValue();
        }
      }
      else if (cname.equals("Target")) {
        target = TargetFactory.getFactory().getTarget(child, metaData);
      } else if ((cname.equals("ObligationExpressions")) || (cname.equals("Obligations"))) {
        parseObligationExpressions(child);
      } else if (cname.equals("AdviceExpressions")) {
        parseAdviceExpressions(child);
      } else if (cname.equals("CombinerParameters")) {
        handleParameters(child);
      }
    }
    obligationExpressions = Collections.unmodifiableSet(obligationExpressions);
    adviceExpressions = Collections.unmodifiableSet(adviceExpressions);
    parameters = Collections.unmodifiableList(parameters);
  }
  
  public String getSubjectPolicyValue()
  {
    return subjectPolicyValue;
  }
  
  public void setSubjectPolicyValue(String subjectPolicyValue)
  {
    this.subjectPolicyValue = subjectPolicyValue;
  }
  
  public String getResourcePolicyValue()
  {
    return resourcePolicyValue;
  }
  
  public void setResourcePolicyValue(String resourcePolicyValue)
  {
    this.resourcePolicyValue = resourcePolicyValue;
  }
  
  public String getActionPolicyValue()
  {
    return actionPolicyValue;
  }
  
  public void setActionPolicyValue(String actionPolicyValue)
  {
    this.actionPolicyValue = actionPolicyValue;
  }
  
  public String getEnvPolicyValue()
  {
    return envPolicyValue;
  }
  
  public void setEnvPolicyValue(String envPolicyValue)
  {
    this.envPolicyValue = envPolicyValue;
  }
  
  private void parseObligationExpressions(Node root)
    throws ParsingException
  {
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if ((node.getNodeName().equals("ObligationExpression")) || 
        (node.getNodeName().equals("Obligation")))
      {
        AbstractObligation obligation = ObligationFactory.getFactory()
          .getObligation(node, metaData);
        obligationExpressions.add(obligation);
      }
    }
  }
  
  private void parseAdviceExpressions(Node root)
    throws ParsingException
  {
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("AdviceExpression")) {
        adviceExpressions.add(AdviceExpression.getInstance(node, metaData));
      }
    }
  }
  
  private void handleDefaults(Node root)
    throws ParsingException
  {
    defaultVersion = null;
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("XPathVersion")) {
        defaultVersion = node.getFirstChild().getNodeValue();
      }
    }
  }
  
  private void handleParameters(Node root)
    throws ParsingException
  {
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("CombinerParameter")) {
        parameters.add(CombinerParameter.getInstance(node));
      }
    }
  }
  
  public URI getId()
  {
    return idAttr;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public CombiningAlgorithm getCombiningAlg()
  {
    return combiningAlg;
  }
  
  public List getCombiningParameters()
  {
    return parameters;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public AbstractTarget getTarget()
  {
    return target;
  }
  
  public String getDefaultVersion()
  {
    return defaultVersion;
  }
  
  public List getChildren()
  {
    return children;
  }
  
  public List getChildElements()
  {
    return childElements;
  }
  
  public Set getObligationExpressions()
  {
    return obligationExpressions;
  }
  
  public Set getAdviceExpressions()
  {
    return adviceExpressions;
  }
  
  public PolicyMetaData getMetaData()
  {
    return metaData;
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    return target.match(context);
  }
  
  protected void setChildren(List children)
  {
    if (children == null)
    {
      this.children = Collections.EMPTY_LIST;
    }
    else
    {
      List list = new ArrayList();
      Iterator it = children.iterator();
      while (it.hasNext())
      {
        CombinerElement element = (CombinerElement)it.next();
        list.add(element.getElement());
      }
      this.children = Collections.unmodifiableList(list);
      childElements = Collections.unmodifiableList(children);
    }
  }
  
  public AbstractResult evaluate(EvaluationCtx context)
  {
    AbstractResult result = combiningAlg.combine(context, parameters, childElements);
    if ((obligationExpressions.size() < 1) && (adviceExpressions.size() < 1)) {
      return result;
    }
    int effect = result.getDecision();
    if ((effect == 2) || (effect == 3)) {
      return result;
    }
    processObligationAndAdvices(context, effect, result);
    return result;
  }
  
  private void processObligationAndAdvices(EvaluationCtx evaluationCtx, int effect, AbstractResult result)
  {
    if ((obligationExpressions != null) && (obligationExpressions.size() > 0))
    {
      Set<ObligationResult> results = new HashSet();
      for (AbstractObligation obligationExpression : obligationExpressions) {
        if (obligationExpression.getFulfillOn() == effect) {
          results.add(obligationExpression.evaluate(evaluationCtx));
        }
      }
      result.getObligations().addAll(results);
    }
    if ((adviceExpressions != null) && (adviceExpressions.size() > 0))
    {
      Set<Advice> advices = new HashSet();
      for (AdviceExpression adviceExpression : adviceExpressions) {
        if (adviceExpression.getAppliesTo() == effect) {
          advices.add(adviceExpression.evaluate(evaluationCtx));
        }
      }
      result.getAdvices().addAll(advices);
    }
  }
  
  protected void encodeCommonElements(OutputStream output, Indenter indenter)
  {
    Iterator it = childElements.iterator();
    while (it.hasNext()) {
      ((CombinerElement)it.next()).encode(output, indenter);
    }
    if (obligationExpressions.size() != 0)
    {
      PrintStream out = new PrintStream(output);
      String indent = indenter.makeString();
      
      out.println(indent + "<Obligations>");
      indenter.in();
      
      it = obligationExpressions.iterator();
      while (it.hasNext()) {
        ((Obligation)it.next()).encode(output, indenter);
      }
      out.println(indent + "</Obligations>");
      indenter.out();
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.AbstractPolicy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
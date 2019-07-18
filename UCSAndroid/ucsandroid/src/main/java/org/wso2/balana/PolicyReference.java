package org.wso2.balana;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;

public class PolicyReference
  extends AbstractPolicy
{
  public static final int POLICY_REFERENCE = 0;
  public static final int POLICYSET_REFERENCE = 1;
  private URI reference;
  private int policyType;
  private VersionConstraints constraints;
  private PolicyFinder finder;
  private PolicyMetaData parentMetaData;
  private static Log logger = LogFactory.getLog(PolicyReference.class);
  
  public PolicyReference(URI reference, int policyType, PolicyFinder finder, PolicyMetaData parentMetaData)
    throws IllegalArgumentException
  {
    this(reference, policyType, new VersionConstraints(null, null, null), finder, parentMetaData);
  }
  
  public PolicyReference(URI reference, int policyType, VersionConstraints constraints, PolicyFinder finder, PolicyMetaData parentMetaData)
    throws IllegalArgumentException
  {
    if ((policyType != 0) && (policyType != 1)) {
      throw new IllegalArgumentException("Input policyType is not avalid value");
    }
    this.reference = reference;
    this.policyType = policyType;
    this.constraints = constraints;
    this.finder = finder;
    this.parentMetaData = parentMetaData;
  }
  
  /**
   * @deprecated
   */
  public static PolicyReference getInstance(Node root, PolicyFinder finder)
    throws ParsingException
  {
    return getInstance(root, finder, new PolicyMetaData());
  }
  
  public static PolicyReference getInstance(Node root, PolicyFinder finder, PolicyMetaData metaData)
    throws ParsingException
  {
    String name = root.getNodeName();
    int policyType;
    URI reference;
    if (name.equals("PolicyIdReference"))
    {
      policyType = 0;
    }
    else
    {
      if (name.equals("PolicySetIdReference")) {
        policyType = 1;
      } else {
        throw new ParsingException("Unknown reference type: " + name);
      }
    }
    try
    {
      reference = new URI(root.getFirstChild().getNodeValue());
    }
    catch (Exception e)
    {

      throw new ParsingException("Invalid URI in Reference", e);
    }
    NamedNodeMap map = root.getAttributes();
    
    String versionConstraint = null;
    Node versionNode = map.getNamedItem("Version");
    if (versionNode != null) {
      versionConstraint = versionNode.getNodeValue();
    }
    String earlyConstraint = null;
    Node earlyNode = map.getNamedItem("EarliestVersion");
    if (earlyNode != null) {
      earlyConstraint = earlyNode.getNodeValue();
    }
    String lateConstraint = null;
    Node lateNode = map.getNamedItem("LatestVersion");
    if (lateNode != null) {
      lateConstraint = lateNode.getNodeValue();
    }
    VersionConstraints constraints = new VersionConstraints(versionConstraint, earlyConstraint, 
      lateConstraint);
    
    return new PolicyReference(reference, policyType, constraints, finder, metaData);
  }
  
  public URI getReference()
  {
    return reference;
  }
  
  public VersionConstraints getConstraints()
  {
    return constraints;
  }
  
  public int getReferenceType()
  {
    return policyType;
  }
  
  public URI getId()
  {
    return resolvePolicy().getId();
  }
  
  public String getVersion()
  {
    return resolvePolicy().getVersion();
  }
  
  public CombiningAlgorithm getCombiningAlg()
  {
    return resolvePolicy().getCombiningAlg();
  }
  
  public String getDescription()
  {
    return resolvePolicy().getDescription();
  }
  
  public AbstractTarget getTarget()
  {
    return resolvePolicy().getTarget();
  }
  
  public String getDefaultVersion()
  {
    return resolvePolicy().getDefaultVersion();
  }
  
  public List getChildren()
  {
    return resolvePolicy().getChildren();
  }
  
  public List getChildElements()
  {
    return resolvePolicy().getChildElements();
  }
  
  public Set getObligationExpressions()
  {
    return resolvePolicy().getObligationExpressions();
  }
  
  public PolicyMetaData getMetaData()
  {
    return resolvePolicy().getMetaData();
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    try
    {
      return getTarget().match(context);
    }
    catch (ProcessingException pe)
    {
      ArrayList code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
      Status status = new Status(code, "couldn't resolve policy ref");
      return new MatchResult(2, status);
    }
  }
  
  private AbstractPolicy resolvePolicy()
  {
    if (finder == null)
    {
      if (logger.isWarnEnabled()) {
        logger.warn("PolicyReference with id " + reference.toString() + 
          " was queried but was " + "not configured with a PolicyFinder");
      }
      throw new ProcessingException("couldn't find the policy with a null finder");
    }
    PolicyFinderResult pfr = finder.findPolicy(reference, policyType, constraints, 
      parentMetaData);
    if (pfr.notApplicable()) {
      throw new ProcessingException("couldn't resolve the policy");
    }
    if (pfr.indeterminate()) {
      throw new ProcessingException("error resolving the policy");
    }
    return pfr.getPolicy();
  }
  
  public AbstractResult evaluate(EvaluationCtx context)
  {
    if (finder == null) {
      return ResultFactory.getFactory().getResult(3, context);
    }
    PolicyFinderResult pfr = finder.findPolicy(reference, policyType, constraints, 
      parentMetaData);
    if (pfr.notApplicable()) {
      return ResultFactory.getFactory().getResult(3, context);
    }
    if (pfr.indeterminate()) {
      return ResultFactory.getFactory().getResult(2, pfr.getStatus(), context);
    }
    return pfr.getPolicy().evaluate(context);
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String encoded = indenter.makeString();
    if (policyType == 0) {
      out.println(encoded + "<PolicyIdReference" + encodeConstraints() + ">" + 
        reference.toString() + "</PolicyIdReference>");
    } else {
      out.println(encoded + "<PolicySetIdReference" + encodeConstraints() + ">" + 
        reference.toString() + "</PolicySetIdReference>");
    }
  }
  
  private String encodeConstraints()
  {
    String str = "";
    VersionConstraints version = getConstraints();
    
    String v = version.getVersionConstraint();
    if (v != null) {
      str = str + " Version=\"" + v + "\"";
    }
    String e = version.getEarliestConstraint();
    if (e != null) {
      str = str + " EarliestVersion=\"" + e + "\"";
    }
    String l = version.getLatestConstraint();
    if (l != null) {
      str = str + " LatestVersion=\"" + l + "\"";
    }
    return str;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.PolicyReference
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
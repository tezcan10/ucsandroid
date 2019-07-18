package org.wso2.balana.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.AbstractTarget;
import org.wso2.balana.Indenter;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.ProcessingException;
import org.wso2.balana.ctx.EvaluationCtx;

public class Target
  extends AbstractTarget
{
  private TargetSection subjectsSection;
  private TargetSection resourcesSection;
  private TargetSection actionsSection;
  private TargetSection environmentsSection;
  private int xacmlVersion;
  private static Log logger = LogFactory.getLog(Target.class);
  
  public Target(TargetSection subjectsSection, TargetSection resourcesSection, TargetSection actionsSection)
  {
    if ((subjectsSection == null) || (resourcesSection == null) || (actionsSection == null)) {
      throw new ProcessingException("All sections of a Target must be non-null");
    }
    this.subjectsSection = subjectsSection;
    this.resourcesSection = resourcesSection;
    this.actionsSection = actionsSection;
    environmentsSection = new TargetSection(null, 3, 
      0);
    xacmlVersion = 0;
  }
  
  public Target(TargetSection subjectsSection, TargetSection resourcesSection, TargetSection actionsSection, TargetSection environmentsSection)
  {
    if ((subjectsSection == null) || (resourcesSection == null) || (actionsSection == null) || 
      (environmentsSection == null)) {
      throw new ProcessingException("All sections of a Target must be non-null");
    }
    this.subjectsSection = subjectsSection;
    this.resourcesSection = resourcesSection;
    this.actionsSection = actionsSection;
    this.environmentsSection = environmentsSection;
    xacmlVersion = 2;
  }
  
  /**
   * @deprecated
   */
  public static Target getInstance(Node root, String xpathVersion)
    throws ParsingException
  {
    return getInstance(root, new PolicyMetaData("urn:oasis:names:tc:xacml:1.0:policy", 
      xpathVersion));
  }
  
  public static Target getInstance(Node root, PolicyMetaData metaData)
    throws ParsingException
  {
    TargetSection subjects = null;
    TargetSection resources = null;
    TargetSection actions = null;
    TargetSection environments = null;
    int version = metaData.getXACMLVersion();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("Subjects")) {
        subjects = TargetSection.getInstance(child, 0, metaData);
      } else if (name.equals("Resources")) {
        resources = TargetSection.getInstance(child, 1, metaData);
      } else if (name.equals("Actions")) {
        actions = TargetSection.getInstance(child, 2, metaData);
      } else if (name.equals("Environments")) {
        environments = TargetSection.getInstance(child, 3, metaData);
      }
    }
    if (subjects == null) {
      subjects = new TargetSection(null, 0, version);
    }
    if (resources == null) {
      resources = new TargetSection(null, 1, version);
    }
    if (actions == null) {
      actions = new TargetSection(null, 2, version);
    }
    if (version == 2)
    {
      if (environments == null) {
        environments = new TargetSection(null, 3, version);
      }
      return new Target(subjects, resources, actions, environments);
    }
    return new Target(subjects, resources, actions);
  }
  
  public TargetSection getSubjectsSection()
  {
    return subjectsSection;
  }
  
  public TargetSection getResourcesSection()
  {
    return resourcesSection;
  }
  
  public TargetSection getActionsSection()
  {
    return actionsSection;
  }
  
  public TargetSection getEnvironmentsSection()
  {
    return environmentsSection;
  }
  
  public boolean matchesAny()
  {
    return (subjectsSection.matchesAny()) && (resourcesSection.matchesAny()) && 
      (actionsSection.matchesAny()) && (environmentsSection.matchesAny());
  }
  
  public MatchResult match(EvaluationCtx context)
  {
    MatchResult result = null;
    if (matchesAny()) {
      return new MatchResult(0);
    }
    result = subjectsSection.match(context);
    if (result.getResult() != 0)
    {
      if (logger.isDebugEnabled()) {
        logger.debug("failed to match Subjects section of Target");
      }
      return result;
    }
    String subjectPolicyValue = result.getPolicyValue();
    
    result = resourcesSection.match(context);
    if (result.getResult() != 0)
    {
      if (logger.isDebugEnabled()) {
        logger.debug("failed to match Resources section of Target");
      }
      return result;
    }
    String resourcePolicyValue = result.getPolicyValue();
    
    result = actionsSection.match(context);
    if (result.getResult() != 0)
    {
      if (logger.isDebugEnabled()) {
        logger.debug("failed to match Actions section of Target");
      }
      return result;
    }
    String actionPolicyValue = result.getPolicyValue();
    
    result = environmentsSection.match(context);
    if (result.getResult() != 0)
    {
      if (logger.isDebugEnabled()) {
        logger.debug("failed to match Environments section of Target");
      }
      return result;
    }
    String envPolicyValue = result.getPolicyValue();
    
    result.setActionPolicyValue(actionPolicyValue);
    result.setSubjectPolicyValue(subjectPolicyValue);
    result.setEnvPolicyValue(envPolicyValue);
    result.setResourcePolicyValue(resourcePolicyValue);
    
    return result;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    boolean matchesAny = (subjectsSection.matchesAny()) && (resourcesSection.matchesAny()) && 
      (actionsSection.matchesAny()) && (environmentsSection.matchesAny());
    if ((matchesAny) && (xacmlVersion == 2))
    {
      out.println("<Target/>");
    }
    else
    {
      out.println(indent + "<Target>");
      indenter.in();
      
      subjectsSection.encode(output, indenter);
      resourcesSection.encode(output, indenter);
      actionsSection.encode(output, indenter);
      if (xacmlVersion == 2) {
        environmentsSection.encode(output, indenter);
      }
      indenter.out();
      out.println(indent + "</Target>");
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml2.Target
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
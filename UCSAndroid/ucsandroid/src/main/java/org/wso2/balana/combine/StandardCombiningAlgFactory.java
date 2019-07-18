package org.wso2.balana.combine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.combine.xacml2.FirstApplicablePolicyAlg;
import org.wso2.balana.combine.xacml2.FirstApplicableRuleAlg;
import org.wso2.balana.combine.xacml2.OnlyOneApplicablePolicyAlg;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitPolicyAlg;
import org.wso2.balana.combine.xacml3.DenyUnlessPermitRuleAlg;
import org.wso2.balana.combine.xacml3.PermitUnlessDenyPolicyAlg;
import org.wso2.balana.combine.xacml3.PermitUnlessDenyRuleAlg;

public class StandardCombiningAlgFactory
  extends BaseCombiningAlgFactory
{
  private static volatile StandardCombiningAlgFactory factoryInstance = null;
  private static Set supportedAlgorithms = null;
  private static Set supportedAlgIds;
  private static Log logger = LogFactory.getLog(StandardCombiningAlgFactory.class);
  
  private StandardCombiningAlgFactory()
  {
    super(supportedAlgorithms);
  }
  
  private static void initAlgorithms()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Initializing standard combining algorithms");
    }
    supportedAlgorithms = new HashSet();
    supportedAlgIds = new HashSet();
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.DenyOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.OrderedDenyOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:ordered-deny-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.OrderedDenyOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-deny-overrides");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.PermitOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.PermitOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:permit-overrides");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.OrderedPermitOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:ordered-permit-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml2.OrderedPermitOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-permit-overrides");
    
    supportedAlgorithms.add(new FirstApplicableRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable");
    supportedAlgorithms.add(new FirstApplicablePolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable");
    
    supportedAlgorithms.add(new OnlyOneApplicablePolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable");
    
    supportedAlgorithms.add(new DenyUnlessPermitRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit");
    supportedAlgorithms.add(new DenyUnlessPermitPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit");
    
    supportedAlgorithms.add(new PermitUnlessDenyRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny");
    supportedAlgorithms.add(new PermitUnlessDenyPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.DenyOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.DenyOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.OrderedDenyOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.OrderedDenyOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-deny-overrides");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.PermitOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.PermitOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides");
    
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.OrderedPermitOverridesRuleAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides");
    supportedAlgorithms.add(new org.wso2.balana.combine.xacml3.OrderedPermitOverridesPolicyAlg());
    supportedAlgIds.add("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-permit-overrides");
    
    supportedAlgIds = Collections.unmodifiableSet(supportedAlgIds);
  }
  
  public static StandardCombiningAlgFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (StandardCombiningAlgFactory.class)
      {
        if (factoryInstance == null)
        {
          initAlgorithms();
          factoryInstance = new StandardCombiningAlgFactory();
        }
      }
    }
    return factoryInstance;
  }
  
  public static CombiningAlgFactory getNewFactory()
  {
    getFactory();
    
    return new BaseCombiningAlgFactory(supportedAlgorithms);
  }
  
  public static Set getStandardAlgorithms(String xacmlVersion)
    throws UnknownIdentifierException
  {
    if ((xacmlVersion.equals("urn:oasis:names:tc:xacml:1.0:policy")) || 
      (xacmlVersion.equals("urn:oasis:names:tc:xacml:2.0:policy:schema:os")) || 
      (xacmlVersion.equals("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"))) {
      return supportedAlgIds;
    }
    throw new UnknownIdentifierException("Unknown XACML version: " + xacmlVersion);
  }
  
  public void addAlgorithm(CombiningAlgorithm alg)
  {
    throw new UnsupportedOperationException("a standard factory cannot support new algorithms");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.StandardCombiningAlgFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
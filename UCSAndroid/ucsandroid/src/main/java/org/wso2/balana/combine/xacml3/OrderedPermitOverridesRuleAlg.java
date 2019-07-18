package org.wso2.balana.combine.xacml3;

import java.net.URI;
import java.net.URISyntaxException;

public class OrderedPermitOverridesRuleAlg
  extends PermitOverridesRuleAlg
{
  public static final String algId = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public OrderedPermitOverridesRuleAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml3.OrderedPermitOverridesRuleAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
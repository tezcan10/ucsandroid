package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;

public class OrderedDenyOverridesRuleAlg
  extends DenyOverridesRuleAlg
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:ordered-deny-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.1:rule-combining-algorithm:ordered-deny-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public OrderedDenyOverridesRuleAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.OrderedDenyOverridesRuleAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
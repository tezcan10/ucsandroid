package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;

public class OrderedDenyOverridesPolicyAlg
  extends DenyOverridesPolicyAlg
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-deny-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-deny-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public OrderedDenyOverridesPolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.OrderedDenyOverridesPolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
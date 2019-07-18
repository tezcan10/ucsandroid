package org.wso2.balana.combine.xacml2;

import java.net.URI;
import java.net.URISyntaxException;

public class OrderedPermitOverridesPolicyAlg
  extends PermitOverridesPolicyAlg
{
  public static final String algId = "urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-permit-overrides";
  private static URI identifierURI;
  private static RuntimeException earlyException;
  
  static
  {
    try
    {
      identifierURI = new URI("urn:oasis:names:tc:xacml:1.1:policy-combining-algorithm:ordered-permit-overrides");
    }
    catch (URISyntaxException se)
    {
      earlyException = new IllegalArgumentException();
      earlyException.initCause(se);
    }
  }
  
  public OrderedPermitOverridesPolicyAlg()
  {
    super(identifierURI);
    if (earlyException != null) {
      throw earlyException;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.xacml2.OrderedPermitOverridesPolicyAlg
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
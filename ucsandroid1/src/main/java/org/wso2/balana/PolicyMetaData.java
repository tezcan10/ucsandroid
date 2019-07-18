package org.wso2.balana;

public class PolicyMetaData
{
  public static final int XACML_DEFAULT_VERSION = 0;
  private static String[] xacmlIdentifiers = { "urn:oasis:names:tc:xacml:1.0:policy", 
    "urn:oasis:names:tc:xacml:1.0:policy", "urn:oasis:names:tc:xacml:2.0:policy:schema:os", 
    "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" };
  public static final String XPATH_1_0_IDENTIFIER = "http://www.w3.org/TR/1999/Rec-xpath-19991116";
  public static final int XPATH_VERSION_UNSPECIFIED = 0;
  public static final int XPATH_VERSION_1_0 = 1;
  private static String[] xpathIdentifiers = { 0"http://www.w3.org/TR/1999/Rec-xpath-19991116" };
  private int xacmlVersion;
  private int xpathVersion;
  
  public PolicyMetaData()
  {
    this(0, 0);
  }
  
  public PolicyMetaData(int xacmlVersion, int xpathVersion)
  {
    this.xacmlVersion = xacmlVersion;
    this.xpathVersion = xpathVersion;
  }
  
  public PolicyMetaData(String xacmlVersion, String xpathVersion)
  {
    if (xacmlVersion == null) {
      this.xacmlVersion = 0;
    } else if (xacmlVersion.equals("urn:oasis:names:tc:xacml:1.0:policy")) {
      this.xacmlVersion = 0;
    } else if (xacmlVersion.equals("urn:oasis:names:tc:xacml:2.0:policy:schema:os")) {
      this.xacmlVersion = 2;
    } else if (xacmlVersion.equals("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17")) {
      this.xacmlVersion = 3;
    } else {
      throw new IllegalArgumentException("Unknown XACML version string: " + xacmlVersion);
    }
    if (xpathVersion != null) {
      this.xpathVersion = 1;
    } else {
      this.xpathVersion = 0;
    }
  }
  
  public int getXACMLVersion()
  {
    return xacmlVersion;
  }
  
  public String getXACMLIdentifier()
  {
    return xacmlIdentifiers[xacmlVersion];
  }
  
  public int getXPathVersion()
  {
    return xpathVersion;
  }
  
  public String getXPathIdentifier()
  {
    return xpathIdentifiers[xpathVersion];
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.PolicyMetaData
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
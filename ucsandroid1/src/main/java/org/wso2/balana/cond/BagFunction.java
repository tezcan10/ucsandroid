package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.Set;

public abstract class BagFunction
  extends FunctionBase
{
  public static final String NAME_BASE_ONE_AND_ONLY = "-one-and-only";
  public static final String NAME_BASE_BAG_SIZE = "-bag-size";
  public static final String NAME_BASE_IS_IN = "-is-in";
  public static final String NAME_BASE_BAG = "-bag";
  private static final boolean[] bagParams = { falsetrue };
  protected static String[] baseTypes = { "http://www.w3.org/2001/XMLSchema#string", 
    "http://www.w3.org/2001/XMLSchema#boolean", "http://www.w3.org/2001/XMLSchema#integer", "http://www.w3.org/2001/XMLSchema#double", 
    "http://www.w3.org/2001/XMLSchema#date", "http://www.w3.org/2001/XMLSchema#dateTime", "http://www.w3.org/2001/XMLSchema#time", 
    "http://www.w3.org/2001/XMLSchema#anyURI", "http://www.w3.org/2001/XMLSchema#hexBinary", 
    "http://www.w3.org/2001/XMLSchema#base64Binary", "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration", 
    "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration", "urn:oasis:names:tc:xacml:1.0:data-type:x500Name", 
    "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" };
  protected static String[] baseTypes2 = { "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress", 
    "urn:oasis:names:tc:xacml:2.0:data-type:dnsName" };
  protected static String[] simpleTypes = { "string", "boolean", "integer", "double", "date", 
    "dateTime", "time", "anyURI", "hexBinary", "base64Binary", "dayTimeDuration", 
    "yearMonthDuration", "x500Name", "rfc822Name" };
  protected static String[] simpleTypes2 = { "ipAddress", "dnsName" };
  
  public static BagFunction getOneAndOnlyInstance(String functionName, String argumentType)
  {
    return new GeneralBagFunction(functionName, argumentType, "-one-and-only");
  }
  
  public static BagFunction getBagSizeInstance(String functionName, String argumentType)
  {
    return new GeneralBagFunction(functionName, argumentType, "-bag-size");
  }
  
  public static BagFunction getIsInInstance(String functionName, String argumentType)
  {
    return new ConditionBagFunction(functionName, argumentType);
  }
  
  public static BagFunction getBagInstance(String functionName, String argumentType)
  {
    return new GeneralBagFunction(functionName, argumentType, "-bag");
  }
  
  protected BagFunction(String functionName, int functionId, String paramType, boolean paramIsBag, int numParams, String returnType, boolean returnsBag)
  {
    super(functionName, functionId, paramType, paramIsBag, numParams, returnType, returnsBag);
  }
  
  protected BagFunction(String functionName, int functionId, String[] paramTypes)
  {
    super(functionName, functionId, paramTypes, bagParams, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.addAll(ConditionBagFunction.getSupportedIdentifiers());
    set.addAll(GeneralBagFunction.getSupportedIdentifiers());
    
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.BagFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
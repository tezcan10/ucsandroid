package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.Set;

public abstract class SetFunction
  extends FunctionBase
{
  public static final String NAME_BASE_INTERSECTION = "-intersection";
  public static final String NAME_BASE_AT_LEAST_ONE_MEMBER_OF = "-at-least-one-member-of";
  public static final String NAME_BASE_UNION = "-union";
  public static final String NAME_BASE_SUBSET = "-subset";
  public static final String NAME_BASE_SET_EQUALS = "-set-equals";
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
  
  public static SetFunction getIntersectionInstance(String functionName, String argumentType)
  {
    return new GeneralSetFunction(functionName, argumentType, "-intersection");
  }
  
  public static SetFunction getAtLeastOneInstance(String functionName, String argumentType)
  {
    return new ConditionSetFunction(functionName, argumentType, 
      "-at-least-one-member-of");
  }
  
  public static SetFunction getUnionInstance(String functionName, String argumentType)
  {
    return new GeneralSetFunction(functionName, argumentType, "-union");
  }
  
  public static SetFunction getSubsetInstance(String functionName, String argumentType)
  {
    return new ConditionSetFunction(functionName, argumentType, "-subset");
  }
  
  public static SetFunction getSetEqualsInstance(String functionName, String argumentType)
  {
    return new ConditionSetFunction(functionName, argumentType, "-set-equals");
  }
  
  protected SetFunction(String functionName, int functionId, String argumentType, String returnType, boolean returnsBag)
  {
    super(functionName, functionId, argumentType, true, 2, returnType, returnsBag);
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.addAll(ConditionSetFunction.getSupportedIdentifiers());
    set.addAll(GeneralSetFunction.getSupportedIdentifiers());
    
    return set;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.SetFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
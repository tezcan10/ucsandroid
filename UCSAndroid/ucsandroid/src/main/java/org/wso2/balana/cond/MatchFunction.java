package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.security.auth.x500.X500Principal;
import org.wso2.balana.attr.AnyURIAttribute;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DNSNameAttribute;
import org.wso2.balana.attr.IPAddressAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.attr.X500NameAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class MatchFunction
  extends FunctionBase
{
  public static final String NAME_REGEXP_STRING_MATCH = "urn:oasis:names:tc:xacml:1.0:function:regexp-string-match";
  public static final String NAME_X500NAME_MATCH = "urn:oasis:names:tc:xacml:1.0:function:x500Name-match";
  public static final String NAME_RFC822NAME_MATCH = "urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match";
  public static final String NAME_STRING_REGEXP_MATCH = "urn:oasis:names:tc:xacml:1.0:function:string-regexp-match";
  public static final String NAME_ANYURI_REGEXP_MATCH = "urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match";
  public static final String NAME_IPADDRESS_REGEXP_MATCH = "urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match";
  public static final String NAME_DNSNAME_REGEXP_MATCH = "urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match";
  public static final String NAME_RFC822NAME_REGEXP_MATCH = "urn:oasis:names:tc:xacml:2.0:function:rfc822Name-regexp-match";
  public static final String NAME_X500NAME_REGEXP_MATCH = "urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match";
  private static final int ID_REGEXP_STRING_MATCH = 0;
  private static final int ID_X500NAME_MATCH = 1;
  private static final int ID_RFC822NAME_MATCH = 2;
  private static final int ID_STRING_REGEXP_MATCH = 3;
  private static final int ID_ANYURI_REGEXP_MATCH = 4;
  private static final int ID_IPADDRESS_REGEXP_MATCH = 5;
  private static final int ID_DNSNAME_REGEXP_MATCH = 6;
  private static final int ID_RFC822NAME_REGEXP_MATCH = 7;
  private static final int ID_X500NAME_REGEXP_MATCH = 8;
  private static final String[] regexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "http://www.w3.org/2001/XMLSchema#string" };
  private static final String[] x500Params = { "urn:oasis:names:tc:xacml:1.0:data-type:x500Name", 
    "urn:oasis:names:tc:xacml:1.0:data-type:x500Name" };
  private static final String[] rfc822Params = { "http://www.w3.org/2001/XMLSchema#string", 
    "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" };
  private static final String[] stringRegexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "http://www.w3.org/2001/XMLSchema#string" };
  private static final String[] anyURIRegexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "http://www.w3.org/2001/XMLSchema#anyURI" };
  private static final String[] ipAddressRegexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress" };
  private static final String[] dnsNameRegexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "urn:oasis:names:tc:xacml:2.0:data-type:dnsName" };
  private static final String[] rfc822NameRegexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" };
  private static final String[] x500NameRegexpParams = { "http://www.w3.org/2001/XMLSchema#string", 
    "urn:oasis:names:tc:xacml:1.0:data-type:x500Name" };
  private static final boolean[] bagParams = new boolean[2];
  
  public MatchFunction(String functionName)
  {
    super(functionName, getId(functionName), getArgumentTypes(functionName), bagParams, "http://www.w3.org/2001/XMLSchema#boolean", false);
  }
  
  private static int getId(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:regexp-string-match")) {
      return 0;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:x500Name-match")) {
      return 1;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match")) {
      return 2;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:string-regexp-match")) {
      return 3;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match")) {
      return 4;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match")) {
      return 5;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match")) {
      return 6;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:rfc822Name-regexp-match")) {
      return 7;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match")) {
      return 8;
    }
    throw new IllegalArgumentException("unknown match function: " + functionName);
  }
  
  private static String[] getArgumentTypes(String functionName)
  {
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:regexp-string-match")) {
      return regexpParams;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:x500Name-match")) {
      return x500Params;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match")) {
      return rfc822Params;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:1.0:function:string-regexp-match")) {
      return stringRegexpParams;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match")) {
      return anyURIRegexpParams;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match")) {
      return ipAddressRegexpParams;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match")) {
      return dnsNameRegexpParams;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:rfc822Name-regexp-match")) {
      return rfc822NameRegexpParams;
    }
    if (functionName.equals("urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match")) {
      return x500NameRegexpParams;
    }
    return null;
  }
  
  public static Set getSupportedIdentifiers()
  {
    Set set = new HashSet();
    
    set.add("urn:oasis:names:tc:xacml:1.0:function:regexp-string-match");
    set.add("urn:oasis:names:tc:xacml:1.0:function:x500Name-match");
    set.add("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match");
    set.add("urn:oasis:names:tc:xacml:1.0:function:string-regexp-match");
    set.add("urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match");
    set.add("urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match");
    set.add("urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match");
    set.add("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match");
    set.add("urn:oasis:names:tc:xacml:1.0:function:x500Name-match");
    
    return set;
  }
  
  public EvaluationResult evaluate(List inputs, EvaluationCtx context)
  {
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null) {
      return result;
    }
    boolean boolResult = false;
    switch (getFunctionId())
    {
    case 0: 
    case 3: 
      String arg0 = ((StringAttribute)argValues[0]).getValue();
      String arg1 = ((StringAttribute)argValues[1]).getValue();
      if ((context.isSearching()) && (arg1.equals("Any"))) {
        boolResult = true;
      } else {
        boolResult = regexpHelper(arg0, arg1);
      }
      break;
    case 1: 
      X500Principal principal = ((X500NameAttribute)argValues[0]).getValue();
      X500Principal principal1 = ((X500NameAttribute)argValues[1]).getValue();
      
      boolResult = principal1.getName("CANONICAL").endsWith(
        principal.getName("CANONICAL"));
      
      break;
    case 2: 
      String string = ((StringAttribute)argValues[0]).getValue();
      String string1 = ((RFC822NameAttribute)argValues[1]).getValue();
      if (string.indexOf('@') != -1)
      {
        String normalized = new RFC822NameAttribute(string).getValue();
        boolResult = normalized.equals(string1);
      }
      else if (string.charAt(0) == '.')
      {
        boolResult = string1.endsWith(string.toLowerCase());
      }
      else
      {
        String mailDomain = string1.substring(string1.indexOf('@') + 1);
        boolResult = string.toLowerCase().equals(mailDomain);
      }
      break;
    case 4: 
      String string2 = ((StringAttribute)argValues[0]).getValue();
      String string3 = ((AnyURIAttribute)argValues[1]).encode();
      
      boolResult = regexpHelper(string2, string3);
      
      break;
    case 5: 
      String string4 = ((StringAttribute)argValues[0]).getValue();
      String string5 = ((IPAddressAttribute)argValues[1]).encode();
      
      boolResult = regexpHelper(string4, string5);
      
      break;
    case 6: 
      String string6 = ((StringAttribute)argValues[0]).getValue();
      String string7 = ((DNSNameAttribute)argValues[1]).encode();
      
      boolResult = regexpHelper(string6, string7);
      
      break;
    case 7: 
      String string8 = ((StringAttribute)argValues[0]).getValue();
      String string9 = ((RFC822NameAttribute)argValues[1]).encode();
      
      boolResult = regexpHelper(string8, string9);
      
      break;
    case 8: 
      String string10 = ((StringAttribute)argValues[0]).getValue();
      String string11 = ((X500NameAttribute)argValues[1]).encode();
      
      boolResult = regexpHelper(string10, string11);
    }
    return EvaluationResult.getInstance(boolResult);
  }
  
  private boolean regexpHelper(String xpr, String str)
  {
    StringBuffer buf = new StringBuffer(xpr);
    if (xpr.charAt(0) != '^') {
      buf = buf.insert(0, ".*");
    }
    if (xpr.charAt(xpr.length() - 1) != '$') {
      buf = buf.insert(buf.length(), ".*");
    }
    int idx = -1;
    idx = buf.indexOf("\\p{Is", 0);
    while (idx != -1)
    {
      buf = buf.replace(idx, idx + 5, "\\p{In");
      idx = buf.indexOf("\\p{Is", idx);
    }
    idx = -1;
    idx = buf.indexOf("\\P{Is", 0);
    while (idx != -1)
    {
      buf = buf.replace(idx, idx + 5, "\\P{In");
      idx = buf.indexOf("\\P{Is", idx);
    }
    idx = -1;
    idx = buf.indexOf("-[", 0);
    while (idx != -1)
    {
      buf = buf.replace(idx, idx + 2, "&&[^");
      idx = buf.indexOf("-[", idx);
    }
    return Pattern.matches(buf.toString(), str);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.MatchFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.finder.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.PolicySet;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

public class FileBasedPolicyFinderModule
  extends PolicyFinderModule
{
  private PolicyFinder finder = null;
  private Map<URI, AbstractPolicy> policies;
  private Set<String> policyLocations;
  private PolicyCombiningAlgorithm combiningAlg;
  private static Log log = LogFactory.getLog(FileBasedPolicyFinderModule.class);
  public static final String POLICY_DIR_PROPERTY = "org.wso2.balana.PolicyDirectory";
  
  public FileBasedPolicyFinderModule()
  {
    policies = new HashMap();
    if (System.getProperty("org.wso2.balana.PolicyDirectory") != null)
    {
      policyLocations = new HashSet();
      policyLocations.add(System.getProperty("org.wso2.balana.PolicyDirectory"));
    }
  }
  
  public FileBasedPolicyFinderModule(Set<String> policyLocations)
  {
    policies = new HashMap();
    this.policyLocations = policyLocations;
  }
  
  public void init(PolicyFinder finder)
  {
    this.finder = finder;
    loadPolicies();
    combiningAlg = new DenyOverridesPolicyAlg();
  }
  
  public PolicyFinderResult findPolicy(EvaluationCtx context)
  {
    ArrayList<AbstractPolicy> selectedPolicies = new ArrayList();
    Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();
    for (Map.Entry<URI, AbstractPolicy> entry : entrySet)
    {
      AbstractPolicy policy = (AbstractPolicy)entry.getValue();
      MatchResult match = policy.match(context);
      int result = match.getResult();
      if (result == 2) {
        return new PolicyFinderResult(match.getStatus());
      }
      if (result == 0)
      {
        if ((combiningAlg == null) && (selectedPolicies.size() > 0))
        {
          ArrayList<String> code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          Status status = new Status(code, "too many applicable top-level policies");
          
          return new PolicyFinderResult(status);
        }
        selectedPolicies.add(policy);
      }
    }
    switch (selectedPolicies.size())
    {
    case 0: 
      if (log.isDebugEnabled()) {
        log.debug("No matching XACML policy found");
      }
      return new PolicyFinderResult();
    case 1: 
      return new PolicyFinderResult((AbstractPolicy)selectedPolicies.get(0));
    }
    return new PolicyFinderResult(new PolicySet(null, combiningAlg, null, selectedPolicies));
  }
  
  public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData)
  {
    AbstractPolicy policy = (AbstractPolicy)policies.get(idReference);
    if (policy != null) {
      if (type == 0)
      {
        if ((policy instanceof Policy)) {
          return new PolicyFinderResult(policy);
        }
      }
      else if ((policy instanceof PolicySet)) {
        return new PolicyFinderResult(policy);
      }
    }
    ArrayList<String> code = new ArrayList();
    code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
    Status status = new Status(code, 
      "couldn't load referenced policy");
    return new PolicyFinderResult(status);
  }
  
  public boolean isIdReferenceSupported()
  {
    return true;
  }
  
  public boolean isRequestSupported()
  {
    return true;
  }
  
  public void loadPolicies()
  {
    policies.clear();
    for (String policyLocation : policyLocations)
    {
      File file = new File(policyLocation);
      if (file.exists()) {
        if (file.isDirectory())
        {
          String[] files = file.list();
          String[] arrayOfString1;
          int j = (arrayOfString1 = files).length;
          for (int i = 0; i < j; i++)
          {
            String policyFile = arrayOfString1[i];
            loadPolicy(policyLocation + File.separator + policyFile, finder);
          }
        }
        else
        {
          loadPolicy(policyLocation, finder);
        }
      }
    }
  }
  
  /* Error */
  private AbstractPolicy loadPolicy(String policyFile, PolicyFinder finder)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: invokestatic 249	javax/xml/parsers/DocumentBuilderFactory:newInstance	()Ljavax/xml/parsers/DocumentBuilderFactory;
    //   8: astore 5
    //   10: aload 5
    //   12: iconst_1
    //   13: invokevirtual 255	javax/xml/parsers/DocumentBuilderFactory:setIgnoringComments	(Z)V
    //   16: aload 5
    //   18: iconst_1
    //   19: invokevirtual 259	javax/xml/parsers/DocumentBuilderFactory:setNamespaceAware	(Z)V
    //   22: aload 5
    //   24: iconst_0
    //   25: invokevirtual 262	javax/xml/parsers/DocumentBuilderFactory:setValidating	(Z)V
    //   28: aload 5
    //   30: invokevirtual 265	javax/xml/parsers/DocumentBuilderFactory:newDocumentBuilder	()Ljavax/xml/parsers/DocumentBuilder;
    //   33: astore 6
    //   35: new 269	java/io/FileInputStream
    //   38: dup
    //   39: aload_1
    //   40: invokespecial 271	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   43: astore 4
    //   45: aload 6
    //   47: aload 4
    //   49: invokevirtual 272	javax/xml/parsers/DocumentBuilder:parse	(Ljava/io/InputStream;)Lorg/w3c/dom/Document;
    //   52: astore 7
    //   54: aload 7
    //   56: invokeinterface 278 1 0
    //   61: astore 8
    //   63: aload 8
    //   65: invokeinterface 284 1 0
    //   70: astore 9
    //   72: aload 9
    //   74: ldc_w 289
    //   77: invokevirtual 291	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   80: ifeq +12 -> 92
    //   83: aload 8
    //   85: invokestatic 294	org/wso2/balana/Policy:getInstance	(Lorg/w3c/dom/Node;)Lorg/wso2/balana/Policy;
    //   88: astore_3
    //   89: goto +113 -> 202
    //   92: aload 9
    //   94: ldc_w 298
    //   97: invokevirtual 291	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   100: ifeq +102 -> 202
    //   103: aload 8
    //   105: aload_2
    //   106: invokestatic 300	org/wso2/balana/PolicySet:getInstance	(Lorg/w3c/dom/Node;Lorg/wso2/balana/finder/PolicyFinder;)Lorg/wso2/balana/PolicySet;
    //   109: astore_3
    //   110: goto +92 -> 202
    //   113: astore 5
    //   115: getstatic 32	org/wso2/balana/finder/impl/FileBasedPolicyFinderModule:log	Lorg/apache/commons/logging/Log;
    //   118: new 221	java/lang/StringBuilder
    //   121: dup
    //   122: ldc_w 303
    //   125: invokespecial 227	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   128: aload_1
    //   129: invokevirtual 231	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: invokevirtual 235	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: aload 5
    //   137: invokeinterface 305 3 0
    //   142: aload 4
    //   144: ifnull +84 -> 228
    //   147: aload 4
    //   149: invokevirtual 309	java/io/InputStream:close	()V
    //   152: goto +76 -> 228
    //   155: astore 11
    //   157: getstatic 32	org/wso2/balana/finder/impl/FileBasedPolicyFinderModule:log	Lorg/apache/commons/logging/Log;
    //   160: ldc_w 314
    //   163: invokeinterface 316 2 0
    //   168: goto +60 -> 228
    //   171: astore 10
    //   173: aload 4
    //   175: ifnull +24 -> 199
    //   178: aload 4
    //   180: invokevirtual 309	java/io/InputStream:close	()V
    //   183: goto +16 -> 199
    //   186: astore 11
    //   188: getstatic 32	org/wso2/balana/finder/impl/FileBasedPolicyFinderModule:log	Lorg/apache/commons/logging/Log;
    //   191: ldc_w 314
    //   194: invokeinterface 316 2 0
    //   199: aload 10
    //   201: athrow
    //   202: aload 4
    //   204: ifnull +24 -> 228
    //   207: aload 4
    //   209: invokevirtual 309	java/io/InputStream:close	()V
    //   212: goto +16 -> 228
    //   215: astore 11
    //   217: getstatic 32	org/wso2/balana/finder/impl/FileBasedPolicyFinderModule:log	Lorg/apache/commons/logging/Log;
    //   220: ldc_w 314
    //   223: invokeinterface 316 2 0
    //   228: aload_3
    //   229: ifnull +18 -> 247
    //   232: aload_0
    //   233: getfield 44	org/wso2/balana/finder/impl/FileBasedPolicyFinderModule:policies	Ljava/util/Map;
    //   236: aload_3
    //   237: invokevirtual 318	org/wso2/balana/AbstractPolicy:getId	()Ljava/net/URI;
    //   240: aload_3
    //   241: invokeinterface 322 3 0
    //   246: pop
    //   247: aload_3
    //   248: areturn
    // Line number table:
    //   Java source line #211	-> byte code offset #0
    //   Java source line #212	-> byte code offset #2
    //   Java source line #216	-> byte code offset #5
    //   Java source line #217	-> byte code offset #10
    //   Java source line #218	-> byte code offset #16
    //   Java source line #219	-> byte code offset #22
    //   Java source line #222	-> byte code offset #28
    //   Java source line #223	-> byte code offset #35
    //   Java source line #224	-> byte code offset #45
    //   Java source line #227	-> byte code offset #54
    //   Java source line #228	-> byte code offset #63
    //   Java source line #230	-> byte code offset #72
    //   Java source line #231	-> byte code offset #83
    //   Java source line #232	-> byte code offset #89
    //   Java source line #233	-> byte code offset #103
    //   Java source line #235	-> byte code offset #110
    //   Java source line #237	-> byte code offset #115
    //   Java source line #239	-> byte code offset #142
    //   Java source line #241	-> byte code offset #147
    //   Java source line #242	-> byte code offset #152
    //   Java source line #243	-> byte code offset #157
    //   Java source line #238	-> byte code offset #171
    //   Java source line #239	-> byte code offset #173
    //   Java source line #241	-> byte code offset #178
    //   Java source line #242	-> byte code offset #183
    //   Java source line #243	-> byte code offset #188
    //   Java source line #246	-> byte code offset #199
    //   Java source line #239	-> byte code offset #202
    //   Java source line #241	-> byte code offset #207
    //   Java source line #242	-> byte code offset #212
    //   Java source line #243	-> byte code offset #217
    //   Java source line #248	-> byte code offset #228
    //   Java source line #249	-> byte code offset #232
    //   Java source line #252	-> byte code offset #247
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	249	0	this	FileBasedPolicyFinderModule
    //   0	249	1	policyFile	String
    //   0	249	2	finder	PolicyFinder
    //   1	247	3	policy	AbstractPolicy
    //   3	205	4	stream	java.io.InputStream
    //   8	21	5	factory	javax.xml.parsers.DocumentBuilderFactory
    //   113	23	5	e	Exception
    //   33	13	6	db	javax.xml.parsers.DocumentBuilder
    //   52	3	7	doc	org.w3c.dom.Document
    //   61	43	8	root	org.w3c.dom.Element
    //   70	23	9	name	String
    //   171	29	10	localObject	Object
    //   155	3	11	e	java.io.IOException
    //   186	3	11	e	java.io.IOException
    //   215	3	11	e	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   5	110	113	java/lang/Exception
    //   147	152	155	java/io/IOException
    //   5	142	171	finally
    //   178	183	186	java/io/IOException
    //   207	212	215	java/io/IOException
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.impl.FileBasedPolicyFinderModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
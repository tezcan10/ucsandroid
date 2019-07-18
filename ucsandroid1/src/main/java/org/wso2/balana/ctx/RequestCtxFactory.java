package org.wso2.balana.ctx;

import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;

public class RequestCtxFactory
{
  private static volatile RequestCtxFactory factoryInstance;
  private static Log log = LogFactory.getLog(RequestCtxFactory.class);
  
  public AbstractRequestCtx getRequestCtx(Node root)
    throws ParsingException
  {
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null)
    {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return org.wso2.balana.ctx.xacml3.RequestCtx.getInstance(root);
      }
      if (("urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim())) || 
        ("urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim()))) {
        return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
      }
      throw new ParsingException("Invalid namespace in XACML request");
    }
    log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
    return org.wso2.balana.ctx.xacml3.RequestCtx.getInstance(root);
  }
  
  public AbstractRequestCtx getRequestCtx(String request)
    throws ParsingException
  {
    Node root = getXacmlRequest(request);
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null)
    {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return org.wso2.balana.ctx.xacml3.RequestCtx.getInstance(root);
      }
      if (("urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim())) || 
        ("urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim()))) {
        return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
      }
      throw new ParsingException("Invalid namespace in XACML request");
    }
    log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
    return org.wso2.balana.ctx.xacml3.RequestCtx.getInstance(root);
  }
  
  public AbstractRequestCtx getRequestCtx(InputStream input)
    throws ParsingException
  {
    Node root = InputParser.parseInput(input, "Request");
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null)
    {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return org.wso2.balana.ctx.xacml3.RequestCtx.getInstance(root);
      }
      if (("urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim())) || 
        ("urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim()))) {
        return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
      }
      throw new ParsingException("Invalid namespace in XACML request");
    }
    log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
    return org.wso2.balana.ctx.xacml3.RequestCtx.getInstance(root);
  }
  
  public static RequestCtxFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (RequestCtxFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new RequestCtxFactory();
        }
      }
    }
    return factoryInstance;
  }
  
  /* Error */
  public org.w3c.dom.Element getXacmlRequest(String request)
    throws ParsingException
  {
    // Byte code:
    //   0: new 102	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_1
    //   5: invokevirtual 104	java/lang/String:getBytes	()[B
    //   8: invokespecial 108	java/io/ByteArrayInputStream:<init>	([B)V
    //   11: astore_2
    //   12: invokestatic 111	javax/xml/parsers/DocumentBuilderFactory:newInstance	()Ljavax/xml/parsers/DocumentBuilderFactory;
    //   15: astore_3
    //   16: aload_3
    //   17: iconst_1
    //   18: invokevirtual 117	javax/xml/parsers/DocumentBuilderFactory:setNamespaceAware	(Z)V
    //   21: aload_3
    //   22: invokevirtual 121	javax/xml/parsers/DocumentBuilderFactory:newDocumentBuilder	()Ljavax/xml/parsers/DocumentBuilder;
    //   25: aload_2
    //   26: invokevirtual 125	javax/xml/parsers/DocumentBuilder:parse	(Ljava/io/InputStream;)Lorg/w3c/dom/Document;
    //   29: astore 4
    //   31: goto +39 -> 70
    //   34: astore 5
    //   36: new 29	org/wso2/balana/ParsingException
    //   39: dup
    //   40: ldc -125
    //   42: invokespecial 65	org/wso2/balana/ParsingException:<init>	(Ljava/lang/String;)V
    //   45: athrow
    //   46: astore 6
    //   48: aload_2
    //   49: invokevirtual 133	java/io/ByteArrayInputStream:close	()V
    //   52: goto +15 -> 67
    //   55: astore 7
    //   57: getstatic 18	org/wso2/balana/ctx/RequestCtxFactory:log	Lorg/apache/commons/logging/Log;
    //   60: ldc -120
    //   62: invokeinterface 138 2 0
    //   67: aload 6
    //   69: athrow
    //   70: aload_2
    //   71: invokevirtual 133	java/io/ByteArrayInputStream:close	()V
    //   74: goto +15 -> 89
    //   77: astore 7
    //   79: getstatic 18	org/wso2/balana/ctx/RequestCtxFactory:log	Lorg/apache/commons/logging/Log;
    //   82: ldc -120
    //   84: invokeinterface 138 2 0
    //   89: aload 4
    //   91: invokeinterface 141 1 0
    //   96: areturn
    // Line number table:
    //   Java source line #171	-> byte code offset #0
    //   Java source line #172	-> byte code offset #12
    //   Java source line #173	-> byte code offset #16
    //   Java source line #176	-> byte code offset #21
    //   Java source line #177	-> byte code offset #31
    //   Java source line #178	-> byte code offset #36
    //   Java source line #179	-> byte code offset #46
    //   Java source line #181	-> byte code offset #48
    //   Java source line #182	-> byte code offset #52
    //   Java source line #183	-> byte code offset #57
    //   Java source line #185	-> byte code offset #67
    //   Java source line #181	-> byte code offset #70
    //   Java source line #182	-> byte code offset #74
    //   Java source line #183	-> byte code offset #79
    //   Java source line #186	-> byte code offset #89
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	RequestCtxFactory
    //   0	97	1	request	String
    //   11	60	2	inputStream	java.io.ByteArrayInputStream
    //   15	7	3	dbf	javax.xml.parsers.DocumentBuilderFactory
    //   29	3	4	doc	org.w3c.dom.Document
    //   70	1	4	doc	org.w3c.dom.Document
    //   89	1	4	doc	org.w3c.dom.Document
    //   34	3	5	e	Exception
    //   46	22	6	localObject	Object
    //   55	3	7	e	java.io.IOException
    //   77	3	7	e	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   21	31	34	java/lang/Exception
    //   21	46	46	finally
    //   48	52	55	java/io/IOException
    //   70	74	77	java/io/IOException
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.RequestCtxFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
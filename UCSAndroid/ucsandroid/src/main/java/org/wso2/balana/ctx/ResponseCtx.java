package org.wso2.balana.ctx;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;

public class ResponseCtx
{
  private Set<AbstractResult> results = null;
  
  public ResponseCtx(AbstractResult result)
  {
    results = new HashSet();
    results.add(result);
  }
  
  public ResponseCtx(Set<AbstractResult> results)
  {
    this.results = Collections.unmodifiableSet(new HashSet(results));
  }
  
  public static ResponseCtx getInstance(Node root)
    throws ParsingException
  {
    String requestCtxNs = root.getNamespaceURI();
    if (requestCtxNs != null)
    {
      if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(requestCtxNs.trim())) {
        return getInstance(root, 3);
      }
      if (("urn:oasis:names:tc:xacml:1.0:context".equals(requestCtxNs.trim())) || 
        ("urn:oasis:names:tc:xacml:2.0:context:schema:os".equals(requestCtxNs.trim()))) {
        return getInstance(root, 2);
      }
      throw new ParsingException("Invalid namespace in XACML response");
    }
    return getInstance(root, 3);
  }
  
  public static ResponseCtx getInstance(Node root, int version)
    throws ParsingException
  {
    Set<AbstractResult> results = new HashSet();
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Result")) {
        if (version == 3) {
          results.add(org.wso2.balana.ctx.xacml3.Result.getInstance(node));
        } else {
          results.add(org.wso2.balana.ctx.xacml2.Result.getInstance(node));
        }
      }
    }
    if (results.size() == 0) {
      throw new ParsingException("must have at least one Result");
    }
    return new ResponseCtx(results);
  }
  
  public Set<AbstractResult> getResults()
  {
    return results;
  }
  
  public String getEncoded()
  {
    OutputStream output = new ByteArrayOutputStream();
    encode(output, new Indenter(0));
    return output.toString();
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    
    String indent = indenter.makeString();
    
    out.println(indent + "<Response>");
    
    Iterator it = results.iterator();
    indenter.in();
    while (it.hasNext())
    {
      AbstractResult result = (AbstractResult)it.next();
      result.encode(out, indenter);
    }
    indenter.out();
    
    out.println(indent + "</Response>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.ResponseCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.xacml3;

import java.io.OutputStream;
import java.io.PrintStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;

public class RequestDefaults
{
  private String xPathVersion;
  
  public RequestDefaults(String xPathVersion)
  {
    this.xPathVersion = xPathVersion;
  }
  
  public static RequestDefaults getInstance(Node root)
  {
    String xPathVersion = null;
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if ("XPathVersion".equals(node.getNodeName())) {
        xPathVersion = node.getFirstChild().getNodeValue();
      }
    }
    return new RequestDefaults(xPathVersion);
  }
  
  public String getXPathVersion()
  {
    return xPathVersion;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    String indent = indenter.makeString();
    PrintStream out = new PrintStream(output);
    
    out.println(indent + "<RequestDefaults>");
    if (xPathVersion != null)
    {
      indenter.in();
      out.println(indent + "<XPathVersion>" + xPathVersion + "</XPathVersion>");
      indenter.out();
    }
    out.println(indent + "</RequestDefaults>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.xacml3.RequestDefaults
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
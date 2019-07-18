package org.wso2.balana.ctx;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;

public class Status
{
  public static final String STATUS_OK = "urn:oasis:names:tc:xacml:1.0:status:ok";
  public static final String STATUS_MISSING_ATTRIBUTE = "urn:oasis:names:tc:xacml:1.0:status:missing-attribute";
  public static final String STATUS_SYNTAX_ERROR = "urn:oasis:names:tc:xacml:1.0:status:syntax-error";
  public static final String STATUS_PROCESSING_ERROR = "urn:oasis:names:tc:xacml:1.0:status:processing-error";
  private List<String> code;
  private String message;
  private StatusDetail detail;
  private static Status okStatus;
  
  static
  {
    List<String> code = new ArrayList();
    code.add("urn:oasis:names:tc:xacml:1.0:status:ok");
    okStatus = new Status(code);
  }
  
  public Status(List<String> code)
  {
    this(code, null, null);
  }
  
  public Status(List code, String message)
  {
    this(code, message, null);
  }
  
  public Status(List<String> code, String message, StatusDetail detail)
    throws IllegalArgumentException
  {
    if (detail != null)
    {
      String c = (String)code.iterator().next();
      if ((c.equals("urn:oasis:names:tc:xacml:1.0:status:ok")) || (c.equals("urn:oasis:names:tc:xacml:1.0:status:syntax-error")) || 
        (c.equals("urn:oasis:names:tc:xacml:1.0:status:processing-error"))) {
        throw new IllegalArgumentException("status detail cannot be included with " + 
          c);
      }
    }
    this.code = Collections.unmodifiableList(new ArrayList(code));
    this.message = message;
    this.detail = detail;
  }
  
  public List<String> getCode()
  {
    return code;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public StatusDetail getDetail()
  {
    return detail;
  }
  
  public static Status getOkInstance()
  {
    return okStatus;
  }
  
  public static Status getInstance(Node root)
    throws ParsingException
  {
    List<String> code = null;
    String message = null;
    StatusDetail detail = null;
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      String name = node.getNodeName();
      if (name.equals("StatusCode")) {
        code = parseStatusCode(node);
      } else if (name.equals("StatusMessage")) {
        message = node.getFirstChild().getNodeValue();
      } else if (name.equals("StatusDetail")) {
        detail = StatusDetail.getInstance(node);
      }
    }
    if (code == null) {
      throw new ParsingException("Missing required element StatusCode in StatusType");
    }
    return new Status(code, message, detail);
  }
  
  private static List<String> parseStatusCode(Node root)
  {
    String val = root.getAttributes().getNamedItem("Value").getNodeValue();
    List<String> code = new ArrayList();
    code.add(val);
    
    NodeList list = ((Element)root).getElementsByTagName("StatusCode");
    for (int i = 0; i < list.getLength(); i++)
    {
      Node node = list.item(i);
      code.add(node.getAttributes().getNamedItem("Value").getNodeValue());
    }
    return code;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<Status>");
    
    indenter.in();
    
    encodeStatusCode(out, indenter, code.iterator());
    if (message != null) {
      out.println(indenter.makeString() + "<StatusMessage>" + message + "</StatusMessage>");
    }
    if (detail != null) {
      out.println(detail.getEncoded());
    }
    indenter.out();
    
    out.println(indent + "</Status>");
  }
  
  private void encodeStatusCode(PrintStream out, Indenter indenter, Iterator iterator)
  {
    String in = indenter.makeString();
    String code = (String)iterator.next();
    if (iterator.hasNext())
    {
      indenter.in();
      out.println(in + "<StatusCode Value=\"" + code + "\">");
      encodeStatusCode(out, indenter, iterator);
      out.println(in + "</StatusCode>");
      indenter.out();
    }
    else
    {
      out.println(in + "<StatusCode Value=\"" + code + "\"/>");
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.Status
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
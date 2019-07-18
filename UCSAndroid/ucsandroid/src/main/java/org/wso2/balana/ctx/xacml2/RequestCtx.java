package org.wso2.balana.ctx.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.xacml3.Attributes;

public class RequestCtx
  extends AbstractRequestCtx
{
  private String resourceContent;
  private Set<Subject> subjects = null;
  private Set resource = null;
  private Set action = null;
  private Set environment = null;
  
  public RequestCtx(Set<Attributes> attributesSet, Node documentRoot)
  {
    this(attributesSet, documentRoot, null);
  }
  
  public RequestCtx(Set<Attributes> attributesSet, Node documentRoot, int version)
  {
    this(attributesSet, documentRoot, null);
  }
  
  public RequestCtx(Set<Attributes> attributesSet, String resourceContent)
  {
    this(attributesSet, null, resourceContent);
  }
  
  public RequestCtx(Set<Attributes> attributesSet, Node documentRoot, String resourceContent)
    throws IllegalArgumentException
  {
    this.attributesSet = attributesSet;
    this.documentRoot = documentRoot;
    this.resourceContent = resourceContent;
    xacmlVersion = 2;
  }
  
  public RequestCtx(Set<Subject> subjects, Set<Attribute> resource, Set<Attribute> action, Set<Attribute> environment)
    throws IllegalArgumentException
  {
    this(null, null, subjects, resource, action, environment, null);
  }
  
  public RequestCtx(Set<Attributes> attributesSet, Node documentRoot, Set<Subject> subjects, Set<Attribute> resource, Set<Attribute> action, Set<Attribute> environment, String resourceContent)
    throws IllegalArgumentException
  {
    this.attributesSet = attributesSet;
    this.documentRoot = documentRoot;
    this.subjects = subjects;
    this.resource = resource;
    this.action = action;
    this.environment = environment;
    this.resourceContent = resourceContent;
    xacmlVersion = 2;
  }
  
  public static RequestCtx getInstance(Node root)
    throws ParsingException
  {
    Set<Subject> newSubjects = new HashSet();
    Set<Attributes> attributesSet = new HashSet();
    Node content = null;
    Set<Attribute> newResource = null;
    Set<Attribute> newAction = null;
    Set<Attribute> newEnvironment = null;
    
    String tagName = root.getNodeName();
    if (!tagName.equals("Request")) {
      throw new ParsingException("Request cannot be constructed using type: " + 
        root.getNodeName());
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node node = children.item(i);
      String tag = node.getNodeName();
      if (tag.equals("Subject"))
      {
        Node catNode = node.getAttributes().getNamedItem("SubjectCategory");
        URI category = null;
        if (catNode != null) {
          try
          {
            category = new URI(catNode.getNodeValue());
          }
          catch (Exception e)
          {
            throw new ParsingException("Invalid Category URI", e);
          }
        }
        Set<Attribute> attributes = parseAttributes(node);
        
        newSubjects.add(new Subject(category, attributes));
        
        attributesSet.add(new Attributes(category, null, attributes, null));
        if (newSubjects.size() < 1) {
          throw new ParsingException("Request must a contain subject");
        }
      }
      else if (tag.equals("Resource"))
      {
        NodeList nodes = node.getChildNodes();
        for (int j = 0; j < nodes.getLength(); j++)
        {
          Node child = nodes.item(j);
          if (node.getNodeName().equals("ResourceContent"))
          {
            if (content != null) {
              throw new ParsingException("Too many resource content elements are defined.");
            }
            content = node;
          }
        }
        newResource = parseAttributes(node);
        attributesSet.add(new Attributes(null, content, newResource, null));
      }
      else if (tag.equals("Action"))
      {
        newAction = parseAttributes(node);
        attributesSet.add(new Attributes(null, content, newAction, null));
      }
      else if (tag.equals("Environment"))
      {
        newEnvironment = parseAttributes(node);
        attributesSet.add(new Attributes(null, content, newEnvironment, null));
      }
    }
    if (newEnvironment == null)
    {
      newEnvironment = new HashSet();
      attributesSet.add(new Attributes(null, content, newEnvironment, null));
    }
    return new RequestCtx(attributesSet, root, newSubjects, newResource, 
      newAction, newEnvironment, null);
  }
  
  private static Set<Attribute> parseAttributes(Node root)
    throws ParsingException
  {
    Set<Attribute> set = new HashSet();
    
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("Attribute")) {
        set.add(Attribute.getInstance(node, 2));
      }
    }
    return set;
  }
  
  public Set getSubjects()
  {
    return subjects;
  }
  
  public Set getResource()
  {
    return resource;
  }
  
  public Set getAction()
  {
    return action;
  }
  
  public Set getEnvironmentAttributes()
  {
    return environment;
  }
  
  public Node getDocumentRoot()
  {
    return documentRoot;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    
    String topIndent = indenter.makeString();
    out.println(topIndent + "<Request xmlns=\"" + "urn:oasis:names:tc:xacml:2.0:resource:scope" + "\" >");
    
    indenter.in();
    String indent = indenter.makeString();
    
    indenter.in();
    
    Iterator it = subjects.iterator();
    while (it.hasNext())
    {
      Subject subject = (Subject)it.next();
      
      out.print(indent + "<Subject SubjectCategory=\"" + subject.getCategory().toString() + 
        "\"");
      
      Set subjectAttrs = subject.getAttributes();
      if (subjectAttrs.size() == 0)
      {
        out.println("/>");
      }
      else
      {
        out.println(">");
        
        encodeAttributes(subjectAttrs, out, indenter);
        
        out.println(indent + "</Subject>");
      }
    }
    if ((resource.size() != 0) || (resourceContent != null))
    {
      out.println(indent + "<Resource>");
      if (resourceContent != null) {
        out.println(indenter.makeString() + "<ResourceContent>" + resourceContent + 
          "</ResourceContent>");
      }
      encodeAttributes(resource, out, indenter);
      out.println(indent + "</Resource>");
    }
    else
    {
      out.println(indent + "<Resource/>");
    }
    if (action.size() != 0)
    {
      out.println(indent + "<Action>");
      encodeAttributes(action, out, indenter);
      out.println(indent + "</Action>");
    }
    else
    {
      out.println(indent + "<Action/>");
    }
    if (environment.size() != 0)
    {
      out.println(indent + "<Environment>");
      encodeAttributes(environment, out, indenter);
      out.println(indent + "</Environment>");
    }
    indenter.out();
    indenter.out();
    
    out.println(topIndent + "</Request>");
  }
  
  private void encodeAttributes(Set attributes, PrintStream out, Indenter indenter)
  {
    Iterator it = attributes.iterator();
    while (it.hasNext())
    {
      Attribute attr = (Attribute)it.next();
      attr.encode(out, indenter);
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml2.RequestCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
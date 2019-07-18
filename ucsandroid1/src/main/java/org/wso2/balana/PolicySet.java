package org.wso2.balana;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.combine.CombinerParameter;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.finder.PolicyFinder;

public class PolicySet
  extends AbstractPolicy
{
  public PolicySet(URI id, PolicyCombiningAlgorithm combiningAlg, AbstractTarget target)
  {
    this(id, null, combiningAlg, null, target, null, null, null);
  }
  
  public PolicySet(URI id, PolicyCombiningAlgorithm combiningAlg, AbstractTarget target, List policies)
  {
    this(id, null, combiningAlg, null, target, policies, null, null);
  }
  
  public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg, String description, AbstractTarget target, List policies)
  {
    this(id, version, combiningAlg, description, target, policies, null, null);
  }
  
  public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg, String description, AbstractTarget target, List policies, String defaultVersion)
  {
    this(id, version, combiningAlg, description, target, policies, defaultVersion, null);
  }
  
  public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg, String description, AbstractTarget target, List policies, String defaultVersion, Set obligations)
  {
    super(id, version, combiningAlg, description, target, defaultVersion, obligations, null, null);
    
    List list = null;
    if (policies != null)
    {
      list = new ArrayList();
      Iterator it = policies.iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (!(o instanceof AbstractPolicy)) {
          throw new IllegalArgumentException("non-AbstractPolicy in policies");
        }
        list.add(new PolicyCombinerElement((AbstractPolicy)o));
      }
    }
    setChildren(list);
  }
  
  public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg, String description, AbstractTarget target, List policyElements, String defaultVersion, Set obligations, List parameters)
  {
    super(id, version, combiningAlg, description, target, defaultVersion, obligations, null, parameters);
    if (policyElements != null)
    {
      Iterator it = policyElements.iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (!(o instanceof PolicyCombinerElement)) {
          throw new IllegalArgumentException("non-AbstractPolicy in policies");
        }
      }
    }
    setChildren(policyElements);
  }
  
  private PolicySet(Node root, PolicyFinder finder)
    throws ParsingException
  {
    super(root, "PolicySet", "PolicyCombiningAlgId");
    
    List policies = new ArrayList();
    HashMap policyParameters = new HashMap();
    HashMap policySetParameters = new HashMap();
    PolicyMetaData metaData = getMetaData();
    
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("PolicySet")) {
        policies.add(getInstance(child, finder));
      } else if (name.equals("Policy")) {
        policies.add(Policy.getInstance(child));
      } else if (name.equals("PolicySetIdReference")) {
        policies.add(PolicyReference.getInstance(child, finder, metaData));
      } else if (name.equals("PolicyIdReference")) {
        policies.add(PolicyReference.getInstance(child, finder, metaData));
      } else if (name.equals("PolicyCombinerParameters")) {
        paramaterHelper(policyParameters, child, "Policy");
      } else if (name.equals("PolicySetCombinerParameters")) {
        paramaterHelper(policySetParameters, child, "PolicySet");
      }
    }
    List elements = new ArrayList();
    Iterator it = policies.iterator();
    while (it.hasNext())
    {
      AbstractPolicy policy = (AbstractPolicy)it.next();
      List list = null;
      if ((policy instanceof Policy))
      {
        list = (List)policyParameters.remove(policy.getId().toString());
      }
      else if ((policy instanceof PolicySet))
      {
        list = (List)policySetParameters.remove(policy.getId().toString());
      }
      else
      {
        PolicyReference ref = (PolicyReference)policy;
        String id = ref.getReference().toString();
        if (ref.getReferenceType() == 0) {
          list = (List)policyParameters.remove(id);
        } else {
          list = (List)policySetParameters.remove(id);
        }
      }
      elements.add(new PolicyCombinerElement(policy, list));
    }
    if (!policyParameters.isEmpty()) {
      throw new ParsingException("Unmatched parameters in Policy");
    }
    if (!policySetParameters.isEmpty()) {
      throw new ParsingException("Unmatched parameters in PolicySet");
    }
    setChildren(elements);
  }
  
  private void paramaterHelper(HashMap parameters, Node root, String prefix)
    throws ParsingException
  {
    String ref = root.getAttributes().getNamedItem(prefix + "IdRef").getNodeValue();
    if (parameters.containsKey(ref))
    {
      List list = (List)parameters.get(ref);
      parseParameters(list, root);
    }
    else
    {
      List list = new ArrayList();
      parseParameters(list, root);
      parameters.put(ref, list);
    }
  }
  
  private void parseParameters(List parameters, Node root)
    throws ParsingException
  {
    NodeList nodes = root.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeName().equals("CombinerParameter")) {
        parameters.add(CombinerParameter.getInstance(node));
      }
    }
  }
  
  public static PolicySet getInstance(Node root)
    throws ParsingException
  {
    return getInstance(root, null);
  }
  
  public static PolicySet getInstance(Node root, PolicyFinder finder)
    throws ParsingException
  {
    if (!root.getNodeName().equals("PolicySet")) {
      throw new ParsingException("Cannot create PolicySet from root of type " + 
        root.getNodeName());
    }
    return new PolicySet(root, finder);
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<PolicySet PolicySetId=\"" + getId().toString() + 
      "\" PolicyCombiningAlgId=\"" + getCombiningAlg().getIdentifier().toString() + 
      "\">");
    
    indenter.in();
    String nextIndent = indenter.makeString();
    
    String description = getDescription();
    if (description != null) {
      out.println(nextIndent + "<Description>" + description + "</Description>");
    }
    String version = getDefaultVersion();
    if (version != null) {
      out.println("<PolicySetDefaults><XPathVersion>" + version + 
        "</XPathVersion></PolicySetDefaults>");
    }
    encodeCommonElements(output, indenter);
    
    indenter.out();
    out.println(indent + "</PolicySet>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.PolicySet
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
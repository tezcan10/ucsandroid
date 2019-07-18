package org.wso2.balana;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.combine.CombinerParameter;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.VariableDefinition;
import org.wso2.balana.cond.VariableManager;

public class Policy
  extends AbstractPolicy
{
  private Set definitions;
  
  public Policy(URI id, RuleCombiningAlgorithm combiningAlg, AbstractTarget target)
  {
    this(id, null, combiningAlg, null, target, null, null, null);
  }
  
  public Policy(URI id, RuleCombiningAlgorithm combiningAlg, AbstractTarget target, List rules)
  {
    this(id, null, combiningAlg, null, target, null, rules, null);
  }
  
  public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description, AbstractTarget target, List rules)
  {
    this(id, version, combiningAlg, description, target, null, rules, null);
  }
  
  public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description, AbstractTarget target, String defaultVersion, List rules)
  {
    this(id, version, combiningAlg, description, target, defaultVersion, rules, null);
  }
  
  public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description, AbstractTarget target, String defaultVersion, List rules, Set obligations)
  {
    this(id, version, combiningAlg, description, target, defaultVersion, rules, obligations, null);
  }
  
  public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description, AbstractTarget target, String defaultVersion, List rules, Set obligations, Set definitions)
  {
    super(id, version, combiningAlg, description, target, defaultVersion, obligations, null, null);
    
    List list = null;
    if (rules != null)
    {
      list = new ArrayList();
      Iterator it = rules.iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (!(o instanceof Rule)) {
          throw new IllegalArgumentException("non-Rule in rules");
        }
        list.add(new RuleCombinerElement((Rule)o));
      }
    }
    setChildren(list);
    if (definitions == null) {
      this.definitions = Collections.EMPTY_SET;
    } else {
      this.definitions = Collections.unmodifiableSet(new HashSet(definitions));
    }
  }
  
  public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description, AbstractTarget target, String defaultVersion, List ruleElements, Set obligations, Set definitions, List parameters)
  {
    super(id, version, combiningAlg, description, target, defaultVersion, obligations, null, parameters);
    if (ruleElements != null)
    {
      Iterator it = ruleElements.iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (!(o instanceof RuleCombinerElement)) {
          throw new IllegalArgumentException("non-Rule in rules");
        }
      }
    }
    setChildren(ruleElements);
    if (definitions == null) {
      this.definitions = Collections.EMPTY_SET;
    } else {
      this.definitions = Collections.unmodifiableSet(new HashSet(definitions));
    }
  }
  
  private Policy(Node root)
    throws ParsingException
  {
    super(root, "Policy", "RuleCombiningAlgId");
    
    List rules = new ArrayList();
    HashMap parameters = new HashMap();
    HashMap variableIds = new HashMap();
    PolicyMetaData metaData = getMetaData();
    
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeName().equals("VariableDefinition"))
      {
        String id = child.getAttributes().getNamedItem("VariableId").getNodeValue();
        if (variableIds.containsKey(id)) {
          throw new ParsingException("multiple definitions for variable " + id);
        }
        variableIds.put(id, child);
      }
    }
    VariableManager manager = new VariableManager(variableIds, metaData);
    definitions = new HashSet();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("Rule"))
      {
        rules.add(Rule.getInstance(child, metaData, manager));
      }
      else if (name.equals("RuleCombinerParameters"))
      {
        String ref = child.getAttributes().getNamedItem("RuleIdRef").getNodeValue();
        if (parameters.containsKey(ref))
        {
          List list = (List)parameters.get(ref);
          parseParameters(list, child);
        }
        else
        {
          List list = new ArrayList();
          parseParameters(list, child);
          parameters.put(ref, list);
        }
      }
      else if (name.equals("VariableDefinition"))
      {
        String id = child.getAttributes().getNamedItem("VariableId").getNodeValue();
        
        definitions.add(manager.getDefinition(id));
      }
    }
    definitions = Collections.unmodifiableSet(definitions);
    
    List elements = new ArrayList();
    Iterator it = rules.iterator();
    while (it.hasNext())
    {
      Rule rule = (Rule)it.next();
      String id = rule.getId().toString();
      List list = (List)parameters.remove(id);
      
      elements.add(new RuleCombinerElement(rule, list));
    }
    if (!parameters.isEmpty()) {
      throw new ParsingException("Unmatched parameters in Rule");
    }
    setChildren(elements);
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
  
  public static Policy getInstance(Node root)
    throws ParsingException
  {
    if (!root.getNodeName().equals("Policy")) {
      throw new ParsingException("Cannot create Policy from root of type " + 
        root.getNodeName());
    }
    return new Policy(root);
  }
  
  public Set getVariableDefinitions()
  {
    return definitions;
  }
  
  public void encode(OutputStream output)
  {
    encode(output, new Indenter(0));
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<Policy PolicyId=\"" + getId().toString() + 
      "\" RuleCombiningAlgId=\"" + getCombiningAlg().getIdentifier().toString() + "\">");
    
    indenter.in();
    String nextIndent = indenter.makeString();
    
    String description = getDescription();
    if (description != null) {
      out.println(nextIndent + "<Description>" + description + "</Description>");
    }
    String version = getDefaultVersion();
    if (version != null) {
      out.println("<PolicyDefaults><XPathVersion>" + version + 
        "</XPathVersion></PolicyDefaults>");
    }
    Iterator it = definitions.iterator();
    while (it.hasNext()) {
      ((VariableDefinition)it.next()).encode(output, indenter);
    }
    encodeCommonElements(output, indenter);
    
    indenter.out();
    out.println(indent + "</Policy>");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.Policy
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
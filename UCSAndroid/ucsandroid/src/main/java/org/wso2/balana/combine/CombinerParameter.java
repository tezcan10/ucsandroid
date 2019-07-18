package org.wso2.balana.combine;

import java.io.OutputStream;
import java.io.PrintStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Balana;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;

public class CombinerParameter
{
  private String name;
  private AttributeValue value;
  
  public CombinerParameter(String name, AttributeValue value)
  {
    this.name = name;
    this.value = value;
  }
  
  public static CombinerParameter getInstance(Node root)
    throws ParsingException
  {
    String name = root.getAttributes().getNamedItem("ParameterName").getNodeValue();
    
    AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
    AttributeValue value = null;
    try
    {
      value = attrFactory.createValue(root.getFirstChild());
    }
    catch (UnknownIdentifierException uie)
    {
      throw new ParsingException(uie.getMessage(), uie);
    }
    return new CombinerParameter(name, value);
  }
  
  public String getName()
  {
    return name;
  }
  
  public AttributeValue getValue()
  {
    return value;
  }
  
  public void encode(OutputStream output, Indenter indenter)
  {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    
    out.println(indent + "<CombinerParameter ParameterName=\"" + getName() + "\">");
    indenter.in();
    
    getValue().encode(output, indenter);
    
    out.println(indent + "</CombinerParameter>");
    indenter.out();
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.combine.CombinerParameter
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
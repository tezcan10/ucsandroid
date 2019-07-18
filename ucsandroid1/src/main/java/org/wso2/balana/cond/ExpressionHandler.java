package org.wso2.balana.cond;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Balana;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeDesignatorFactory;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeSelectorFactory;

public class ExpressionHandler
{
  public static Expression parseExpression(Node root, PolicyMetaData metaData, VariableManager manager)
    throws ParsingException
  {
    String name = root.getNodeName();
    if (name.equals("Apply")) {
      return Apply.getInstance(root, metaData, manager);
    }
    if (name.equals("AttributeValue")) {
      try
      {
        return Balana.getInstance().getAttributeFactory().createValue(root);
      }
      catch (UnknownIdentifierException uie)
      {
        throw new ParsingException("Unknown DataType", uie);
      }
    }
    if ("AttributeDesignator".equals(name)) {
      return AttributeDesignatorFactory.getFactory().getAbstractDesignator(root, metaData);
    }
    if (name.equals("SubjectAttributeDesignator")) {
      return AttributeDesignatorFactory.getFactory().getAbstractDesignator(root, metaData);
    }
    if (name.equals("ResourceAttributeDesignator")) {
      return AttributeDesignatorFactory.getFactory().getAbstractDesignator(root, metaData);
    }
    if (name.equals("ActionAttributeDesignator")) {
      return AttributeDesignatorFactory.getFactory().getAbstractDesignator(root, metaData);
    }
    if (name.equals("EnvironmentAttributeDesignator")) {
      return AttributeDesignatorFactory.getFactory().getAbstractDesignator(root, metaData);
    }
    if (name.equals("AttributeSelector")) {
      return AttributeSelectorFactory.getFactory().getAbstractSelector(root, metaData);
    }
    if (name.equals("Function")) {
      return getFunction(root, metaData, FunctionFactory.getGeneralInstance());
    }
    if (name.equals("VariableReference")) {
      return VariableReference.getInstance(root, metaData, manager);
    }
    return null;
  }
  
  public static Function getFunction(Node root, PolicyMetaData metaData, FunctionFactory factory)
    throws ParsingException
  {
    Node functionNode = root.getAttributes().getNamedItem("FunctionId");
    String functionName = functionNode.getNodeValue();
    try
    {
      return factory.createFunction(functionName);
    }
    catch (UnknownIdentifierException uie)
    {
      throw new ParsingException("Unknown FunctionId", uie);
    }
    catch (FunctionTypeException fte)
    {
      try
      {
        FunctionFactory ff = FunctionFactory.getGeneralInstance();
        return ff.createAbstractFunction(functionName, root, metaData.getXPathIdentifier());
      }
      catch (Exception e)
      {
        throw new ParsingException("failed to create abstract function " + 
          functionName, e);
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.ExpressionHandler
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
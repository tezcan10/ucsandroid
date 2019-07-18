package org.wso2.balana;

import java.util.HashMap;
import org.w3c.dom.Node;
import org.wso2.balana.xacml2.Obligation;
import org.wso2.balana.xacml3.ObligationExpression;

public class ObligationFactory
{
  private HashMap<String, AbstractObligation> targetMap = new HashMap();
  private static volatile ObligationFactory factoryInstance;
  
  private static void init() {}
  
  public void registerObligation() {}
  
  public AbstractObligation getObligation(Node node, PolicyMetaData metaData)
    throws ParsingException
  {
    if (3 == metaData.getXACMLVersion()) {
      return ObligationExpression.getInstance(node, metaData);
    }
    return Obligation.getInstance(node);
  }
  
  public static ObligationFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (ObligationFactory.class)
      {
        if (factoryInstance == null) {
          factoryInstance = new ObligationFactory();
        }
      }
    }
    return factoryInstance;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ObligationFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
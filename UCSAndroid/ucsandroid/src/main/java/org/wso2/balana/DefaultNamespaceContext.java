package org.wso2.balana;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class DefaultNamespaceContext
  implements NamespaceContext
{
  private String prefix;
  private String namespaceURI;
  
  public DefaultNamespaceContext(String prefix, String namespaceURI)
  {
    this.prefix = prefix;
    this.namespaceURI = namespaceURI;
  }
  
  public String getNamespaceURI(String prefix)
  {
    if ((prefix != null) && (prefix.equals(this.prefix))) {
      return namespaceURI;
    }
    return null;
  }
  
  public String getPrefix(String namespaceURI)
  {
    if ((namespaceURI != null) && (namespaceURI.equals(this.namespaceURI))) {
      return prefix;
    }
    return null;
  }
  
  public Iterator getPrefixes(String namespaceURI)
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.DefaultNamespaceContext
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
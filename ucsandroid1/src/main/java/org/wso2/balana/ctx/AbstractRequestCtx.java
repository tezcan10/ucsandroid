package org.wso2.balana.ctx;

import java.io.OutputStream;
import java.util.Set;
import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.xacml3.Attributes;

public abstract class AbstractRequestCtx
{
  protected int xacmlVersion;
  protected Node documentRoot = null;
  protected Set<Attributes> attributesSet = null;
  protected boolean isSearch;
  
  public Node getDocumentRoot()
  {
    return documentRoot;
  }
  
  public void setSearch(boolean isSearch)
  {
    this.isSearch = isSearch;
  }
  
  public boolean isSearch()
  {
    return isSearch;
  }
  
  public int getXacmlVersion()
  {
    return xacmlVersion;
  }
  
  public void setXacmlVersion(int xacmlVersion)
  {
    this.xacmlVersion = xacmlVersion;
  }
  
  public Set<Attributes> getAttributesSet()
  {
    return attributesSet;
  }
  
  public abstract void encode(OutputStream paramOutputStream, Indenter paramIndenter);
  
  public abstract void encode(OutputStream paramOutputStream);
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.AbstractRequestCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
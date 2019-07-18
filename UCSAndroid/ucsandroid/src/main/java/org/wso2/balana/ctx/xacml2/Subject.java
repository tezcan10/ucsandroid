package org.wso2.balana.ctx.xacml2;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Subject
{
  private URI category;
  private Set attributes;
  public static final URI DEFAULT_CATEGORY;
  private static RuntimeException earlyException = null;
  
  static
  {
    URI defaultURI = null;
    try
    {
      defaultURI = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
    }
    catch (Exception e)
    {
      earlyException = new IllegalArgumentException("invalid URI");
      earlyException.initCause(e);
    }
    DEFAULT_CATEGORY = defaultURI;
  }
  
  public Subject(Set attributes)
  {
    this(null, attributes);
    if (earlyException != null) {
      throw earlyException;
    }
  }
  
  public Subject(URI category, Set attributes)
  {
    if (category == null) {
      this.category = DEFAULT_CATEGORY;
    } else {
      this.category = category;
    }
    this.attributes = Collections.unmodifiableSet(new HashSet(attributes));
  }
  
  public URI getCategory()
  {
    return category;
  }
  
  public Set getAttributes()
  {
    return attributes;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.xacml2.Subject
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.ctx;

import java.net.URI;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.finder.AttributeFinder;

public abstract class BasicEvaluationCtx
  implements EvaluationCtx
{
  protected DateAttribute currentDate;
  protected TimeAttribute currentTime;
  protected DateTimeAttribute currentDateTime;
  protected boolean useCachedEnvValues = false;
  protected Node requestRoot;
  protected AbstractRequestCtx requestCtx;
  protected PDPConfig pdpConfig;
  private static Log logger = LogFactory.getLog(BasicEvaluationCtx.class);
  
  public Node getRequestRoot()
  {
    return requestRoot;
  }
  
  public boolean isSearching()
  {
    return false;
  }
  
  public synchronized TimeAttribute getCurrentTime()
  {
    long millis = dateTimeHelper();
    if (useCachedEnvValues) {
      return currentTime;
    }
    return new TimeAttribute(new Date(millis));
  }
  
  public synchronized DateAttribute getCurrentDate()
  {
    long millis = dateTimeHelper();
    if (useCachedEnvValues) {
      return currentDate;
    }
    return new DateAttribute(new Date(millis));
  }
  
  public synchronized DateTimeAttribute getCurrentDateTime()
  {
    long millis = dateTimeHelper();
    if (useCachedEnvValues) {
      return currentDateTime;
    }
    return new DateTimeAttribute(new Date(millis));
  }
  
  public AbstractRequestCtx getRequestCtx()
  {
    return requestCtx;
  }
  
  public EvaluationResult getAttribute(String path, URI type, URI category, URI contextSelector, String xpathVersion)
  {
    if (pdpConfig.getAttributeFinder() != null) {
      return pdpConfig.getAttributeFinder().findAttribute(path, type, this, 
        xpathVersion);
    }
    logger.warn("Context tried to invoke AttributeFinder but was not configured with one");
    
    return new EvaluationResult(BagAttribute.createEmptyBag(type));
  }
  
  private long dateTimeHelper()
  {
    if (currentTime != null) {
      return -1L;
    }
    Date time = new Date();
    long millis = time.getTime();
    if (!useCachedEnvValues) {
      return millis;
    }
    currentTime = new TimeAttribute(time);
    currentDate = new DateAttribute(new Date(millis));
    currentDateTime = new DateTimeAttribute(new Date(millis));
    
    return -1L;
  }
  
  protected EvaluationResult callHelper(URI type, URI id, String issuer, URI category)
  {
    if (pdpConfig.getAttributeFinder() != null) {
      return pdpConfig.getAttributeFinder().findAttribute(type, id, issuer, category, this);
    }
    if (logger.isWarnEnabled()) {
      logger.warn("Context tried to invoke AttributeFinder but was not configured with one");
    }
    return new EvaluationResult(BagAttribute.createEmptyBag(type));
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.BasicEvaluationCtx
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
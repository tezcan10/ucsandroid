package org.wso2.balana.attr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.proxy.AnyURIAttributeProxy;
import org.wso2.balana.attr.proxy.Base64BinaryAttributeProxy;
import org.wso2.balana.attr.proxy.BooleanAttributeProxy;
import org.wso2.balana.attr.proxy.DNSNameAttributeProxy;
import org.wso2.balana.attr.proxy.DateAttributeProxy;
import org.wso2.balana.attr.proxy.DateTimeAttributeProxy;
import org.wso2.balana.attr.proxy.DayTimeDurationAttributeProxy;
import org.wso2.balana.attr.proxy.DoubleAttributeProxy;
import org.wso2.balana.attr.proxy.HexBinaryAttributeProxy;
import org.wso2.balana.attr.proxy.IPAddressAttributeProxy;
import org.wso2.balana.attr.proxy.IntegerAttributeProxy;
import org.wso2.balana.attr.proxy.RFC822NameAttributeProxy;
import org.wso2.balana.attr.proxy.StringAttributeProxy;
import org.wso2.balana.attr.proxy.TimeAttributeProxy;
import org.wso2.balana.attr.proxy.X500NameAttributeProxy;
import org.wso2.balana.attr.proxy.YearMonthDurationAttributeProxy;
import org.wso2.balana.attr.proxy.xacml3.XPathAttributeProxy;

public class StandardAttributeFactory
  extends BaseAttributeFactory
{
  private static volatile StandardAttributeFactory factoryInstance = null;
  private static HashMap supportedDatatypes = null;
  private static Set supportedV1Identifiers;
  private static Set supportedV2Identifiers;
  private static Set supportedV3Identifiers;
  private static Log logger = LogFactory.getLog(StandardAttributeFactory.class);
  
  private StandardAttributeFactory()
  {
    super(supportedDatatypes);
  }
  
  private static void initDatatypes()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Initializing standard datatypes");
    }
    supportedDatatypes = new HashMap();
    
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#boolean", new BooleanAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#string", new StringAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#date", new DateAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#time", new TimeAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#dateTime", new DateTimeAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration", 
      new DayTimeDurationAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration", 
      new YearMonthDurationAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#double", new DoubleAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#integer", new IntegerAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#anyURI", new AnyURIAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#hexBinary", new HexBinaryAttributeProxy());
    supportedDatatypes.put("http://www.w3.org/2001/XMLSchema#base64Binary", new Base64BinaryAttributeProxy());
    supportedDatatypes.put("urn:oasis:names:tc:xacml:1.0:data-type:x500Name", new X500NameAttributeProxy());
    supportedDatatypes.put("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name", new RFC822NameAttributeProxy());
    
    supportedV1Identifiers = Collections.unmodifiableSet(supportedDatatypes.keySet());
    
    supportedDatatypes.put("urn:oasis:names:tc:xacml:2.0:data-type:dnsName", new DNSNameAttributeProxy());
    supportedDatatypes.put("urn:oasis:names:tc:xacml:2.0:data-type:ipAddress", new IPAddressAttributeProxy());
    
    supportedV2Identifiers = Collections.unmodifiableSet(supportedDatatypes.keySet());
    
    supportedDatatypes.put("urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression", new XPathAttributeProxy());
    
    supportedV3Identifiers = Collections.unmodifiableSet(supportedDatatypes.keySet());
  }
  
  public static StandardAttributeFactory getFactory()
  {
    if (factoryInstance == null) {
      synchronized (StandardAttributeFactory.class)
      {
        if (factoryInstance == null)
        {
          initDatatypes();
          factoryInstance = new StandardAttributeFactory();
        }
      }
    }
    return factoryInstance;
  }
  
  public static AttributeFactory getNewFactory()
  {
    getFactory();
    
    return new BaseAttributeFactory(supportedDatatypes);
  }
  
  public static Set getStandardDatatypes(String xacmlVersion)
    throws UnknownIdentifierException
  {
    if (xacmlVersion.equals("urn:oasis:names:tc:xacml:1.0:policy")) {
      return supportedV1Identifiers;
    }
    if (xacmlVersion.equals("urn:oasis:names:tc:xacml:2.0:policy:schema:os")) {
      return supportedV2Identifiers;
    }
    if (xacmlVersion.equals("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17")) {
      return supportedV3Identifiers;
    }
    throw new UnknownIdentifierException("Unknown XACML version: " + xacmlVersion);
  }
  
  public void addDatatype(String id, AttributeProxy proxy)
  {
    throw new UnsupportedOperationException("a standard factory cannot support new datatypes");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.StandardAttributeFactory
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
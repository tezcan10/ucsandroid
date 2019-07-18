package org.wso2.balana.ctx;

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.ParsingException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class InputParser
  implements ErrorHandler
{
  private File schemaFile;
  private static InputParser ipReference = null;
  private static final String CONTEXT_SCHEMA_PROPERTY = "com.sun.xacml.ContextSchema";
  private static Log logger = LogFactory.getLog(InputParser.class);
  private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  
  static
  {
    String schemaName = System.getProperty("com.sun.xacml.ContextSchema");
    if (schemaName != null) {
      ipReference = new InputParser(new File(schemaName));
    }
  }
  
  private InputParser(File schemaFile)
  {
    this.schemaFile = schemaFile;
  }
  
  public static Node parseInput(InputStream input, String rootTag)
    throws ParsingException
  {
    NodeList nodes = null;
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setIgnoringComments(true);
      
      DocumentBuilder builder = null;
      
      factory.setNamespaceAware(true);
      if (ipReference == null)
      {
        factory.setValidating(false);
        
        builder = factory.newDocumentBuilder();
      }
      else
      {
        factory.setValidating(true);
        
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", ipReferenceschemaFile);
        
        builder = factory.newDocumentBuilder();
        builder.setErrorHandler(ipReference);
      }
      Document doc = builder.parse(input);
      nodes = doc.getElementsByTagName(rootTag);
    }
    catch (Exception e)
    {
      throw new ParsingException("Error tring to parse " + rootTag + "Type", e);
    }
    if (nodes.getLength() != 1) {
      throw new ParsingException("Only one " + rootTag + "Type allowed " + 
        "at the root of a Context doc");
    }
    return nodes.item(0);
  }
  
  public void warning(SAXParseException exception)
    throws SAXException
  {
    if (logger.isWarnEnabled()) {
      logger.warn("Warning on line " + exception.getLineNumber() + ": " + 
        exception.getMessage());
    }
  }
  
  public void error(SAXParseException exception)
    throws SAXException
  {
    if (logger.isErrorEnabled()) {
      logger.error("Error on line " + exception.getLineNumber() + ": " + 
        exception.getMessage());
    }
    throw new SAXException("invalid context document");
  }
  
  public void fatalError(SAXParseException exception)
    throws SAXException
  {
    if (logger.isErrorEnabled()) {
      logger.error("FatalError on line " + exception.getLineNumber() + ": " + 
        exception.getMessage());
    }
    throw new SAXException("invalid context document");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.InputParser
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
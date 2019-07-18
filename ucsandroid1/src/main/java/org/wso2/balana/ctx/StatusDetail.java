package org.wso2.balana.ctx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.xml.sax.SAXException;

public class StatusDetail
{
  private Node detailRoot;
  private String detailText = null;
  private List<MissingAttributeDetail> missingAttributeDetails;
  
  public StatusDetail(List<MissingAttributeDetail> missingAttributeDetails)
    throws IllegalArgumentException
  {
    this.missingAttributeDetails = missingAttributeDetails;
    try
    {
      detailText = "<StatusDetail>\n";
      for (MissingAttributeDetail attribute : missingAttributeDetails) {
        detailText = (detailText + attribute.getEncoded() + "\n");
      }
      detailText += "</StatusDetail>";
      detailRoot = textToNode(detailText);
    }
    catch (ParsingException pe)
    {
      throw new IllegalArgumentException("Invalid MissingAttributeDetail data, caused by " + 
        pe.getMessage());
    }
  }
  
  public StatusDetail(String encoded)
    throws ParsingException
  {
    detailText = ("<StatusDetail>\n" + encoded + "\n</StatusDetail>");
    detailRoot = textToNode(detailText);
  }
  
  private StatusDetail(Node root)
  {
    detailRoot = root;
    try
    {
      detailText = nodeToText(root);
    }
    catch (ParsingException localParsingException) {}
  }
  
  private Node textToNode(String encoded)
    throws ParsingException
  {
    try
    {
      String text = "<?xml version=\"1.0\"?>\n";
      byte[] bytes = (text + encoded).getBytes();
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = factory.newDocumentBuilder();
      Document doc = db.parse(new ByteArrayInputStream(bytes));
      
      return doc.getDocumentElement();
    }
    catch (ParserConfigurationException e)
    {
      throw new ParsingException("invalid XML for status detail");
    }
    catch (SAXException e)
    {
      throw new ParsingException("invalid XML for status detail");
    }
    catch (IOException e)
    {
      throw new ParsingException("invalid XML for status detail");
    }
  }
  
  private String nodeToText(Node node)
    throws ParsingException
  {
    StringWriter sw = new StringWriter();
    try
    {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty("omit-xml-declaration", "yes");
      transformer.setOutputProperty("indent", "yes");
      transformer.transform(new DOMSource(node), new StreamResult(sw));
    }
    catch (TransformerException te)
    {
      throw new ParsingException("invalid XML for status detail");
    }
    return sw.toString();
  }
  
  public static StatusDetail getInstance(Node root)
    throws ParsingException
  {
    if (!root.getNodeName().equals("StatusDetail")) {
      throw new ParsingException("not a StatusDetail node");
    }
    return new StatusDetail(root);
  }
  
  public Node getDetail()
  {
    return detailRoot;
  }
  
  public List<MissingAttributeDetail> getMissingAttributeDetails()
  {
    return missingAttributeDetails;
  }
  
  public String getEncoded()
    throws IllegalStateException
  {
    if (detailText == null) {
      throw new IllegalStateException("no encoded form available");
    }
    return detailText;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ctx.StatusDetail
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
package org.wso2.balana.attr;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

public class AttributeDesignator
  extends AbstractDesignator
{
  public static final int SUBJECT_TARGET = 0;
  public static final int RESOURCE_TARGET = 1;
  public static final int ACTION_TARGET = 2;
  public static final int ENVIRONMENT_TARGET = 3;
  public static final String SUBJECT_CATEGORY_DEFAULT = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
  private static final String[] targetTypes = new String[]{"Subject", "Resource", "Action", "Environment"};
  private int target;
  private URI type;
  private URI id;
  private String issuer;
  private boolean mustBePresent;
  private URI category;
  private static Log logger = LogFactory.getLog(AttributeDesignator.class);

  public AttributeDesignator(int target, URI type, URI id, boolean mustBePresent) {
    this(target, type, id, mustBePresent, (String)null, (URI)null);
  }

  public AttributeDesignator(int target, URI type, URI id, boolean mustBePresent, String issuer) throws IllegalArgumentException {
    this(target, type, id, mustBePresent, (String)null, (URI)null);
  }

  public AttributeDesignator(int target, URI type, URI id, boolean mustBePresent, String issuer, URI category) throws IllegalArgumentException {
    if (target != 0 && target != 1 && target != 2 && target != 3) {
      throw new IllegalArgumentException("Input target is not a validvalue");
    } else {
      this.target = target;
      this.type = type;
      this.id = id;
      this.mustBePresent = mustBePresent;
      this.issuer = issuer;
      this.category = category;
    }
  }

  public static AttributeDesignator getInstance(Node root) throws ParsingException {
    URI type = null;
    URI id = null;
    String issuer = null;
    boolean mustBePresent = false;
    URI category = null;
    String tagName = root.getNodeName();
    byte target;
    if (tagName.equals("SubjectAttributeDesignator")) {
      target = 0;
    } else if (tagName.equals("ResourceAttributeDesignator")) {
      target = 1;
    } else if (tagName.equals("ActionAttributeDesignator")) {
      target = 2;
    } else {
      if (!tagName.equals("EnvironmentAttributeDesignator")) {
        throw new ParsingException("AttributeDesignator cannot be constructed using type: " + root.getNodeName());
      }

      target = 3;
    }

    NamedNodeMap attrs = root.getAttributes();

    try {
      id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
    } catch (Exception var12) {
      throw new ParsingException("Required AttributeId missing in AttributeDesignator", var12);
    }

    try {
      type = new URI(attrs.getNamedItem("DataType").getNodeValue());
    } catch (Exception var11) {
      throw new ParsingException("Required DataType missing in AttributeDesignator", var11);
    }

    try {
      Node node = attrs.getNamedItem("Issuer");
      if (node != null) {
        issuer = node.getNodeValue();
      }

      if (target == 0) {
        Node scnode = attrs.getNamedItem("SubjectCategory");
        if (scnode != null) {
          category = new URI(scnode.getNodeValue());
        } else {
          category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
        }
      } else if (target == 1) {
        category = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
      } else if (target == 2) {
        category = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:action");
      } else if (target == 3) {
        category = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:environment");
      }

      node = attrs.getNamedItem("MustBePresent");
      if (node != null && node.getNodeValue().equals("true")) {
        mustBePresent = true;
      }
    } catch (Exception var13) {
      throw new ParsingException("Error parsing AttributeDesignator optional attributes", var13);
    }

    return new AttributeDesignator(target, type, id, mustBePresent, issuer, category);
  }

  public int getDesignatorType() {
    return this.target;
  }

  public URI getType() {
    return this.type;
  }

  public URI getId() {
    return this.id;
  }

  public URI getCategory() {
    return this.category;
  }

  public String getIssuer() {
    return this.issuer;
  }

  public boolean mustBePresent() {
    return this.mustBePresent;
  }

  public boolean returnsBag() {
    return true;
  }

  /** @deprecated */
  public boolean evaluatesToBag() {
    return true;
  }

  public List getChildren() {
    return Collections.EMPTY_LIST;
  }

  public EvaluationResult evaluate(EvaluationCtx evaluationCtx) {
    EvaluationResult result = null;
    switch(this.target) {
      case 0:
        result = evaluationCtx.getAttribute(this.type, this.id, this.issuer, this.category);
        break;
      case 1:
        result = evaluationCtx.getAttribute(this.type, this.id, this.issuer, this.category);
        break;
      case 2:
        result = evaluationCtx.getAttribute(this.type, this.id, this.issuer, this.category);
        break;
      case 3:
        result = evaluationCtx.getAttribute(this.type, this.id, this.issuer, this.category);
    }

    if (result != null) {
      if (result.indeterminate()) {
        return result;
      } else {
        BagAttribute bag = (BagAttribute)result.getAttributeValue();
        if (bag.isEmpty() && this.mustBePresent) {
          if (logger.isDebugEnabled()) {
            logger.debug("AttributeDesignator failed to resolve a value for a required attribute: " + this.id.toString());
          }

          ArrayList<String> code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:missing-attribute");
          String message = "Couldn't find " + targetTypes[this.target] + "AttributeDesignator attribute";
          return new EvaluationResult(new Status(code, message));
        } else {
          return result;
        }
      }
    } else {
      ArrayList<String> code = new ArrayList();
      code.add("urn:oasis:names:tc:xacml:1.0:status:missing-attribute");
      String message = "Couldn't find " + targetTypes[this.target] + "AttributeDesignator attribute";
      return new EvaluationResult(new Status(code, message));
    }
  }

  public void encode(OutputStream output) {
    this.encode(output, new Indenter(0));
  }

  public void encode(OutputStream output, Indenter indenter) {
    PrintStream out = new PrintStream(output);
    String indent = indenter.makeString();
    String tag = "<" + targetTypes[this.target] + "AttributeDesignator";
    if (this.target == 0 && this.category != null) {
      tag = tag + " SubjectCategory=\"" + this.category.toString() + "\"";
    }

    tag = tag + " AttributeId=\"" + this.id.toString() + "\"";
    tag = tag + " DataType=\"" + this.type.toString() + "\"";
    if (this.issuer != null) {
      tag = tag + " Issuer=\"" + this.issuer.toString() + "\"";
    }

    if (this.mustBePresent) {
      tag = tag + " MustBePresent=\"true\"";
    }

    tag = tag + "/>";
    out.println(indent + tag);
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.AttributeDesignator
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
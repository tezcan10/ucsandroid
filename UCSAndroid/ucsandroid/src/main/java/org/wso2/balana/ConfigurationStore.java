package org.wso2.balana;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeFactoryProxy;
import org.wso2.balana.attr.AttributeProxy;
import org.wso2.balana.attr.BaseAttributeFactory;
import org.wso2.balana.attr.StandardAttributeFactory;
import org.wso2.balana.combine.BaseCombiningAlgFactory;
import org.wso2.balana.combine.CombiningAlgFactory;
import org.wso2.balana.combine.CombiningAlgFactoryProxy;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.StandardCombiningAlgFactory;
import org.wso2.balana.cond.BaseFunctionFactory;
import org.wso2.balana.cond.BasicFunctionFactoryProxy;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.FunctionFactory;
import org.wso2.balana.cond.FunctionFactoryProxy;
import org.wso2.balana.cond.FunctionProxy;
import org.wso2.balana.cond.StandardFunctionFactory;
import org.wso2.balana.cond.cluster.FunctionCluster;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.ResourceFinder;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ConfigurationStore {
  public static final String PDP_CONFIG_PROPERTY = "org.wso2.balana.PDPConfigFile";
  private PDPConfig defaultPDPConfig;
  private HashMap pdpConfigMap;
  private AttributeFactoryProxy defaultAttributeFactoryProxy;
  private HashMap attributeMap;
  private CombiningAlgFactoryProxy defaultCombiningFactoryProxy;
  private HashMap combiningMap;
  private FunctionFactoryProxy defaultFunctionFactoryProxy;
  private HashMap functionMap;
  private ClassLoader loader;
  private static Log logger = LogFactory.getLog(ConfigurationStore.class);

  public ConfigurationStore() throws ParsingException {
    String configFile = System.getProperty("org.wso2.balana.PDPConfigFile");
    if (configFile == null) {
      logger.error("A property defining a config file was expected, but none was provided");
      throw new ParsingException("Config property org.wso2.balana.PDPConfigFile needs to be set");
    } else {
      try {
        this.setupConfig(new File(configFile));
      } catch (ParsingException var3) {
        logger.error("Runtime config file couldn't be loaded so no configurations will be available", var3);
        throw var3;
      }
    }
  }

  public ConfigurationStore(File configFile) throws ParsingException {
    try {
      this.setupConfig(configFile);
    } catch (ParsingException var3) {
      logger.error("Runtime config file couldn't be loaded so no configurations will be available", var3);
      throw var3;
    }
  }

  private void setupConfig(File configFile) throws ParsingException {
    logger.info("Loading runtime configuration");
    this.loader = this.getClass().getClassLoader();
    Node root = this.getRootNode(configFile);
    this.pdpConfigMap = new HashMap();
    this.attributeMap = new HashMap();
    this.combiningMap = new HashMap();
    this.functionMap = new HashMap();
    NamedNodeMap attrs = root.getAttributes();
    String defaultPDP = attrs.getNamedItem("defaultPDP").getNodeValue();
    String defaultAF = this.getDefaultFactory(attrs, "defaultAttributeFactory");
    String defaultCAF = this.getDefaultFactory(attrs, "defaultCombiningAlgFactory");
    String defaultFF = this.getDefaultFactory(attrs, "defaultFunctionFactory");
    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      String childName = child.getNodeName();
      String elementName = null;
      if (child.getNodeType() == 1) {
        elementName = child.getAttributes().getNamedItem("name").getNodeValue();
      }

      if (childName.equals("pdp")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading PDP: " + elementName);
        }

        if (this.pdpConfigMap.containsKey(elementName)) {
          throw new ParsingException("more that one pdp with name \"" + elementName + "\"");
        }

        this.pdpConfigMap.put(elementName, this.parsePDPConfig(child));
      } else if (childName.equals("attributeFactory")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading AttributeFactory: " + elementName);
        }

        if (this.attributeMap.containsKey(elementName)) {
          throw new ParsingException("more that one attributeFactory with name " + elementName + "\"");
        }

        this.attributeMap.put(elementName, this.parseAttributeFactory(child));
      } else if (childName.equals("combiningAlgFactory")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading CombiningAlgFactory: " + elementName);
        }

        if (this.combiningMap.containsKey(elementName)) {
          throw new ParsingException("more that one combiningAlgFactory with name \"" + elementName + "\"");
        }

        this.combiningMap.put(elementName, this.parseCombiningAlgFactory(child));
      } else if (childName.equals("functionFactory")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading FunctionFactory: " + elementName);
        }

        if (this.functionMap.containsKey(elementName)) {
          throw new ParsingException("more that one functionFactory with name \"" + elementName + "\"");
        }

        this.functionMap.put(elementName, this.parseFunctionFactory(child));
      }
    }

    this.defaultPDPConfig = (PDPConfig)this.pdpConfigMap.get(defaultPDP);
    this.defaultAttributeFactoryProxy = (AttributeFactoryProxy)this.attributeMap.get(defaultAF);
    if (this.defaultAttributeFactoryProxy == null) {
      try {
        this.defaultAttributeFactoryProxy = new ConfigurationStore.AFProxy(AttributeFactory.getInstance(defaultAF));
      } catch (Exception var15) {
        throw new ParsingException("Unknown AttributeFactory", var15);
      }
    }

    this.defaultCombiningFactoryProxy = (CombiningAlgFactoryProxy)this.combiningMap.get(defaultCAF);
    if (this.defaultCombiningFactoryProxy == null) {
      try {
        this.defaultCombiningFactoryProxy = new ConfigurationStore.CAFProxy(CombiningAlgFactory.getInstance(defaultCAF));
      } catch (Exception var14) {
        throw new ParsingException("Unknown CombininAlgFactory", var14);
      }
    }

    this.defaultFunctionFactoryProxy = (FunctionFactoryProxy)this.functionMap.get(defaultFF);
    if (this.defaultFunctionFactoryProxy == null) {
      try {
        this.defaultFunctionFactoryProxy = FunctionFactory.getInstance(defaultFF);
      } catch (Exception var13) {
        throw new ParsingException("Unknown FunctionFactory", var13);
      }
    }

  }

  private String getDefaultFactory(NamedNodeMap attrs, String factoryName) {
    Node node = attrs.getNamedItem(factoryName);
    return node != null ? node.getNodeValue() : "urn:oasis:names:tc:xacml:1.0:policy";
  }

  private Node getRootNode(File configFile) throws ParsingException {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setIgnoringComments(true);
    dbFactory.setNamespaceAware(false);
    dbFactory.setValidating(false);
    DocumentBuilder db = null;

    try {
      db = dbFactory.newDocumentBuilder();
    } catch (ParserConfigurationException var20) {
      throw new ParsingException("couldn't get a document builder", var20);
    }

    Document doc = null;
    FileInputStream stream = null;

    try {
      stream = new FileInputStream(configFile);
      doc = db.parse(stream);
    } catch (IOException var17) {
      throw new ParsingException("failed to load the file ", var17);
    } catch (SAXException var18) {
      throw new ParsingException("error parsing the XML tree", var18);
    } catch (IllegalArgumentException var19) {
      throw new ParsingException("no data to parse", var19);
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException var16) {
          logger.error("Error while closing input stream");
        }
      }

    }

    Element root = doc.getDocumentElement();
    if (!root.getTagName().equals("config")) {
      throw new ParsingException("unknown document type: " + root.getTagName());
    } else {
      return root;
    }
  }

  private PDPConfig parsePDPConfig(Node root) throws ParsingException {
    ArrayList attrModules = new ArrayList();
    HashSet policyModules = new HashSet();
    ArrayList rsrcModules = new ArrayList();
    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("policyFinderModule")) {
        policyModules.add(this.loadClass("module", child));
      } else if (name.equals("attributeFinderModule")) {
        attrModules.add(this.loadClass("module", child));
      } else if (name.equals("resourceFinderModule")) {
        rsrcModules.add(this.loadClass("module", child));
      }
    }

    AttributeFinder attrFinder = new AttributeFinder();
    attrFinder.setModules(attrModules);
    PolicyFinder policyFinder = new PolicyFinder();
    policyFinder.setModules(policyModules);
    ResourceFinder rsrcFinder = new ResourceFinder();
    rsrcFinder.setModules(rsrcModules);
    return new PDPConfig(attrFinder, policyFinder, rsrcFinder);
  }

  private AttributeFactoryProxy parseAttributeFactory(Node root) throws ParsingException {
    AttributeFactory factory = null;
    if (this.useStandard(root, "useStandardDatatypes")) {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting with standard Datatypes");
      }

      factory = StandardAttributeFactory.getNewFactory();
    } else {
      factory = new BaseAttributeFactory();
    }

    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      if (child.getNodeName().equals("datatype")) {
        String identifier = child.getAttributes().getNamedItem("identifier").getNodeValue();
        AttributeProxy proxy = (AttributeProxy)this.loadClass("datatype", child);

        try {
          ((AttributeFactory)factory).addDatatype(identifier, proxy);
        } catch (IllegalArgumentException var9) {
          throw new ParsingException("duplicate datatype: " + identifier, var9);
        }
      }
    }

    return new ConfigurationStore.AFProxy((AttributeFactory)factory);
  }

  private CombiningAlgFactoryProxy parseCombiningAlgFactory(Node root) throws ParsingException {
    CombiningAlgFactory factory = null;
    if (this.useStandard(root, "useStandardAlgorithms")) {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting with standard Combining Algorithms");
      }

      factory = StandardCombiningAlgFactory.getNewFactory();
    } else {
      factory = new BaseCombiningAlgFactory();
    }

    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      if (child.getNodeName().equals("algorithm")) {
        CombiningAlgorithm alg = (CombiningAlgorithm)this.loadClass("algorithm", child);

        try {
          ((CombiningAlgFactory)factory).addAlgorithm(alg);
        } catch (IllegalArgumentException var8) {
          throw new ParsingException("duplicate combining algorithm: " + alg.getIdentifier().toString(), var8);
        }
      }
    }

    return new ConfigurationStore.CAFProxy((CombiningAlgFactory)factory);
  }

  private FunctionFactoryProxy parseFunctionFactory(Node root) throws ParsingException {
    FunctionFactoryProxy proxy = null;
    FunctionFactory generalFactory = null;
    FunctionFactory conditionFactory = null;
    FunctionFactory targetFactory = null;
    if (this.useStandard(root, "useStandardFunctions")) {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting with standard Functions");
      }

      proxy = StandardFunctionFactory.getNewFactoryProxy();
      targetFactory = ((FunctionFactoryProxy)proxy).getTargetFactory();
      conditionFactory = ((FunctionFactoryProxy)proxy).getConditionFactory();
      generalFactory = ((FunctionFactoryProxy)proxy).getGeneralFactory();
    } else {
      generalFactory = new BaseFunctionFactory();
      conditionFactory = new BaseFunctionFactory((FunctionFactory)generalFactory);
      targetFactory = new BaseFunctionFactory((FunctionFactory)conditionFactory);
      proxy = new BasicFunctionFactoryProxy((FunctionFactory)targetFactory, (FunctionFactory)conditionFactory, (FunctionFactory)generalFactory);
    }

    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("target")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading [TARGET] functions");
        }

        this.functionParserHelper(child, (FunctionFactory)targetFactory);
      } else if (name.equals("condition")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading [CONDITION] functions");
        }

        this.functionParserHelper(child, (FunctionFactory)conditionFactory);
      } else if (name.equals("general")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading [GENERAL] functions");
        }

        this.functionParserHelper(child, (FunctionFactory)generalFactory);
      }
    }

    return (FunctionFactoryProxy)proxy;
  }

  private void functionParserHelper(Node root, FunctionFactory factory) throws ParsingException {
    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("function")) {
        Function function = (Function)this.loadClass("function", child);

        try {
          factory.addFunction(function);
        } catch (IllegalArgumentException var10) {
          throw new ParsingException("duplicate function", var10);
        }
      } else {
        FunctionCluster cluster;
        if (name.equals("abstractFunction")) {
          cluster = null;

          URI identifier;
          try {
            identifier = new URI(child.getAttributes().getNamedItem("identifier").getNodeValue());
          } catch (URISyntaxException var13) {
            throw new ParsingException("invalid function identifier", var13);
          }

          FunctionProxy proxy = (FunctionProxy)this.loadClass("abstract function", child);

          try {
            factory.addAbstractFunction(proxy, identifier);
          } catch (IllegalArgumentException var12) {
            throw new ParsingException("duplicate abstract function", var12);
          }
        } else if (name.equals("functionCluster")) {
          cluster = (FunctionCluster)this.loadClass("function cluster", child);
          Iterator it = cluster.getSupportedFunctions().iterator();

          while(it.hasNext()) {
            try {
              factory.addFunction((Function)it.next());
            } catch (IllegalArgumentException var11) {
              throw new ParsingException("duplicate function", var11);
            }
          }
        }
      }
    }

  }

  private Object loadClass(String prefix, Node root) throws ParsingException {
    String className = root.getAttributes().getNamedItem("class").getNodeValue();
    if (logger.isDebugEnabled()) {
      logger.debug("Loading [ " + prefix + ": " + className + " ]");
    }

    Class c = null;

    try {
      c = this.loader.loadClass(className);
    } catch (ClassNotFoundException var20) {
      throw new ParsingException("couldn't load class " + className, var20);
    }

    Object instance = null;
    if (!root.hasChildNodes()) {
      try {
        instance = c.newInstance();
      } catch (InstantiationException var18) {
        throw new ParsingException("couldn't instantiate " + className + " with empty constructor", var18);
      } catch (IllegalAccessException var19) {
        throw new ParsingException("couldn't get access to instance of " + className, var19);
      }
    } else {
      Set args = null;

      try {
        args = this.getArgs(root);
      } catch (IllegalArgumentException var17) {
        throw new ParsingException("illegal class arguments", var17);
      }

      int argLength = args.size();
      Constructor[] cons = c.getConstructors();
      Constructor constructor = null;

      for(int i = 0; i < cons.length; ++i) {
        Class[] params = cons[i].getParameterTypes();
        if (params.length == argLength) {
          Iterator it = args.iterator();

          int j;
          for(j = 0; it.hasNext() && params[j].isAssignableFrom(it.next().getClass()); ++j) {
          }

          if (j == argLength) {
            constructor = cons[i];
          }
        }

        if (constructor != null) {
          break;
        }
      }

      if (constructor == null) {
        throw new ParsingException("couldn't find a matching constructor");
      }

      try {
        instance = constructor.newInstance(args.toArray());
      } catch (InstantiationException var14) {
        throw new ParsingException("couldn't instantiate " + className, var14);
      } catch (IllegalAccessException var15) {
        throw new ParsingException("couldn't get access to instance of " + className, var15);
      } catch (InvocationTargetException var16) {
        throw new ParsingException("couldn't create " + className, var16);
      }
    }

    return instance;
  }

  private Set getArgs(Node root) {
    Set args = new HashSet();
    NodeList children = root.getChildNodes();

    for(int i = 0; i < children.getLength(); ++i) {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (child.getNodeType() == 1) {
        if (name.equals("string")) {
          args.add(child.getFirstChild().getNodeValue());
        } else {
          if (!name.equals("set")) {
            throw new IllegalArgumentException("unkown arg type: " + name);
          }

          args.add(this.getArgs(child));
        }
      }
    }

    return args;
  }

  private boolean useStandard(Node node, String attributeName) {
    NamedNodeMap map = node.getAttributes();
    if (map == null) {
      return true;
    } else {
      Node attrNode = map.getNamedItem(attributeName);
      return attrNode == null ? true : attrNode.getNodeValue().equals("true");
    }
  }

  public PDPConfig getDefaultPDPConfig() throws UnknownIdentifierException {
    if (this.defaultPDPConfig == null) {
      throw new UnknownIdentifierException("no default available");
    } else {
      return this.defaultPDPConfig;
    }
  }

  public PDPConfig getPDPConfig(String name) throws UnknownIdentifierException {
    Object object = this.pdpConfigMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown pdp: " + name);
    } else {
      return (PDPConfig)object;
    }
  }

  public Set getSupportedPDPConfigurations() {
    return Collections.unmodifiableSet(this.pdpConfigMap.keySet());
  }

  public AttributeFactoryProxy getDefaultAttributeFactoryProxy() {
    return this.defaultAttributeFactoryProxy;
  }

  public AttributeFactory getAttributeFactory(String name) throws UnknownIdentifierException {
    Object object = this.attributeMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown factory: " + name);
    } else {
      return (AttributeFactory)object;
    }
  }

  public Set getSupportedAttributeFactories() {
    return Collections.unmodifiableSet(this.attributeMap.keySet());
  }

  public void registerAttributeFactories() {
    Iterator it = this.attributeMap.keySet().iterator();

    while(it.hasNext()) {
      String id = (String)it.next();
      AttributeFactory af = (AttributeFactory)this.attributeMap.get(id);

      try {
        AttributeFactory.registerFactory(id, new ConfigurationStore.AFProxy(af));
      } catch (IllegalArgumentException var5) {
        if (logger.isWarnEnabled()) {
          logger.warn("Couldn't register AttributeFactory:" + id + " (already in use)", var5);
        }
      }
    }

  }

  public CombiningAlgFactoryProxy getDefaultCombiningFactoryProxy() {
    return this.defaultCombiningFactoryProxy;
  }

  public CombiningAlgFactory getCombiningAlgFactory(String name) throws UnknownIdentifierException {
    Object object = this.combiningMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown factory: " + name);
    } else {
      return (CombiningAlgFactory)object;
    }
  }

  public Set getSupportedCombiningAlgFactories() {
    return Collections.unmodifiableSet(this.combiningMap.keySet());
  }

  public void registerCombiningAlgFactories() {
    Iterator it = this.combiningMap.keySet().iterator();

    while(it.hasNext()) {
      String id = (String)it.next();
      CombiningAlgFactory cf = (CombiningAlgFactory)this.combiningMap.get(id);

      try {
        CombiningAlgFactory.registerFactory(id, new ConfigurationStore.CAFProxy(cf));
      } catch (IllegalArgumentException var5) {
        if (logger.isWarnEnabled()) {
          logger.warn("Couldn't register CombiningAlgFactory: " + id + " (already in use)", var5);
        }
      }
    }

  }

  public FunctionFactoryProxy getDefaultFunctionFactoryProxy() {
    return this.defaultFunctionFactoryProxy;
  }

  public FunctionFactoryProxy getFunctionFactoryProxy(String name) throws UnknownIdentifierException {
    Object object = this.functionMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown factory: " + name);
    } else {
      return (FunctionFactoryProxy)object;
    }
  }

  public Set getSupportedFunctionFactories() {
    return Collections.unmodifiableSet(this.functionMap.keySet());
  }

  public void registerFunctionFactories() {
    Iterator it = this.functionMap.keySet().iterator();

    while(it.hasNext()) {
      String id = (String)it.next();
      FunctionFactoryProxy ffp = (FunctionFactoryProxy)this.functionMap.get(id);

      try {
        FunctionFactory.registerFactory(id, ffp);
      } catch (IllegalArgumentException var5) {
        if (logger.isWarnEnabled()) {
          logger.warn("Couldn't register FunctionFactory: " + id + " (already in use)", var5);
        }
      }
    }

  }

  public void useDefaultFactories() {
    if (logger.isDebugEnabled()) {
      logger.debug("Switching to default factories from configuration");
    }

    if (this.defaultAttributeFactoryProxy != null) {
      AttributeFactory.setDefaultFactory(this.defaultAttributeFactoryProxy);
    }

    if (this.defaultCombiningFactoryProxy != null) {
      CombiningAlgFactory.setDefaultFactory(this.defaultCombiningFactoryProxy);
    }

    if (this.defaultFunctionFactoryProxy != null) {
      FunctionFactory.setDefaultFactory(this.defaultFunctionFactoryProxy);
    }

  }

  static class AFProxy implements AttributeFactoryProxy {
    private AttributeFactory factory;

    public AFProxy(AttributeFactory factory) {
      this.factory = factory;
    }

    public AttributeFactory getFactory() {
      return this.factory;
    }
  }

  static class CAFProxy implements CombiningAlgFactoryProxy {
    private CombiningAlgFactory factory;

    public CAFProxy(CombiningAlgFactory factory) {
      this.factory = factory;
    }

    public CombiningAlgFactory getFactory() {
      return this.factory;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ConfigurationStore
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
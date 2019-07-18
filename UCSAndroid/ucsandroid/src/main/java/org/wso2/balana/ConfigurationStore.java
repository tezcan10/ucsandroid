package org.wso2.balana;

import java.io.File;
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

public class ConfigurationStore
{
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
  
  public ConfigurationStore()
    throws ParsingException
  {
    String configFile = System.getProperty("org.wso2.balana.PDPConfigFile");
    if (configFile == null)
    {
      logger.error("A property defining a config file was expected, but none was provided");
      
      throw new ParsingException("Config property org.wso2.balana.PDPConfigFile needs to be set");
    }
    try
    {
      setupConfig(new File(configFile));
    }
    catch (ParsingException pe)
    {
      logger.error("Runtime config file couldn't be loaded so no configurations will be available", 
        pe);
      throw pe;
    }
  }
  
  public ConfigurationStore(File configFile)
    throws ParsingException
  {
    try
    {
      setupConfig(configFile);
    }
    catch (ParsingException pe)
    {
      logger.error("Runtime config file couldn't be loaded so no configurations will be available", 
        pe);
      throw pe;
    }
  }
  
  private void setupConfig(File configFile)
    throws ParsingException
  {
    logger.info("Loading runtime configuration");
    
    loader = getClass().getClassLoader();
    
    Node root = getRootNode(configFile);
    
    pdpConfigMap = new HashMap();
    attributeMap = new HashMap();
    combiningMap = new HashMap();
    functionMap = new HashMap();
    
    NamedNodeMap attrs = root.getAttributes();
    String defaultPDP = attrs.getNamedItem("defaultPDP").getNodeValue();
    String defaultAF = getDefaultFactory(attrs, "defaultAttributeFactory");
    String defaultCAF = getDefaultFactory(attrs, "defaultCombiningAlgFactory");
    String defaultFF = getDefaultFactory(attrs, "defaultFunctionFactory");
    
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String childName = child.getNodeName();
      String elementName = null;
      if (child.getNodeType() == 1) {
        elementName = child.getAttributes().getNamedItem("name").getNodeValue();
      }
      if (childName.equals("pdp"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading PDP: " + elementName);
        }
        if (pdpConfigMap.containsKey(elementName)) {
          throw new ParsingException("more that one pdp with name \"" + elementName + 
            "\"");
        }
        pdpConfigMap.put(elementName, parsePDPConfig(child));
      }
      else if (childName.equals("attributeFactory"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading AttributeFactory: " + elementName);
        }
        if (attributeMap.containsKey(elementName)) {
          throw new ParsingException("more that one attributeFactory with name " + 
            elementName + "\"");
        }
        attributeMap.put(elementName, parseAttributeFactory(child));
      }
      else if (childName.equals("combiningAlgFactory"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading CombiningAlgFactory: " + elementName);
        }
        if (combiningMap.containsKey(elementName)) {
          throw new ParsingException("more that one combiningAlgFactory with name \"" + 
            elementName + "\"");
        }
        combiningMap.put(elementName, parseCombiningAlgFactory(child));
      }
      else if (childName.equals("functionFactory"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading FunctionFactory: " + elementName);
        }
        if (functionMap.containsKey(elementName)) {
          throw new ParsingException("more that one functionFactory with name \"" + 
            elementName + "\"");
        }
        functionMap.put(elementName, parseFunctionFactory(child));
      }
    }
    defaultPDPConfig = ((PDPConfig)pdpConfigMap.get(defaultPDP));
    
    defaultAttributeFactoryProxy = ((AttributeFactoryProxy)attributeMap.get(defaultAF));
    if (defaultAttributeFactoryProxy == null) {
      try
      {
        defaultAttributeFactoryProxy = new AFProxy(AttributeFactory.getInstance(defaultAF));
      }
      catch (Exception e)
      {
        throw new ParsingException("Unknown AttributeFactory", e);
      }
    }
    defaultCombiningFactoryProxy = ((CombiningAlgFactoryProxy)combiningMap.get(defaultCAF));
    if (defaultCombiningFactoryProxy == null) {
      try
      {
        defaultCombiningFactoryProxy = new CAFProxy(CombiningAlgFactory.getInstance(defaultCAF));
      }
      catch (Exception e)
      {
        throw new ParsingException("Unknown CombininAlgFactory", e);
      }
    }
    defaultFunctionFactoryProxy = ((FunctionFactoryProxy)functionMap.get(defaultFF));
    if (defaultFunctionFactoryProxy == null) {
      try
      {
        defaultFunctionFactoryProxy = FunctionFactory.getInstance(defaultFF);
      }
      catch (Exception e)
      {
        throw new ParsingException("Unknown FunctionFactory", e);
      }
    }
  }
  
  private String getDefaultFactory(NamedNodeMap attrs, String factoryName)
  {
    Node node = attrs.getNamedItem(factoryName);
    if (node != null) {
      return node.getNodeValue();
    }
    return "urn:oasis:names:tc:xacml:1.0:policy";
  }
  
  /* Error */
  private Node getRootNode(File configFile)
    throws ParsingException
  {
    // Byte code:
    //   0: invokestatic 315	javax/xml/parsers/DocumentBuilderFactory:newInstance	()Ljavax/xml/parsers/DocumentBuilderFactory;
    //   3: astore_2
    //   4: aload_2
    //   5: iconst_1
    //   6: invokevirtual 321	javax/xml/parsers/DocumentBuilderFactory:setIgnoringComments	(Z)V
    //   9: aload_2
    //   10: iconst_0
    //   11: invokevirtual 325	javax/xml/parsers/DocumentBuilderFactory:setNamespaceAware	(Z)V
    //   14: aload_2
    //   15: iconst_0
    //   16: invokevirtual 328	javax/xml/parsers/DocumentBuilderFactory:setValidating	(Z)V
    //   19: aconst_null
    //   20: astore_3
    //   21: aload_2
    //   22: invokevirtual 331	javax/xml/parsers/DocumentBuilderFactory:newDocumentBuilder	()Ljavax/xml/parsers/DocumentBuilder;
    //   25: astore_3
    //   26: goto +18 -> 44
    //   29: astore 4
    //   31: new 42	org/wso2/balana/ParsingException
    //   34: dup
    //   35: ldc_w 335
    //   38: aload 4
    //   40: invokespecial 263	org/wso2/balana/ParsingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   43: athrow
    //   44: aconst_null
    //   45: astore 4
    //   47: aconst_null
    //   48: astore 5
    //   50: new 337	java/io/FileInputStream
    //   53: dup
    //   54: aload_1
    //   55: invokespecial 339	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   58: astore 5
    //   60: aload_3
    //   61: aload 5
    //   63: invokevirtual 341	javax/xml/parsers/DocumentBuilder:parse	(Ljava/io/InputStream;)Lorg/w3c/dom/Document;
    //   66: astore 4
    //   68: goto +79 -> 147
    //   71: astore 6
    //   73: new 42	org/wso2/balana/ParsingException
    //   76: dup
    //   77: ldc_w 347
    //   80: aload 6
    //   82: invokespecial 263	org/wso2/balana/ParsingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   85: athrow
    //   86: astore 6
    //   88: new 42	org/wso2/balana/ParsingException
    //   91: dup
    //   92: ldc_w 349
    //   95: aload 6
    //   97: invokespecial 263	org/wso2/balana/ParsingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   100: athrow
    //   101: astore 6
    //   103: new 42	org/wso2/balana/ParsingException
    //   106: dup
    //   107: ldc_w 351
    //   110: aload 6
    //   112: invokespecial 263	org/wso2/balana/ParsingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   115: athrow
    //   116: astore 7
    //   118: aload 5
    //   120: ifnull +24 -> 144
    //   123: aload 5
    //   125: invokevirtual 353	java/io/InputStream:close	()V
    //   128: goto +16 -> 144
    //   131: astore 8
    //   133: getstatic 36	org/wso2/balana/ConfigurationStore:logger	Lorg/apache/commons/logging/Log;
    //   136: ldc_w 358
    //   139: invokeinterface 54 2 0
    //   144: aload 7
    //   146: athrow
    //   147: aload 5
    //   149: ifnull +24 -> 173
    //   152: aload 5
    //   154: invokevirtual 353	java/io/InputStream:close	()V
    //   157: goto +16 -> 173
    //   160: astore 8
    //   162: getstatic 36	org/wso2/balana/ConfigurationStore:logger	Lorg/apache/commons/logging/Log;
    //   165: ldc_w 358
    //   168: invokeinterface 54 2 0
    //   173: aload 4
    //   175: invokeinterface 360 1 0
    //   180: astore 6
    //   182: aload 6
    //   184: invokeinterface 366 1 0
    //   189: ldc_w 371
    //   192: invokevirtual 164	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   195: ifne +34 -> 229
    //   198: new 42	org/wso2/balana/ParsingException
    //   201: dup
    //   202: new 174	java/lang/StringBuilder
    //   205: dup
    //   206: ldc_w 373
    //   209: invokespecial 178	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   212: aload 6
    //   214: invokeinterface 366 1 0
    //   219: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   222: invokevirtual 183	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: invokespecial 62	org/wso2/balana/ParsingException:<init>	(Ljava/lang/String;)V
    //   228: athrow
    //   229: aload 6
    //   231: areturn
    // Line number table:
    //   Java source line #309	-> byte code offset #0
    //   Java source line #311	-> byte code offset #4
    //   Java source line #312	-> byte code offset #9
    //   Java source line #313	-> byte code offset #14
    //   Java source line #315	-> byte code offset #19
    //   Java source line #317	-> byte code offset #21
    //   Java source line #318	-> byte code offset #26
    //   Java source line #319	-> byte code offset #31
    //   Java source line #322	-> byte code offset #44
    //   Java source line #323	-> byte code offset #47
    //   Java source line #325	-> byte code offset #50
    //   Java source line #326	-> byte code offset #60
    //   Java source line #327	-> byte code offset #68
    //   Java source line #328	-> byte code offset #73
    //   Java source line #329	-> byte code offset #86
    //   Java source line #330	-> byte code offset #88
    //   Java source line #331	-> byte code offset #101
    //   Java source line #332	-> byte code offset #103
    //   Java source line #333	-> byte code offset #116
    //   Java source line #334	-> byte code offset #118
    //   Java source line #336	-> byte code offset #123
    //   Java source line #337	-> byte code offset #128
    //   Java source line #338	-> byte code offset #133
    //   Java source line #341	-> byte code offset #144
    //   Java source line #334	-> byte code offset #147
    //   Java source line #336	-> byte code offset #152
    //   Java source line #337	-> byte code offset #157
    //   Java source line #338	-> byte code offset #162
    //   Java source line #343	-> byte code offset #173
    //   Java source line #345	-> byte code offset #182
    //   Java source line #346	-> byte code offset #198
    //   Java source line #348	-> byte code offset #229
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	ConfigurationStore
    //   0	232	1	configFile	File
    //   3	19	2	dbFactory	javax.xml.parsers.DocumentBuilderFactory
    //   20	41	3	db	javax.xml.parsers.DocumentBuilder
    //   29	10	4	pce	javax.xml.parsers.ParserConfigurationException
    //   45	129	4	doc	org.w3c.dom.Document
    //   48	105	5	stream	java.io.InputStream
    //   71	10	6	ioe	java.io.IOException
    //   86	10	6	saxe	org.xml.sax.SAXException
    //   101	10	6	iae	IllegalArgumentException
    //   180	50	6	root	org.w3c.dom.Element
    //   116	29	7	localObject	Object
    //   131	3	8	e	java.io.IOException
    //   160	3	8	e	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   21	26	29	javax/xml/parsers/ParserConfigurationException
    //   50	68	71	java/io/IOException
    //   50	68	86	org/xml/sax/SAXException
    //   50	68	101	java/lang/IllegalArgumentException
    //   50	116	116	finally
    //   123	128	131	java/io/IOException
    //   152	157	160	java/io/IOException
  }
  
  private PDPConfig parsePDPConfig(Node root)
    throws ParsingException
  {
    ArrayList attrModules = new ArrayList();
    HashSet policyModules = new HashSet();
    ArrayList rsrcModules = new ArrayList();
    
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("policyFinderModule")) {
        policyModules.add(loadClass("module", child));
      } else if (name.equals("attributeFinderModule")) {
        attrModules.add(loadClass("module", child));
      } else if (name.equals("resourceFinderModule")) {
        rsrcModules.add(loadClass("module", child));
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
  
  private AttributeFactoryProxy parseAttributeFactory(Node root)
    throws ParsingException
  {
    AttributeFactory factory = null;
    if (useStandard(root, "useStandardDatatypes"))
    {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting with standard Datatypes");
      }
      factory = StandardAttributeFactory.getNewFactory();
    }
    else
    {
      factory = new BaseAttributeFactory();
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeName().equals("datatype"))
      {
        String identifier = child.getAttributes().getNamedItem("identifier").getNodeValue();
        AttributeProxy proxy = (AttributeProxy)loadClass("datatype", child);
        try
        {
          factory.addDatatype(identifier, proxy);
        }
        catch (IllegalArgumentException iae)
        {
          throw new ParsingException("duplicate datatype: " + identifier, iae);
        }
      }
    }
    return new AFProxy(factory);
  }
  
  private CombiningAlgFactoryProxy parseCombiningAlgFactory(Node root)
    throws ParsingException
  {
    CombiningAlgFactory factory = null;
    if (useStandard(root, "useStandardAlgorithms"))
    {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting with standard Combining Algorithms");
      }
      factory = StandardCombiningAlgFactory.getNewFactory();
    }
    else
    {
      factory = new BaseCombiningAlgFactory();
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeName().equals("algorithm"))
      {
        CombiningAlgorithm alg = (CombiningAlgorithm)loadClass("algorithm", child);
        try
        {
          factory.addAlgorithm(alg);
        }
        catch (IllegalArgumentException iae)
        {
          throw new ParsingException("duplicate combining algorithm: " + 
            alg.getIdentifier().toString(), iae);
        }
      }
    }
    return new CAFProxy(factory);
  }
  
  private FunctionFactoryProxy parseFunctionFactory(Node root)
    throws ParsingException
  {
    FunctionFactoryProxy proxy = null;
    FunctionFactory generalFactory = null;
    FunctionFactory conditionFactory = null;
    FunctionFactory targetFactory = null;
    if (useStandard(root, "useStandardFunctions"))
    {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting with standard Functions");
      }
      proxy = StandardFunctionFactory.getNewFactoryProxy();
      
      targetFactory = proxy.getTargetFactory();
      conditionFactory = proxy.getConditionFactory();
      generalFactory = proxy.getGeneralFactory();
    }
    else
    {
      generalFactory = new BaseFunctionFactory();
      conditionFactory = new BaseFunctionFactory(generalFactory);
      targetFactory = new BaseFunctionFactory(conditionFactory);
      
      proxy = new BasicFunctionFactoryProxy(targetFactory, conditionFactory, generalFactory);
    }
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("target"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading [TARGET] functions");
        }
        functionParserHelper(child, targetFactory);
      }
      else if (name.equals("condition"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading [CONDITION] functions");
        }
        functionParserHelper(child, conditionFactory);
      }
      else if (name.equals("general"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("Loading [GENERAL] functions");
        }
        functionParserHelper(child, generalFactory);
      }
    }
    return proxy;
  }
  
  private void functionParserHelper(Node root, FunctionFactory factory)
    throws ParsingException
  {
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals("function"))
      {
        Function function = (Function)loadClass("function", child);
        try
        {
          factory.addFunction(function);
        }
        catch (IllegalArgumentException iae)
        {
          throw new ParsingException("duplicate function", iae);
        }
      }
      else if (name.equals("abstractFunction"))
      {
        URI identifier = null;
        try
        {
          identifier = new URI(child.getAttributes().getNamedItem("identifier")
            .getNodeValue());
        }
        catch (URISyntaxException urise)
        {
          throw new ParsingException("invalid function identifier", urise);
        }
        FunctionProxy proxy = (FunctionProxy)loadClass("abstract function", child);
        try
        {
          factory.addAbstractFunction(proxy, identifier);
        }
        catch (IllegalArgumentException iae)
        {
          throw new ParsingException("duplicate abstract function", iae);
        }
      }
      else if (name.equals("functionCluster"))
      {
        FunctionCluster cluster = (FunctionCluster)loadClass("function cluster", child);
        
        Iterator it = cluster.getSupportedFunctions().iterator();
        while (it.hasNext()) {
          try
          {
            factory.addFunction((Function)it.next());
          }
          catch (IllegalArgumentException iae)
          {
            throw new ParsingException("duplicate function", iae);
          }
        }
      }
    }
  }
  
  private Object loadClass(String prefix, Node root)
    throws ParsingException
  {
    String className = root.getAttributes().getNamedItem("class").getNodeValue();
    if (logger.isDebugEnabled()) {
      logger.debug("Loading [ " + prefix + ": " + className + " ]");
    }
    Class c = null;
    try
    {
      c = loader.loadClass(className);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new ParsingException("couldn't load class " + className, cnfe);
    }
    Object instance = null;
    if (!root.hasChildNodes())
    {
      try
      {
        instance = c.newInstance();
      }
      catch (InstantiationException ie)
      {
        throw new ParsingException("couldn't instantiate " + className + 
          " with empty constructor", ie);
      }
      catch (IllegalAccessException iae)
      {
        throw new ParsingException("couldn't get access to instance of " + className, 
          iae);
      }
    }
    else
    {
      Set args = null;
      try
      {
        args = getArgs(root);
      }
      catch (IllegalArgumentException iae)
      {
        throw new ParsingException("illegal class arguments", iae);
      }
      int argLength = args.size();
      
      Constructor[] cons = c.getConstructors();
      Constructor constructor = null;
      for (int i = 0; i < cons.length; i++)
      {
        Class[] params = cons[i].getParameterTypes();
        if (params.length == argLength)
        {
          Iterator it = args.iterator();
          int j = 0;
          while (it.hasNext())
          {
            if (!params[j].isAssignableFrom(it.next().getClass())) {
              break;
            }
            j++;
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
      try
      {
        instance = constructor.newInstance(args.toArray());
      }
      catch (InstantiationException ie)
      {
        throw new ParsingException("couldn't instantiate " + className, ie);
      }
      catch (IllegalAccessException iae)
      {
        throw new ParsingException("couldn't get access to instance of " + className, 
          iae);
      }
      catch (InvocationTargetException ite)
      {
        throw new ParsingException("couldn't create " + className, ite);
      }
    }
    return instance;
  }
  
  private Set getArgs(Node root)
  {
    Set args = new HashSet();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (child.getNodeType() == 1) {
        if (name.equals("string")) {
          args.add(child.getFirstChild().getNodeValue());
        } else if (name.equals("set")) {
          args.add(getArgs(child));
        } else {
          throw new IllegalArgumentException("unkown arg type: " + name);
        }
      }
    }
    return args;
  }
  
  private boolean useStandard(Node node, String attributeName)
  {
    NamedNodeMap map = node.getAttributes();
    if (map == null) {
      return true;
    }
    Node attrNode = map.getNamedItem(attributeName);
    if (attrNode == null) {
      return true;
    }
    return attrNode.getNodeValue().equals("true");
  }
  
  public PDPConfig getDefaultPDPConfig()
    throws UnknownIdentifierException
  {
    if (defaultPDPConfig == null) {
      throw new UnknownIdentifierException("no default available");
    }
    return defaultPDPConfig;
  }
  
  public PDPConfig getPDPConfig(String name)
    throws UnknownIdentifierException
  {
    Object object = pdpConfigMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown pdp: " + name);
    }
    return (PDPConfig)object;
  }
  
  public Set getSupportedPDPConfigurations()
  {
    return Collections.unmodifiableSet(pdpConfigMap.keySet());
  }
  
  public AttributeFactoryProxy getDefaultAttributeFactoryProxy()
  {
    return defaultAttributeFactoryProxy;
  }
  
  public AttributeFactory getAttributeFactory(String name)
    throws UnknownIdentifierException
  {
    Object object = attributeMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown factory: " + name);
    }
    return (AttributeFactory)object;
  }
  
  public Set getSupportedAttributeFactories()
  {
    return Collections.unmodifiableSet(attributeMap.keySet());
  }
  
  public void registerAttributeFactories()
  {
    Iterator it = attributeMap.keySet().iterator();
    while (it.hasNext())
    {
      String id = (String)it.next();
      AttributeFactory af = (AttributeFactory)attributeMap.get(id);
      try
      {
        AttributeFactory.registerFactory(id, new AFProxy(af));
      }
      catch (IllegalArgumentException iae)
      {
        if (logger.isWarnEnabled()) {
          logger.warn("Couldn't register AttributeFactory:" + id + " (already in use)", 
            iae);
        }
      }
    }
  }
  
  public CombiningAlgFactoryProxy getDefaultCombiningFactoryProxy()
  {
    return defaultCombiningFactoryProxy;
  }
  
  public CombiningAlgFactory getCombiningAlgFactory(String name)
    throws UnknownIdentifierException
  {
    Object object = combiningMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown factory: " + name);
    }
    return (CombiningAlgFactory)object;
  }
  
  public Set getSupportedCombiningAlgFactories()
  {
    return Collections.unmodifiableSet(combiningMap.keySet());
  }
  
  public void registerCombiningAlgFactories()
  {
    Iterator it = combiningMap.keySet().iterator();
    while (it.hasNext())
    {
      String id = (String)it.next();
      CombiningAlgFactory cf = (CombiningAlgFactory)combiningMap.get(id);
      try
      {
        CombiningAlgFactory.registerFactory(id, new CAFProxy(cf));
      }
      catch (IllegalArgumentException iae)
      {
        if (logger.isWarnEnabled()) {
          logger.warn("Couldn't register CombiningAlgFactory: " + id + 
            " (already in use)", iae);
        }
      }
    }
  }
  
  public FunctionFactoryProxy getDefaultFunctionFactoryProxy()
  {
    return defaultFunctionFactoryProxy;
  }
  
  public FunctionFactoryProxy getFunctionFactoryProxy(String name)
    throws UnknownIdentifierException
  {
    Object object = functionMap.get(name);
    if (object == null) {
      throw new UnknownIdentifierException("unknown factory: " + name);
    }
    return (FunctionFactoryProxy)object;
  }
  
  public Set getSupportedFunctionFactories()
  {
    return Collections.unmodifiableSet(functionMap.keySet());
  }
  
  public void registerFunctionFactories()
  {
    Iterator it = functionMap.keySet().iterator();
    while (it.hasNext())
    {
      String id = (String)it.next();
      FunctionFactoryProxy ffp = (FunctionFactoryProxy)functionMap.get(id);
      try
      {
        FunctionFactory.registerFactory(id, ffp);
      }
      catch (IllegalArgumentException iae)
      {
        if (logger.isWarnEnabled()) {
          logger.warn("Couldn't register FunctionFactory: " + id + " (already in use)", 
            iae);
        }
      }
    }
  }
  
  public void useDefaultFactories()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("Switching to default factories from configuration");
    }
    if (defaultAttributeFactoryProxy != null) {
      AttributeFactory.setDefaultFactory(defaultAttributeFactoryProxy);
    }
    if (defaultCombiningFactoryProxy != null) {
      CombiningAlgFactory.setDefaultFactory(defaultCombiningFactoryProxy);
    }
    if (defaultFunctionFactoryProxy != null) {
      FunctionFactory.setDefaultFactory(defaultFunctionFactoryProxy);
    }
  }
  
  static class AFProxy
    implements AttributeFactoryProxy
  {
    private AttributeFactory factory;
    
    public AFProxy(AttributeFactory factory)
    {
      this.factory = factory;
    }
    
    public AttributeFactory getFactory()
    {
      return factory;
    }
  }
  
  static class CAFProxy
    implements CombiningAlgFactoryProxy
  {
    private CombiningAlgFactory factory;
    
    public CAFProxy(CombiningAlgFactory factory)
    {
      this.factory = factory;
    }
    
    public CombiningAlgFactory getFactory()
    {
      return factory;
    }
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.ConfigurationStore
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
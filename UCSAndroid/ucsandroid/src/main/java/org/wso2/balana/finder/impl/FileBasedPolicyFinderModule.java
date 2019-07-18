package org.wso2.balana.finder.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.AbstractTarget;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.PolicySet;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FileBasedPolicyFinderModule
  extends PolicyFinderModule {
        private PolicyFinder finder = null;
        private Map<URI, AbstractPolicy> policies = new HashMap();
        private Set<String> policyLocations;
        private PolicyCombiningAlgorithm combiningAlg;
        private static Log log = LogFactory.getLog(FileBasedPolicyFinderModule.class);
        public static final String POLICY_DIR_PROPERTY = "org.wso2.balana.PolicyDirectory";

        public FileBasedPolicyFinderModule() {
          if (System.getProperty("org.wso2.balana.PolicyDirectory") != null) {
            this.policyLocations = new HashSet();
            this.policyLocations.add(System.getProperty("org.wso2.balana.PolicyDirectory"));
          }

        }

        public FileBasedPolicyFinderModule(Set<String> policyLocations) {
          this.policyLocations = policyLocations;
        }

        public void init(PolicyFinder finder) {
          this.finder = finder;
          this.loadPolicies();
          this.combiningAlg = new DenyOverridesPolicyAlg();
        }

        public PolicyFinderResult findPolicy(EvaluationCtx context) {
          ArrayList<AbstractPolicy> selectedPolicies = new ArrayList();
          Set<Entry<URI, AbstractPolicy>> entrySet = this.policies.entrySet();
          Iterator var5 = entrySet.iterator();

          while(var5.hasNext()) {
            Entry<URI, AbstractPolicy> entry = (Entry)var5.next();
            AbstractPolicy policy = (AbstractPolicy)entry.getValue();
            MatchResult match = policy.match(context);
            int result = match.getResult();
            if (result == 2) {
              return new PolicyFinderResult(match.getStatus());
            }

            if (result == 0) {
              if (this.combiningAlg == null && selectedPolicies.size() > 0) {
                ArrayList<String> code = new ArrayList();
                code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
                Status status = new Status(code, "too many applicable top-level policies");
                return new PolicyFinderResult(status);
              }

              selectedPolicies.add(policy);
            }
          }

          switch(selectedPolicies.size()) {
            case 0:
              if (log.isDebugEnabled()) {
                log.debug("No matching XACML policy found");
              }

              return new PolicyFinderResult();
            case 1:
              return new PolicyFinderResult((AbstractPolicy)selectedPolicies.get(0));
            default:
              return new PolicyFinderResult(new PolicySet((URI)null, this.combiningAlg, (AbstractTarget)null, selectedPolicies));
          }
        }

        public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData) {
          AbstractPolicy policy = (AbstractPolicy)this.policies.get(idReference);
          if (policy != null) {
            if (type == 0) {
              if (policy instanceof Policy) {
                return new PolicyFinderResult(policy);
              }
            } else if (policy instanceof PolicySet) {
              return new PolicyFinderResult(policy);
            }
          }

          ArrayList<String> code = new ArrayList();
          code.add("urn:oasis:names:tc:xacml:1.0:status:processing-error");
          Status status = new Status(code, "couldn't load referenced policy");
          return new PolicyFinderResult(status);
        }

        public boolean isIdReferenceSupported() {
          return true;
        }

        public boolean isRequestSupported() {
          return true;
        }

        public void loadPolicies() {
          this.policies.clear();
          Iterator var2 = this.policyLocations.iterator();

          while(true) {
            while(true) {
              String policyLocation;
              File file;
              do {
                if (!var2.hasNext()) {
                  return;
                }

                policyLocation = (String)var2.next();
                file = new File(policyLocation);
              } while(!file.exists());

              if (file.isDirectory()) {
                String[] files = file.list();
                String[] var8 = files;
                int var7 = files.length;

                for(int var6 = 0; var6 < var7; ++var6) {
                  String policyFile = var8[var6];
                  this.loadPolicy(policyLocation + File.separator + policyFile, this.finder);
                }
              } else {
                this.loadPolicy(policyLocation, this.finder);
              }
            }
          }
        }

        private AbstractPolicy loadPolicy(String policyFile, PolicyFinder finder) {
          AbstractPolicy policy = null;
          FileInputStream stream = null;

          try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            DocumentBuilder db = factory.newDocumentBuilder();
            stream = new FileInputStream(policyFile);
            Document doc = db.parse(stream);
            Element root = doc.getDocumentElement();
            String name = root.getLocalName();
            if (name.equals("Policy")) {
              policy = Policy.getInstance(root);
            } else if (name.equals("PolicySet")) {
              policy = PolicySet.getInstance(root, finder);
            }
          } catch (Exception var18) {
            log.error("Fail to load policy : " + policyFile, var18);
          } finally {
            if (stream != null) {
              try {
                stream.close();
              } catch (IOException var17) {
                log.error("Error while closing input stream");
              }
            }

          }

          if (policy != null) {
            this.policies.put(((AbstractPolicy)policy).getId(), policy);
          }

          return (AbstractPolicy)policy;
        }
}

/* Location:
 * Qualified Name:     org.wso2.balana.finder.impl.FileBasedPolicyFinderModule
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
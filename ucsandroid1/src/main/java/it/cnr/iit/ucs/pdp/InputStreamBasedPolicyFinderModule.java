/*
 * THIS IS POLICY FINDER MODULE implemented by CNR for supporting DATA USAGE
 * CONTROL CHANGED by CNR-IIT : THIS SHOULD BE MOVED TO PDP (CORE)
 */

package it.cnr.iit.ucs.pdp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicySet;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

/**
 * This is file based policy repository. Policies can be inside the directory in
 * a file system. Then you can set directory location using
 * "org.wso2.balana.PolicyDirectory" JAVA property
 *
 * @author Fabio Bindi Filippo Lauria
 *
 */
class InputStreamBasedPolicyFinderModule extends PolicyFinderModule {

    private static final Logger log = Logger.getLogger( InputStreamBasedPolicyFinderModule.class.getName() );

    private PolicyFinder finder = null;

    private Map<URI, AbstractPolicy> policies = new HashMap<>();

    // the policy is stored here
    private String policy = "";

    private PolicyCombiningAlgorithm combiningAlg;

    private DocumentBuilderFactory documentBuilderFactory;

    public InputStreamBasedPolicyFinderModule( String policy ) {
        try {
            this.policy = policy;

            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
            documentBuilderFactory.setIgnoringComments( true );
            documentBuilderFactory.setNamespaceAware( true );
            documentBuilderFactory.setValidating( false );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
            throw new IllegalStateException( "Unable to protect against XXE" );
        }
    }

    @Override
    public void init( PolicyFinder finder ) {
        this.finder = finder;

        loadPolicies();
        combiningAlg = new DenyOverridesPolicyAlg();
    }

    @Override
    public PolicyFinderResult findPolicy( EvaluationCtx context ) {
        ArrayList<AbstractPolicy> selectedPolicies = new ArrayList<>();
        Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for( Map.Entry<URI, AbstractPolicy> entry : entrySet ) {
            AbstractPolicy abstractPolicy = entry.getValue();
            MatchResult match = abstractPolicy.match( context );
            int result = match.getResult();

            // if target matching was indeterminate, then return the error
            if( result == MatchResult.INDETERMINATE ) {
                return new PolicyFinderResult( match.getStatus() );
            }
            // see if the target matched
            if( result == MatchResult.MATCH ) {
                if( ( combiningAlg == null ) && ( selectedPolicies.isEmpty() ) ) {
                    // we found a match before, so this is an error
                    ArrayList<String> code = new ArrayList<>();
                    code.add( Status.STATUS_PROCESSING_ERROR );
                    Status status = new Status( code, "too many applicable " + "top-level policies" );
                    return new PolicyFinderResult( status );
                }
                // this is the first match we've found, so remember it
                selectedPolicies.add( abstractPolicy );
            }
        }

        // no errors happened during the search, so now take the right
        // action based on how many policies we found
        switch( selectedPolicies.size() ) {
            case 0:
                return new PolicyFinderResult();
            case 1:
                return new PolicyFinderResult( ( selectedPolicies.get( 0 ) ) );
            default:
                return new PolicyFinderResult( new PolicySet( null, combiningAlg, null, selectedPolicies ) );
        }
    }

    @Override
    public boolean isIdReferenceSupported() {
        return true;
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    public void loadPolicies() {
        policies.clear();
        loadPolicy( finder );
    }

    /**
     * Private helper that tries to load the given file-based policy, and returns
     * null if any error occurs.
     */
    private AbstractPolicy loadPolicy( PolicyFinder finder ) {
        AbstractPolicy abstractPolicy = null;

        try (InputStream stream = new ByteArrayInputStream( policy.getBytes() )) {
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            Document doc = db.parse( stream );
            Element root = doc.getDocumentElement();
            String name = root.getLocalName();

            if( name.equals( "Policy" ) ) {
                abstractPolicy = Policy.getInstance( root );
            } else if( name.equals( "PolicySet" ) ) {
                abstractPolicy = PolicySet.getInstance( root, finder );
            }
        } catch( Exception e ) {
            log.warning( "fail to load UXACML policy : " + e.getLocalizedMessage() );
        }

        if( abstractPolicy != null ) {
            policies.put( abstractPolicy.getId(), abstractPolicy );
        }

        return abstractPolicy;
    }
}

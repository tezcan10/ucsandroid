package it.cnr.iit.xacml.wrappers;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.contexthandler.pipregistry.PIPRegistryInterface;
import it.cnr.iit.ucs.exceptions.RequestException;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.xacml.Attribute;

import oasis.names.tc.xacml.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class RequestWrapper {

    private static final Logger log = Logger.getLogger( RequestWrapper.class.getName() );

    private PIPRegistryInterface pipRegistry;
    private RequestType requestType;
    private String request;

    private RequestWrapper() {}

    public static RequestWrapper build( String request ) throws RequestException {
        return build( request, null );
    }

    public static RequestWrapper build( String request, PIPRegistryInterface pipRegistry ) throws RequestException {
        RequestWrapper requestWrapper = new RequestWrapper();
        try {
            requestWrapper.requestType = unmarshalRequestType( request );
        } catch( Exception e ) {
            throw new RequestException( "Error marshalling request : {0}" + e.getMessage() );
        }

        requestWrapper.request = request;
        requestWrapper.pipRegistry = pipRegistry;
        return requestWrapper;
    }

    public static RequestWrapper build( RequestWrapper request ) throws RequestException {
        return RequestWrapper.build( request.getRequest(), request.pipRegistry );
    }

    public String getRequest() {
        return request;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public boolean requestHasAttribute( Attribute attribute ) {
        for( AttributesType attributeType : requestType.getAttributes() ) {
            for( AttributeType att : attributeType.getAttribute() ) {
                if( attribute.getAttributeId().equals( att.getAttributeId() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean update() {
        try {
            request = marshalRequestType( requestType );
            return true;
        } catch( Exception e ) {
            log.log( Level.SEVERE, "Error marshalling request (update) : {0}", e.getMessage() );
            return false;
        }
    }

    public synchronized boolean fatten( boolean subscribe ) {
        if( pipRegistry == null ) {
            log.log( Level.INFO, "pipRegistry is not set in this requestWrapper" );
            return false;
        }
        if( subscribe ) {
            pipRegistry.subscribeAll( requestType );
        } else {
            pipRegistry.retrieveAll( requestType );
        }
        return update();
    }

    public static RequestType unmarshalRequestType( String request ) throws Exception {
        return JAXBUtility.unmarshalToObject( RequestType.class, request );
    }

    public static String marshalRequestType( RequestType request ) throws Exception {
        return JAXBUtility.marshalToString( RequestType.class, request, "Request", JAXBUtility.SCHEMA );
    }

}

/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package oasis.names.tc.xacml.core.schema.wd_17;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import it.cnr.iit.xacml.Category;


@Root( name = "RequestType",
        strict = false )
public final class RequestType {

    private static final Logger log = Logger.getLogger( RequestType.class.getName() );

    @Element( name = "RequestDefaults" )
    protected RequestDefaultsType requestDefaults;
    @Element( name = "Attributes", required = true )
    protected List<AttributesType> attributes;
    @Element( name = "MultiRequests" )
    protected MultiRequestsType multiRequests;
    @Attribute( name = "ReturnPolicyIdList", required = true )
    protected boolean returnPolicyIdList;
    @Attribute( name = "CombinedDecision", required = true )
    protected boolean combinedDecision;

    public RequestDefaultsType getRequestDefaults() {
        return requestDefaults;
    }

    public void setRequestDefaults( RequestDefaultsType value ) {
        this.requestDefaults = value;
    }

    public List<AttributesType> getAttributes() {
        if( attributes == null ) {
            attributes = new ArrayList<>();
        }
        return this.attributes;
    }

    public MultiRequestsType getMultiRequests() {
        return multiRequests;
    }

    public void setMultiRequests( MultiRequestsType value ) {
        this.multiRequests = value;
    }

    public boolean isReturnPolicyIdList() {
        return returnPolicyIdList;
    }

    public void setReturnPolicyIdList( boolean value ) {
        this.returnPolicyIdList = value;
    }

    public boolean isCombinedDecision() {
        return combinedDecision;
    }

    public void setCombinedDecision( boolean value ) {
        this.combinedDecision = value;
    }

    /**
     * Retrieves the first attribute value stored inside the request attributes.
     * By assumption in our case in the original request we have just the
     * attributes related to the name of the subject who performs the action, the
     * name of the action he wants to perform and the name of the resource on
     * which he wants to perform the action. Any other schema is not allowed.
     *
     * @param requestAttributes
     *          the attributes of the request
     * @param subject
     *          the category in which we're interested into
     * @return the value of the attribute, null otherwise
     */
    public String getAttributeValue( Category subject ) {
        try {
            for( AttributesType attributeType : attributes ) {
                if( attributeType.getCategory().equals( subject.toString() ) ) {
                    return attributeType.getAttribute().get( 0 ).getAttributeValue().get( 0 )
                        .getContent().get( 0 ).toString();
                }
            }
        } catch( Exception e ) {
            log.severe( "error getting attribute value : " + e.getMessage() );
        }

        return null;
    }

    public String getAttribute( String category, String attributeId ) {
        String res = null;

        AttributesType attbs = attributes.stream()
            .filter( a -> a.getCategory().endsWith( category ) ).findFirst()
            .orElse( null );

        if( attbs != null ) {
            AttributeType attr = attbs.getAttribute().stream()
                .filter( a -> a.getAttributeId().endsWith( attributeId ) ).findFirst()
                .orElse( null );

            if( attr != null ) {
                res = attr.getAttributeValue().get( 0 ).getContent().get( 0 ).toString();
            }
        }

        return res;
    }

    public boolean addAttribute( it.cnr.iit.xacml.Attribute attribute, String value ) {
        return addAttribute( attribute.getCategory().toString(),
            attribute.getDataType().toString(),
            attribute.getAttributeId(), value );
    }

    public boolean addAttribute( String category, String dataType,
            String attributeId, String value ) {

        AttributeValueType attributeValueType = new AttributeValueType();
        attributeValueType.setDataType( dataType );
        attributeValueType.getContent().add( value );
        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeId( attributeId );
        attributeType.setIncludeInResult( false );
        attributeType.setIssuer( "false" );
        attributeType.getAttributeValue().add( attributeValueType );
        AttributesType attributesType = new AttributesType();
        attributesType.setCategory( category );
        attributesType.getAttribute().add( attributeType );
        boolean added = false;
        for( AttributesType at : this.getAttributes() ) {
            if( at.getCategory().equals( category ) ) {
                at.getAttribute().add( attributeType );
                added = true;
            }
        }
        if( !added ) {
            this.getAttributes().add( attributesType );
        }

        return true;
    }

    public boolean addAttribute( String category, String dataType,
            String attributeId, List<String> value ) {
        AttributeValueType attributeValueType = new AttributeValueType();
        attributeValueType.setDataType( dataType );
        attributeValueType.getContent().addAll( value );
        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeId( attributeId );
        attributeType.setIncludeInResult( false );
        attributeType.setIssuer( "false" );
        attributeType.getAttributeValue().add( attributeValueType );
        AttributesType attributesType = new AttributesType();
        attributesType.setCategory( category );
        attributesType.getAttribute().add( attributeType );
        boolean added = false;
        for( AttributesType at : this.getAttributes() ) {
            if( at.getCategory().equals( category ) ) {
                at.getAttribute().add( attributeType );
                added = true;
            }
        }
        if( !added ) {
            this.getAttributes().add( attributesType );
        }

        return true;
    }

}

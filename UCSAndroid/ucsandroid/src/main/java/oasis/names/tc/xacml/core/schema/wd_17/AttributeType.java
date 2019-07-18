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

@Root( name = "AttributeType", strict = false)
public class AttributeType {

    @Element( name = "AttributeValue", required = true )
    protected List<AttributeValueType> attributeValue;
    @Attribute( name = "AttributeId", required = true )
    protected String attributeId;
    @Attribute( name = "Issuer" )
    protected String issuer;
    @Attribute( name = "IncludeInResult", required = true )
    protected boolean includeInResult;

    public List<AttributeValueType> getAttributeValue() {
        if( attributeValue == null ) {
            attributeValue = new ArrayList<>();
        }
        return this.attributeValue;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId( String value ) {
        this.attributeId = value;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer( String value ) {
        this.issuer = value;
    }

    public boolean isIncludeInResult() {
        return includeInResult;
    }

    public void setIncludeInResult( boolean value ) {
        this.includeInResult = value;
    }

}

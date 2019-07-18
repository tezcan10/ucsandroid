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

@Root( name = "AttributesType", strict = false )
public class AttributesType {

    @Element( name = "Content" )
    protected ContentType content;
    @Element( name = "Attribute" )
    protected List<AttributeType> attribute;
    @Attribute( name = "Category", required = true )
    protected String category;
    @Attribute( name = "id" )
    protected String id;

    public ContentType getContent() {
        return content;
    }

    public void setContent( ContentType value ) {
        this.content = value;
    }

    public List<AttributeType> getAttribute() {
        if( attribute == null ) {
            attribute = new ArrayList<>();
        }
        return this.attribute;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory( String value ) {
        this.category = value;
    }

    public String getId() {
        return id;
    }

    public void setId( String value ) {
        this.id = value;
    }

}

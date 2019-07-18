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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

@Root( name = "AttributeValueType", strict = false)
public class AttributeValueType {

    @Element
    protected List<Object> content;
    @Attribute( name = "DataType", required = true )
    protected String dataType;
    @Attribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public List<Object> getContent() {
        if( content == null ) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType( String value ) {
        this.dataType = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}

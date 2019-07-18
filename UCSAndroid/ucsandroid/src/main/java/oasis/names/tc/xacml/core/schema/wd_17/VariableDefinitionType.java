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

@Root( name = "VariableDefinitionType", strict = false )
public class VariableDefinitionType {

    @Element( name = "Expression")
    protected Object expression;
    @Attribute( name = "VariableId", required = true )
    protected String variableId;

    public Object getExpression() { // NOSONAR
        return expression;
    }

    public void setExpression( Object value ) {
        this.expression = value;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId( String value ) {
        this.variableId = value;
    }

}

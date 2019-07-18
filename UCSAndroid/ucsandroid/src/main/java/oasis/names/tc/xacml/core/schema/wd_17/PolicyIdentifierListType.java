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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root( name = "PolicyIdentifierListType", strict = false )
public class PolicyIdentifierListType {

    @ElementList(name = "PolicyIdReference")
    protected List<SimpleElement<?>> policyIdReferenceOrPolicySetIdReference;

    public List<SimpleElement<?>> getPolicyIdReferenceOrPolicySetIdReference() {
        if( policyIdReferenceOrPolicySetIdReference == null ) {
            policyIdReferenceOrPolicySetIdReference = new ArrayList<>();
        }
        return this.policyIdReferenceOrPolicySetIdReference;
    }

}
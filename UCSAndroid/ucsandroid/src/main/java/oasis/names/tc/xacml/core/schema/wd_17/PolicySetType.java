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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Root( name = "PolicySetType", strict = false )
public class PolicySetType {

    @Element( name = "Description" )
    protected String description;
    @Element( name = "PolicyIssuer" )
    protected PolicyIssuerType policyIssuer;
    @Element( name = "PolicySetDefaults" )
    protected DefaultsType policySetDefaults;
    @Element( name = "Target", required = true )
    protected TargetType target;
    @ElementList(name = "CombinerParameters", required = false)
    protected List<SimpleElement<?>> combinerParameters;
    @ElementList(name = "PolicySet", required = false)
    protected List<SimpleElement<?>> policySet;
    @ElementList(name = "Policy", required = false)
    protected List<SimpleElement<?>> policy;
    @ElementList(name = "PolicyIdReference", required = false)
    protected List<SimpleElement<?>> policyIdReference;
    @Element( name = "ObligationExpressions" )
    protected ObligationExpressionsType obligationExpressions;
    @Element( name = "AdviceExpressions" )
    protected AdviceExpressionsType adviceExpressions;
    @Attribute( name = "PolicySetId", required = true )
    protected String policySetId;
    @Attribute( name = "Version", required = true )
    protected String version;
    @Attribute( name = "PolicyCombiningAlgId", required = true )
    protected String policyCombiningAlgId;
    @Attribute( name = "MaxDelegationDepth" )
    protected BigInteger maxDelegationDepth;

    public String getDescription() {
        return description;
    }

    public void setDescription( String value ) {
        this.description = value;
    }

    public PolicyIssuerType getPolicyIssuer() {
        return policyIssuer;
    }

    public void setPolicyIssuer( PolicyIssuerType value ) {
        this.policyIssuer = value;
    }

    public DefaultsType getPolicySetDefaults() {
        return policySetDefaults;
    }

    public void setPolicySetDefaults( DefaultsType value ) {
        this.policySetDefaults = value;
    }

    public TargetType getTarget() {
        return target;
    }

    public void setTarget( TargetType value ) {
        this.target = value;
    }

    public List<SimpleElement<?>> getPolicySetOrPolicyOrPolicySetIdReference() { // NOSONAR
        ArrayList<SimpleElement<?>> list = new ArrayList<>();
        list.addAll(policySet);
        list.addAll(policy);
        list.addAll(combinerParameters);
        list.addAll(policyIdReference);
        return list;
    }

    public ObligationExpressionsType getObligationExpressions() {
        return obligationExpressions;
    }

    public void setObligationExpressions( ObligationExpressionsType value ) {
        this.obligationExpressions = value;
    }

    public AdviceExpressionsType getAdviceExpressions() {
        return adviceExpressions;
    }

    public void setAdviceExpressions( AdviceExpressionsType value ) {
        this.adviceExpressions = value;
    }

    public String getPolicySetId() {
        return policySetId;
    }

    public void setPolicySetId( String value ) {
        this.policySetId = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String value ) {
        this.version = value;
    }

    public String getPolicyCombiningAlgId() {
        return policyCombiningAlgId;
    }

    public void setPolicyCombiningAlgId( String value ) {
        this.policyCombiningAlgId = value;
    }

    public BigInteger getMaxDelegationDepth() {
        return maxDelegationDepth;
    }

    public void setMaxDelegationDepth( BigInteger value ) {
        this.maxDelegationDepth = value;
    }

}

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

@Root( name = "RuleType", strict = false )
public class RuleType {

    @Element( name = "Description" )
    protected String description;
    @Element( name = "Target" )
    protected TargetType target;
    @Element( name = "Condition" )
    protected List<ConditionType> condition;
    @Element( name = "ObligationExpressions" )
    protected ObligationExpressionsType obligationExpressions;
    @Element( name = "AdviceExpressions" )
    protected AdviceExpressionsType adviceExpressions;
    @Attribute( name = "RuleId", required = true )
    protected String ruleId;
    @Attribute( name = "Effect", required = true )
    protected EffectType effect;

    public String getDescription() {
        return description;
    }

    public void setDescription( String value ) {
        this.description = value;
    }

    public TargetType getTarget() {
        return target;
    }

    public void setTarget( TargetType value ) {
        this.target = value;
    }

    public List<ConditionType> getCondition() {
        if( condition == null ) {
            condition = new ArrayList<>();
        }
        return this.condition;
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

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId( String value ) {
        this.ruleId = value;
    }

    public EffectType getEffect() {
        return effect;
    }

    public void setEffect( EffectType value ) {
        this.effect = value;
    }

}

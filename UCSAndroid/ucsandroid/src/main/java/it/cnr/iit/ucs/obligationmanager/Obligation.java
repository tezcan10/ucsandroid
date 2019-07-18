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
package it.cnr.iit.ucs.obligationmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Obligation {

    private List<Action> actionList = null;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public List<Action> getActionList() {
        return actionList;
    }

    public void setActionList( List<Action> actionList ) {
        this.actionList = actionList;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty( String name, Object value ) {
        this.additionalProperties.put( name, value );
    }

}

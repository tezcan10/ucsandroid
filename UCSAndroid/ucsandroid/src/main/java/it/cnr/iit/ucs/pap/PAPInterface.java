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
package it.cnr.iit.ucs.pap;

import java.util.List;

/**
 * This is the interface to be used to communicate with the Policy
 * Administration Point.
 *
 * The PAP is a storage of Policy, hence all the methods regarding the PAP are
 * methods to add, retrieve or list the policy it stores.
 *
 * @author Antonio La Marra
 */
public interface PAPInterface {
    /**
     * Retrieves the policy identified by that policy id
     *
     * @param policyId
     *          the id of the policy we're interested into
     * @return a String that represents the policy
     */
    public String retrievePolicy( String policyId );

    /**
     * Add a new policy to the table
     *
     * @param policy
     *          the policy to be added
     * @return true if everything goes fine, false otherwise
     */
    public boolean addPolicy( String policy );

    /**
     * List all the policies stored in the table
     *
     * @return the list of policies in String format
     */
    public List<String> listPolicies();
}

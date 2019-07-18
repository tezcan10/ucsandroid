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
package it.cnr.iit.ucs.message.endaccess;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.cnr.iit.ucs.constants.ENTITIES;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.EvaluatedMessage;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.pdp.PDPEvaluation;

/**
 * This is the response provided by the UCS once it receives an endAccess
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class EndAccessResponseMessage extends Message implements EvaluatedMessage {

    @JsonProperty
    private PDPEvaluation evaluation;

    public EndAccessResponseMessage() {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString() );
        purpose = PURPOSE.END_RESPONSE;
    }

    public EndAccessResponseMessage( String source, String destination, String messageId ) {
        super( source, destination, messageId );
        purpose = PURPOSE.END_RESPONSE;
    }

    public EndAccessResponseMessage( String messageId ) {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString(), messageId );
        purpose = PURPOSE.END_RESPONSE;
    }

    @Override
    public PDPEvaluation getEvaluation() {
        return evaluation;
    }

    @Override
    public void setEvaluation( PDPEvaluation evaluation ) {
        this.evaluation = evaluation;
    }

}

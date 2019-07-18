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
package it.cnr.iit.ucs.contexthandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import it.cnr.iit.ucs.contexthandler.pipregistry.PIPRegistry;
import it.cnr.iit.ucs.contexthandler.pipregistry.PIPRegistryInterface;
import it.cnr.iit.ucs.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.pdp.PDPInterface;
import it.cnr.iit.ucs.pip.PIPCHInterface;
import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucs.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the abstract representation of the context handler object.
 * In order to work properly, a context handler requires the interfaces offered
 * by other components.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public abstract class AbstractContextHandler implements ContextHandlerInterface {

    private SessionManagerInterface sessionManager;
    private RequestManagerToCHInterface requestManager;
    private ObligationManagerInterface obligationManager;
    private PIPRegistryInterface pipRegistry;
    private PDPInterface pdp;
    private PAPInterface pap;

    protected ContextHandlerProperties properties;
    protected URI uri;

    protected AbstractContextHandler( ContextHandlerProperties properties )  {
        Reject.ifNull( properties, ContextHandlerProperties.class.getName() );
        this.properties = properties;

        try {
            this.uri = new URI(properties.getUri()); // NOSONAR
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        pipRegistry = new PIPRegistry();
    }

    protected final SessionManagerInterface getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager( SessionManagerInterface sessionManager ) {
        Reject.ifNull( sessionManager, SessionManagerInterface.class.getName() );
        this.sessionManager = sessionManager;
    }

    protected final PDPInterface getPdp() {
        return pdp;
    }

    public void setPdp( PDPInterface pdp ) {
        Reject.ifNull( pdp, PDPInterface.class.getName() );
        this.pdp = pdp;
    }

    protected final PAPInterface getPap() {
        return pap;
    }

    public void setPap( PAPInterface pap ) {
        Reject.ifNull( pap, PAPInterface.class.getName() );
        this.pap = pap;
    }

    protected final RequestManagerToCHInterface getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(
            RequestManagerToCHInterface requestManager ) {
        Reject.ifNull( requestManager, RequestManagerToCHInterface.class.getName() );
        this.requestManager = requestManager;
    }

    public void setPIPs( List<PIPCHInterface> pipList ) {
        Reject.ifNull( pipList, PIPCHInterface.class.getName() + " list" );
        for( PIPCHInterface pip : pipList ) {
            pipRegistry.add( pip );
        }
    }

    protected void setPipRegistry( PIPRegistryInterface pipRegistry ) {
        Reject.ifNull( pipRegistry, PIPRegistryInterface.class.getName() );
        this.pipRegistry = pipRegistry;
    }

    public PIPRegistryInterface getPipRegistry() {
        return pipRegistry;
    }

    public void setObligationManager( ObligationManagerInterface obligationManager ) {
        Reject.ifNull( obligationManager, ObligationManagerInterface.class.getName() );
        this.obligationManager = obligationManager;
    }

    protected final ObligationManagerInterface getObligationManager() {
        return obligationManager;
    }

}

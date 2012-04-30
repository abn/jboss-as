/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.test.clustering.extended.ejb2.stateful.remote.failover;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.RemoteHome;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import org.jboss.ejb3.annotation.Clustered;
import org.jboss.logging.Logger;

/**
 * @author Ondrej Chaloupka
 */
@Stateful
@Clustered
@RemoteHome(CounterRemoteHome.class)
public class CounterBean extends CounterBaseBean implements SessionBean {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CounterBean.class);
        
    public void ejbRemove() throws EJBException, RemoteException {
        log.info("ejbRemove called...");
        CounterSingleton.destroyCounter.incrementAndGet();        
    }

    @Override
    public void ejbActivate() throws EJBException, RemoteException {
        
    }

    @Override
    public void ejbPassivate() throws EJBException, RemoteException {
        
    }

    @Override
    public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
        
    }
}

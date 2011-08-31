/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.ejb3.timerservice;

import org.jboss.as.ejb3.component.messagedriven.MessageDrivenComponent;
import org.jboss.as.ejb3.component.messagedriven.MessageDrivenComponentInstance;
import org.jboss.as.ejb3.component.stateless.StatelessSessionComponent;
import org.jboss.as.ejb3.component.stateless.StatelessSessionComponentInstance;
import org.jboss.as.ejb3.pool.Pool;
import org.jboss.as.ejb3.timerservice.spi.MultiTimeoutMethodTimedObjectInvoker;

import javax.ejb.Timer;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Timed object invoker for an EJB
 *
 * @author Stuart Douglas
 */
public class MessageDrivenTimedObjectInvokerImpl implements MultiTimeoutMethodTimedObjectInvoker, Serializable {

    private final MessageDrivenComponent ejbComponent;
    private final Pool<MessageDrivenComponentInstance> pool;

    public MessageDrivenTimedObjectInvokerImpl(final MessageDrivenComponent ejbComponent) {
        this.ejbComponent = ejbComponent;
        this.pool = ejbComponent.getPool();
    }

    @Override
    public void callTimeout(final Timer timer, final Method timeoutMethod) throws Exception {
        final MessageDrivenComponentInstance instance = acquireInstance();
        try {
            instance.invokeTimeoutMethod(timeoutMethod, timer);
        } finally {
            releaseInstance(instance);
        }
    }

    private MessageDrivenComponentInstance acquireInstance() {
        final MessageDrivenComponentInstance instance;
        if (pool != null) {
            instance = pool.get();
        } else {
            instance = (MessageDrivenComponentInstance) ejbComponent.createInstance();
        }
        return instance;
    }

    @Override
    public String getTimedObjectId() {
        return ejbComponent.getComponentName();
    }

    @Override
    public void callTimeout(final Timer timer) throws Exception {
        final MessageDrivenComponentInstance instance = acquireInstance();
        try {
            instance.invokeTimeoutMethod(timer);
        } finally {
            releaseInstance(instance);
        }
    }

    private void releaseInstance(final MessageDrivenComponentInstance instance) {
        if (pool != null) {
            pool.release(instance);
        } else {
            instance.destroy();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return ejbComponent.getComponentClass().getClassLoader();
    }
}

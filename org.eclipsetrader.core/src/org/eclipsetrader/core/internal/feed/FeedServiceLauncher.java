/*
 * Copyright (c) 2004-2008 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package org.eclipsetrader.core.internal.feed;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipsetrader.core.ILauncher;
import org.eclipsetrader.core.feed.IFeedConnector;
import org.eclipsetrader.core.internal.CoreActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class FeedServiceLauncher implements ILauncher, IExecutableExtension {
	private String id;
	private String name;

	public FeedServiceLauncher() {
	}

	/* (non-Javadoc)
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
     */
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
    	id = config.getAttribute("id");
    	name = config.getAttribute("name");
    }

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.ILauncher#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.ILauncher#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.ILauncher#launch(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(IProgressMonitor monitor) {
    	try {
    		BundleContext context = CoreActivator.getDefault().getBundle().getBundleContext();
    		ServiceReference serviceReference = context.getServiceReference(FeedService.class.getName());
    		if (serviceReference != null) {
        		FeedService service = (FeedService) context.getService(serviceReference);

            	IFeedConnector[] connector = service.getConnectors();
    			for (int i = 0; i < connector.length; i++) {
                	try {
                		connector[i].connect();
            		} catch(Throwable e) {
        				Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error starting connector", e); //$NON-NLS-1$
        				CoreActivator.log(status);
            		}
    			}

        		context.ungetService(serviceReference);
    		}
    	} catch(Exception e) {
    		Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error reading feed service", e);
    		CoreActivator.getDefault().getLog().log(status);
    	}
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.ILauncher#terminate(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void terminate(IProgressMonitor monitor) {
    	try {
    		BundleContext context = CoreActivator.getDefault().getBundle().getBundleContext();
    		ServiceReference serviceReference = context.getServiceReference(FeedService.class.getName());
    		if (serviceReference != null) {
        		FeedService service = (FeedService) context.getService(serviceReference);

            	IFeedConnector[] connector = service.getConnectors();
    			for (int i = 0; i < connector.length; i++) {
                	try {
                		connector[i].disconnect();
            		} catch(Throwable e) {
        				Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error starting connector", e); //$NON-NLS-1$
        				CoreActivator.log(status);
            		}
    			}

        		context.ungetService(serviceReference);
    		}
    	} catch(Exception e) {
    		Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error reading feed service", e);
    		CoreActivator.getDefault().getLog().log(status);
    	}
	}
}
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

package org.eclipsetrader.ui.internal.navigator;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipsetrader.core.repositories.IRepositoryChangeListener;
import org.eclipsetrader.core.repositories.IRepositoryService;
import org.eclipsetrader.core.repositories.RepositoryChangeEvent;
import org.eclipsetrader.ui.internal.UIActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("restriction")
public class WatchListsContentProvider implements IStructuredContentProvider {
	private Viewer viewer;

	private IRepositoryChangeListener resourceListener = new IRepositoryChangeListener() {
		public void repositoryResourceChanged(RepositoryChangeEvent event) {
    		NavigatorView view = (NavigatorView) viewer.getInput();
    		view.update();

			if (!viewer.getControl().isDisposed()) {
				try {
					viewer.getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!viewer.getControl().isDisposed())
								viewer.refresh();
						}
					});
				} catch (SWTException e) {
					if (e.code != SWT.ERROR_DEVICE_DISPOSED)
						throw e;
				}
			}
		}
	};

	public WatchListsContentProvider() {
	}

	/* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
		return getRepositoryService().getWatchLists();
    }

	/* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
		getRepositoryService().removeRepositoryResourceListener(resourceListener);
    }

	/* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	if (oldInput != null)
			getRepositoryService().removeRepositoryResourceListener(resourceListener);

    	this.viewer = viewer;

    	if (newInput != null)
			getRepositoryService().addRepositoryResourceListener(resourceListener);
    }

	protected IRepositoryService getRepositoryService() {
		try {
			BundleContext context = UIActivator.getDefault().getBundle().getBundleContext();
			ServiceReference serviceReference = context.getServiceReference(IRepositoryService.class.getName());
			IRepositoryService service = (IRepositoryService) context.getService(serviceReference);
			context.ungetService(serviceReference);
			return service;
		} catch (Exception e) {
			Status status = new Status(Status.ERROR, UIActivator.PLUGIN_ID, 0, "Error reading repository service", e); //$NON-NLS-1$
			UIActivator.getDefault().getLog().log(status);
		}
		return null;
	}
}

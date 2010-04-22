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

package org.eclipsetrader.ui.internal.providers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipsetrader.core.feed.ITrade;
import org.eclipsetrader.core.views.IDataProvider;
import org.eclipsetrader.core.views.IDataProviderFactory;

public class LastTradeDateFactory extends AbstractProviderFactory {
	protected DateFormat formatter = DateFormat.getDateInstance(SimpleDateFormat.MEDIUM);

	public class DataProvider implements IDataProvider {

		public DataProvider() {
		}

		/* (non-Javadoc)
		 * @see org.eclipsetrader.core.views.IDataProvider#init(org.eclipse.core.runtime.IAdaptable)
		 */
		public void init(IAdaptable adaptable) {
		}

		/* (non-Javadoc)
		 * @see org.eclipsetrader.core.views.IDataProvider#getFactory()
		 */
		public IDataProviderFactory getFactory() {
			return LastTradeDateFactory.this;
		}

		/* (non-Javadoc)
		 * @see org.eclipsetrader.core.views.IDataProvider#getValue(org.eclipse.core.runtime.IAdaptable)
		 */
		public IAdaptable getValue(IAdaptable adaptable) {
			ITrade trade = (ITrade) adaptable.getAdapter(ITrade.class);
			if (trade != null && trade.getTime() != null) {
				final Date value = trade.getTime();
				return new IAdaptable() {
					@SuppressWarnings("unchecked")
					public Object getAdapter(Class adapter) {
						if (adapter.isAssignableFrom(String.class))
							return formatter.format(value);
						if (adapter.isAssignableFrom(Date.class))
							return value;
						return null;
					}

					@Override
					public boolean equals(Object obj) {
						if (!(obj instanceof IAdaptable))
							return false;
						Date s = (Date) ((IAdaptable) obj).getAdapter(Date.class);
						return s == value || (value != null && value.equals(s));
					}
				};
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipsetrader.core.views.IDataProvider#dispose()
		 */
		public void dispose() {
		}
	}

	public LastTradeDateFactory() {
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.views.IDataProviderFactory#createProvider()
	 */
	public IDataProvider createProvider() {
		return new DataProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.views.IDataProviderFactory#getType()
	 */
	@SuppressWarnings("unchecked")
	public Class[] getType() {
		return new Class[] {
		    Date.class, String.class,
		};
	}
}
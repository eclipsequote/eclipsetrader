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

package org.eclipsetrader.core.views;

import java.util.Date;

/**
 * Interface implemented by watchlist items.
 *
 * @since 1.0
 */
public interface IWatchListElement extends IHolding {
	public static final String DATE = "date";
	public static final String POSITION = "position";
	public static final String PURCHASE_PRICE = "purchasePrice";

	public void setPosition(Long position);

	public void setPurchasePrice(Double purchasePrice);

	public void setDate(Date date);
}
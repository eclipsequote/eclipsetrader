/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.trading.internal.orders;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.eclipsetrader.core.db.Order;
import net.sourceforge.eclipsetrader.core.db.OrderStatus;
import net.sourceforge.eclipsetrader.trading.IOrdersLabelProvider;

import org.eclipse.swt.SWT;

public class StatusColumn implements IOrdersLabelProvider
{
    static Map labels = new HashMap();
    static {
        labels.put(OrderStatus.NEW, "New");
        labels.put(OrderStatus.PARTIAL, "Partial");
        labels.put(OrderStatus.FILLED, "Filled");
        labels.put(OrderStatus.CANCELED, "Canceled");
        labels.put(OrderStatus.REJECTED, "Rejected");
        labels.put(OrderStatus.PENDING_CANCEL, "Pending Cancel");
        labels.put(OrderStatus.PENDING_NEW, "Pending New");
    }

    public StatusColumn()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.IOrdersLabelProvider#getHeaderText()
     */
    public String getHeaderText()
    {
        return "Status";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.IOrdersLabelProvider#getStyle()
     */
    public int getStyle()
    {
        return SWT.LEFT;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.IOrdersLabelProvider#getText(net.sourceforge.eclipsetrader.core.db.Order)
     */
    public String getText(Order order)
    {
        String text = (String)labels.get(order.getStatus());
        return text != null ? text : "";
    }
}
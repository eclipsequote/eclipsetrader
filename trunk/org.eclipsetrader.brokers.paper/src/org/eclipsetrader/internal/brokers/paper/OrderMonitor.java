/*
 * Copyright (c) 2004-2009 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package org.eclipsetrader.internal.brokers.paper;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipsetrader.core.trading.BrokerException;
import org.eclipsetrader.core.trading.IBroker;
import org.eclipsetrader.core.trading.IOrder;
import org.eclipsetrader.core.trading.IOrderMonitor;
import org.eclipsetrader.core.trading.IOrderMonitorListener;
import org.eclipsetrader.core.trading.IOrderStatus;
import org.eclipsetrader.core.trading.ITransaction;
import org.eclipsetrader.core.trading.OrderMonitorEvent;
import org.eclipsetrader.internal.brokers.paper.transactions.OrderElement;
import org.eclipsetrader.internal.brokers.paper.types.DoubleValueAdapter;
import org.eclipsetrader.internal.brokers.paper.types.OrderStatusAdapter;

@XmlRootElement(name = "monitor")
public class OrderMonitor implements IOrderMonitor, IAdaptable {
	@XmlAttribute(name = "id")
	private String id;

	@XmlElement(name = "order")
	private OrderElement order;

	@XmlAttribute(name = "filled-qty")
	private Long filledQuantity;

	@XmlAttribute(name = "avg-price")
	@XmlJavaTypeAdapter(DoubleValueAdapter.class)
	private Double averagePrice;

	@XmlAttribute(name = "status")
	@XmlJavaTypeAdapter(OrderStatusAdapter.class)
	private IOrderStatus status = IOrderStatus.New;

	@XmlAttribute(name = "message")
	private String message;

	private PaperBroker broker;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

	private List<ITransaction> transactions = new ArrayList<ITransaction>();

	protected OrderMonitor() {
		try {
			broker = (PaperBroker) new PaperBrokerFactory().create();
		} catch (CoreException e) {
			Status status = new Status(Status.WARNING, Activator.PLUGIN_ID, 0, "Error initializing monitor", e); //$NON-NLS-1$
			Activator.log(status);
		}
	}

	public OrderMonitor(PaperBroker broker, IOrder order) {
		this.broker = broker;
		this.order = new OrderElement(order);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getBrokerConnector()
	 */
	@XmlTransient
	public IBroker getBrokerConnector() {
		return broker;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getId()
	 */
	@XmlTransient
	public String getId() {
		return id;
	}

	public void setId(String assignedId) {
		this.id = assignedId;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getOrder()
	 */
	@XmlTransient
	public IOrder getOrder() {
		return order.getOrder();
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#submit()
	 */
	public void submit() throws BrokerException {
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#cancel()
	 */
	public void cancel() throws BrokerException {
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#allowModify()
	 */
	public boolean allowModify() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#modify(org.eclipsetrader.core.trading.IOrder)
	 */
	public void modify(IOrder order) throws BrokerException {
		throw new BrokerException("Modify not allowed");
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#addOrderMonitorListener(org.eclipsetrader.core.trading.IOrderMonitorListener)
	 */
	public void addOrderMonitorListener(IOrderMonitorListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#removeOrderMonitorListener(org.eclipsetrader.core.trading.IOrderMonitorListener)
	 */
	public void removeOrderMonitorListener(IOrderMonitorListener listener) {
		listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getStatus()
	 */
	@XmlTransient
	public IOrderStatus getStatus() {
		return status;
	}

	public void setStatus(IOrderStatus status) {
		IOrderStatus oldValue = this.status;
		if (this.status != status) {
			this.status = status;
			propertyChangeSupport.firePropertyChange(PROP_STATUS, oldValue, this.status);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getFilledQuantity()
	 */
	@XmlTransient
	public Long getFilledQuantity() {
		return filledQuantity;
	}

	public void setFilledQuantity(Long filledQuantity) {
		Long oldValue = this.filledQuantity;
		if (filledQuantity != null && !filledQuantity.equals(this.filledQuantity)) {
			this.filledQuantity = filledQuantity;
			propertyChangeSupport.firePropertyChange(PROP_FILLED_QUANTITY, oldValue, this.filledQuantity);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getAveragePrice()
	 */
	@XmlTransient
	public Double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(Double averagePrice) {
		Double oldValue = this.averagePrice;
		if (averagePrice != null && !averagePrice.equals(this.averagePrice)) {
			this.averagePrice = averagePrice;
			propertyChangeSupport.firePropertyChange(PROP_AVERAGE_PRICE, oldValue, this.averagePrice);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.IOrderMonitor#getMessage()
	 */
	@XmlTransient
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter.isAssignableFrom(propertyChangeSupport.getClass()))
			return propertyChangeSupport;
		if (adapter.isAssignableFrom(getClass()))
			return this;
		return null;
	}

	public void fireOrderCompletedEvent() {
		OrderMonitorEvent event = new OrderMonitorEvent(this, order.getOrder());

		Object[] l = listeners.getListeners();
		for (int i = 0; i < l.length; i++) {
			try {
				((IOrderMonitorListener) l[i]).orderCompleted(event);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@XmlTransient
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public void addTransaction(ITransaction transaction) {
		transactions.add(transaction);
	}

	@XmlTransient
	public ITransaction[] getTransactions() {
		return transactions.toArray(new ITransaction[transactions.size()]);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrderMonitor: id=" + getId() + ", status=" + getStatus() + ", filledQuantity=" + getFilledQuantity() +
		       ", averagePrice=" + getAveragePrice() + " [" + order.toString() + "]";
	}
}

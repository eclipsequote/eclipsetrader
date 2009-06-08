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

package org.eclipsetrader.core.ats;

public interface ITradeSystemService {

	public ITradeStrategy getStrategy(String id);

	public ITradeSystem[] getTradeSystems();

	public void addTradeSystem(ITradeSystem system);

	public void removeTradeSystem(ITradeSystem system);

	public ITradeSystemMonitor start(ITradeSystem system);

	public void stop(ITradeSystem system);

	public void addTradeSystemListener(ITradeSystemListener listener);

	public void removeTradeSystemListener(ITradeSystemListener listener);
}
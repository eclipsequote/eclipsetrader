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

package org.eclipsetrader.core.internal.ats.repository;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipsetrader.core.feed.TimeSpan;

public class TimeSpanAdapter extends XmlAdapter<String, TimeSpan> {

	public TimeSpanAdapter() {
	}

	/* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(TimeSpan v) throws Exception {
	    return v != null ? v.toString() : null;
    }

	/* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public TimeSpan unmarshal(String v) throws Exception {
	    return v != null ? TimeSpan.fromString(v) : null;
    }
}
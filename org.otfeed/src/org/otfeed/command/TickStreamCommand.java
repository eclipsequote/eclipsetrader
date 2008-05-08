/**
 * Copyright 2007 Mike Kroutikov.
 *
 * This program is free software; you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License as 
 *   published by the Free Software Foundation; either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *   You should have received a copy of the Lesser GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.otfeed.command;

import org.otfeed.protocol.ICommand;

/**
 * Request for the real-time (live) quotes.
 * <p/>
 * Generates {@link org.otfeed.event.OTQuote quote}, 
 * {@link org.otfeed.event.OTTrade trade},
 * {@link org.otfeed.event.OTMMQuote marker-maker quote}, and 
 * {@link org.otfeed.event.OTBBO bbo} events.
 */
public final class TickStreamCommand 
		extends ExchangeSymbolAndQuoteDelegateHolder implements ICommand {

	/**
	 * Creates new tick stream command, initializing 
	 * all its properties, except delegates.
	 * 
	 * @param exchangeCode exchange code.
	 * @param symbolCode symbol code.
	 * @param volumeStyle volume reporting style.
	 */
	public TickStreamCommand(String exchangeCode,
			String symbolCode,
			VolumeStyleEnum volumeStyle) {
		setExchangeCode(exchangeCode);
		setSymbolCode(symbolCode);
		setVolumeStyle(volumeStyle);
	}

	/**
	 * Default construtor. Initializes
	 * {@link #getVolumeStyle volumeStyle} property to its default
	 * value of {@link VolumeStyleEnum#COMPOUND COMPOUND}.
	 * Other properties must be set explicitly before using 
	 * this object. 
	 */
	public TickStreamCommand() {
		this(null, null, VolumeStyleEnum.COMPOUND);
	}

	/**
	 * Creates new tick stream command for a single event type, initializing 
	 * all its properties, except <code>individualFlag</code>,
	 * which is set to <code>COMPOUND</code>, and delegates.
	 * 
	 * @param exchangeCode exchange code.
	 * @param symbolCode symbol code.
	 */
	public TickStreamCommand(String exchangeCode,
			String symbolCode) {
		this(exchangeCode, 
				symbolCode,
				VolumeStyleEnum.COMPOUND);
	}
}

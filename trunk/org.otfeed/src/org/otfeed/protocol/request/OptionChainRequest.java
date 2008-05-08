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

package org.otfeed.protocol.request;

import org.otfeed.command.MonthAndYear;
import org.otfeed.command.PriceRange;
import org.otfeed.command.VolumeStyleEnum;
import org.otfeed.event.ICompletionDelegate;
import org.otfeed.event.IDataDelegate;
import org.otfeed.event.OTBBO;
import org.otfeed.event.OTMMQuote;
import org.otfeed.event.OTQuote;
import org.otfeed.event.OTTrade;
import org.otfeed.protocol.CommandEnum;

/**
 * Request to receive option chain events.
 */
public final class OptionChainRequest extends AbstractOptionChainRequest {

	public OptionChainRequest(int requestId, 
			String exchangeCode,
			String symbolCode,
			MonthAndYear expiration,
			PriceRange strikeRange,
			VolumeStyleEnum volumeStyle,
			IDataDelegate<OTQuote>   quoteDelegate,
			IDataDelegate<OTTrade>   tradeDelegate,
			IDataDelegate<OTMMQuote> mmQuoteDelegate,
			IDataDelegate<OTBBO>     bboDelegate,
			ICompletionDelegate completionDelegate) {

		super(CommandEnum.REQUEST_OPTION_CHAIN_U,
				requestId, 
				exchangeCode,
				symbolCode,
				expiration,
				strikeRange,
				volumeStyle,
				quoteDelegate,
				tradeDelegate,
				mmQuoteDelegate,
				bboDelegate,
				completionDelegate
		);
	}

	@Override
	public final CommandEnum getCancelCommand() {
		return CommandEnum.CANCEL_OPTION_CHAIN;
	}
}
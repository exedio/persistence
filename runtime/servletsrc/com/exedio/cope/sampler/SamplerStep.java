/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.sampler;

import com.exedio.cope.ChangeListenerDispatcherInfo;
import com.exedio.cope.ChangeListenerInfo;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.util.Pool;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

final class SamplerStep
{
	final Date date;
	final Instant initialized;
	final Instant connected;
	final Pool.Info connectionPoolInfo;
	final long nextTransactionId;
	final TransactionCounters transactionCounters;
	final ChangeListenerInfo changeListenerInfo;
	final ChangeListenerDispatcherInfo changeListenerDispatcherInfo;
	final long duration;

	final ArrayList<Transaction> transactions;

	SamplerStep(
			final Model sampledModel,
			final Duration transactionDuration)
	{
		// gather data
		final Timer.Sample start = Timer.start();
		date = new Date();
		initialized = sampledModel.getInitializeInstant();
		connected = sampledModel.getConnectInstant();
		connectionPoolInfo = sampledModel.getConnectionPoolInfo();
		nextTransactionId = sampledModel.getNextTransactionId();
		transactionCounters = sampledModel.getTransactionCounters();
		final Collection<Transaction> openTransactions = sampledModel.getOpenTransactions();
		changeListenerInfo = sampledModel.getChangeListenersInfo();
		changeListenerDispatcherInfo = sampledModel.getChangeListenerDispatcherInfo();
		duration = Sampler.stop(start, sampledModel, "gather");

		// process data
		transactions = new ArrayList<>(openTransactions.size());
		{
			final long threshold = date.getTime() - transactionDuration.toMillis();
			for(final Transaction transaction : openTransactions)
			{
				if(transaction.getStartDate().getTime()<=threshold)
					transactions.add(transaction);
			}
		}
	}

	boolean isCompatibleTo(final SamplerStep from)
	{
		if(from==null)
			return false;

		return
			initialized.equals(from.initialized) &&
			connected.equals(from.connected);
	}
}

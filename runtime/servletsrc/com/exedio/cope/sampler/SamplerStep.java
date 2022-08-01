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
import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.ClusterSenderInfo;
import com.exedio.cope.Model;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.util.Pool;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

final class SamplerStep
{
	final Date date;
	final Instant initialized;
	final Instant connected;
	final Pool.Info connectionPoolInfo;
	final long nextTransactionId;
	final TransactionCounters transactionCounters;
	final QueryCacheInfo queryCacheInfo;
	final ChangeListenerInfo changeListenerInfo;
	final ChangeListenerDispatcherInfo changeListenerDispatcherInfo;
	final ClusterSenderInfo clusterSenderInfo;
	final ClusterListenerInfo clusterListenerInfo;
	private final HashMap<Integer, ClusterListenerInfo.Node> clusterListenerInfoNodes;
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
		queryCacheInfo = getQueryCacheInfo(sampledModel);
		changeListenerInfo = sampledModel.getChangeListenersInfo();
		changeListenerDispatcherInfo = sampledModel.getChangeListenerDispatcherInfo();
		clusterSenderInfo = sampledModel.getClusterSenderInfo();
		clusterListenerInfo = sampledModel.getClusterListenerInfo();
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
		if(clusterListenerInfo!=null)
		{
			clusterListenerInfoNodes = new HashMap<>();
			for(final ClusterListenerInfo.Node node : clusterListenerInfo.getNodes())
				if(clusterListenerInfoNodes.putIfAbsent(node.getID(), node)!=null)
					throw new RuntimeException("" + node.getID());
		}
		else
		{
			clusterListenerInfoNodes = null;
		}
	}

	@SuppressWarnings("deprecation")
	private static QueryCacheInfo getQueryCacheInfo(final Model model)
	{
		return model.getQueryCacheInfo();
	}

	boolean isCompatibleTo(final SamplerStep from)
	{
		if(from==null)
			return false;

		assert (clusterSenderInfo!=null)       ==(from.clusterSenderInfo!=null);
		assert (clusterListenerInfo!=null)     ==(from.clusterListenerInfo!=null);
		assert (clusterListenerInfoNodes!=null)==(from.clusterListenerInfoNodes!=null);

		return
			initialized.equals(from.initialized) &&
			connected.equals(from.connected);
	}

	ClusterListenerInfo.Node map(final ClusterListenerInfo.Node node)
	{
		return clusterListenerInfoNodes.get(node.getID());
	}
}

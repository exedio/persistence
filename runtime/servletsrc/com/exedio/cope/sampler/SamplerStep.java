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
import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.ItemCacheStatistics;
import com.exedio.cope.Model;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.misc.MediaSummary;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.Pool;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

final class SamplerStep
{
	final Date date;
	final Date initialized;
	final Date connected;
	final Pool.Info connectionPoolInfo;
	final long nextTransactionId;
	final TransactionCounters transactionCounters;
	final ItemCacheStatistics itemCacheStatistics;
	final QueryCacheInfo queryCacheInfo;
	final ChangeListenerInfo changeListenerInfo;
	final ChangeListenerDispatcherInfo changeListenerDispatcherInfo;
	final MediaInfo[] mediaInfos;
	final int mediasNoSuchPath;
	final ClusterSenderInfo clusterSenderInfo;
	final ClusterListenerInfo clusterListenerInfo;
	private final HashMap<Integer, ClusterListenerInfo.Node> clusterListenerInfoNodes;
	final long duration;

	final ItemCacheInfo[] itemCacheInfos;
	final MediaSummary mediaSummary;
	final ArrayList<Transaction> transactions;

	SamplerStep(
			final Model sampledModel,
			final MediaPath[] medias,
			final Duration transactionDuration)
	{
		// prepare
		this.mediaInfos = new MediaInfo[medias.length];

		// gather data
		final long start = System.nanoTime();
		date = new Date();
		initialized = sampledModel.getInitializeDate();
		connected = sampledModel.getConnectDate();
		connectionPoolInfo = sampledModel.getConnectionPoolInfo();
		nextTransactionId = sampledModel.getNextTransactionId();
		transactionCounters = sampledModel.getTransactionCounters();
		final Collection<Transaction> openTransactions = sampledModel.getOpenTransactions();
		itemCacheStatistics = sampledModel.getItemCacheStatistics();
		queryCacheInfo = sampledModel.getQueryCacheInfo();
		changeListenerInfo = sampledModel.getChangeListenersInfo();
		changeListenerDispatcherInfo = sampledModel.getChangeListenerDispatcherInfo();
		mediasNoSuchPath = MediaPath.getNoSuchPath();
		{
			int i = 0;
			for(final MediaPath path : medias)
				mediaInfos[i++] = path.getInfo();
		}
		clusterSenderInfo = sampledModel.getClusterSenderInfo();
		clusterListenerInfo = sampledModel.getClusterListenerInfo();
		duration = System.nanoTime() - start;

		// process data
		itemCacheInfos = itemCacheStatistics.getDetails();
		mediaSummary = new MediaSummary(mediaInfos);
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

	boolean isCompatibleTo(final SamplerStep from)
	{
		if(from==null)
			return false;

		assert  itemCacheInfos.length          == from.itemCacheInfos.length;
		assert  mediaInfos.length              == from.mediaInfos.length;
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

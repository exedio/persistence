/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.sample;

import static com.exedio.cope.Query.newQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.ClusterSenderInfo;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.Model;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.Selectable;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.Pool;
import com.exedio.cope.util.PrefixSource;
import com.exedio.cope.util.Properties;

public final class Sampler
{
	private final Model historyModel;

	private final String name;
	private final Model watchedModel;
	private final MediaPath[] medias;
	private ConnectToken connectToken = null;

	public Sampler(final Model watchedModel)
	{
		if(watchedModel==null)
			throw new NullPointerException("model");

		this.name = getClass().getSimpleName() + '#' + watchedModel.toString();
		this.watchedModel = watchedModel;

		this.historyModel =
			new Model(
				HistoryRevisions.REVISIONS,
				HistoryModel.TYPE,
				HistoryItemCache.TYPE,
				HistoryClusterNode.TYPE,
				HistoryMedia.TYPE,
				HistoryPurge.TYPE);
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		for(final Type<?> type : watchedModel.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
		this.medias = medias.toArray(new MediaPath[medias.size()]);
	}

	Model getModel()
	{
		return historyModel;
	}

	public void connect()
	{
		// TODO rename table while
		if(connectToken==null)
		{
			connectToken =
				ConnectToken.issue(
						historyModel,
						new ConnectProperties(
								makeSource(new PrefixSource(
										watchedModel.getConnectProperties().getContext(),
										"sampler.")),
								null),
						name);
		}
	}

	private static Properties.Source makeSource(final Properties.Source original)
	{
		return new Properties.Source(){

			public String get(final String key)
			{
				if("cache.item.limit".equals(key) || "cache.query.limit".equals(key))
					return "0";
				return original.get(key);
			}

			public String getDescription()
			{
				return original.getDescription();
			}

			public Collection<String> keySet()
			{
				return original.keySet();
			}
		};
	}

	public void check()
	{
		historyModel.reviseIfSupported();
		try
		{
			historyModel.startTransaction("check");
			historyModel.checkSchema();
			historyModel.commit();
		}
		finally
		{
			historyModel.rollbackIfNotCommitted();
		}
	}

	public void disconnect()
	{
		if(connectToken!=null)
		{
			connectToken.returnIt();
			connectToken = null;
		}
	}

	void store(final int running) // non-private for testing
	{
		// prepare
		final int thread = System.identityHashCode(this);
		final MediaInfo[] mediaInfos = new MediaInfo[medias.length];

		// gather data
		final Date date = new Date();
		final Date initializeDate = watchedModel.getInitializeDate();
		final Date connectDate = watchedModel.getConnectDate();
		final Pool.Info connectionPoolInfo = watchedModel.getConnectionPoolInfo();
		final long nextTransactionId = watchedModel.getNextTransactionId();
		final TransactionCounters transactionCounters = watchedModel.getTransactionCounters();
		final ItemCacheInfo[] itemCacheInfos = watchedModel.getItemCacheInfo();
		final QueryCacheInfo queryCacheInfo = watchedModel.getQueryCacheInfo();
		final int mediasNoSuchPath = MediaPath.getNoSuchPath();
		int mediaValuesIndex = 0;
		for(final MediaPath path : medias)
			mediaInfos[mediaValuesIndex++] = path.getInfo();
		final ClusterSenderInfo clusterSenderInfo = watchedModel.getClusterSenderInfo();
		final ClusterListenerInfo clusterListenerInfo = watchedModel.getClusterListenerInfo();

		// process data
		final ItemCacheSummary itemCacheSummary = new ItemCacheSummary(itemCacheInfos);
		final MediaSummary mediaSummary = new MediaSummary(mediaInfos);
		final ArrayList<SetValue> sv = new ArrayList<SetValue>();

		// save data
		try
		{
			historyModel.startTransaction(name);
			final HistoryModel model;
			{
				sv.clear();
				sv.add(HistoryModel.date.map(date));
				sv.add(HistoryModel.initializeDate.map(initializeDate));
				sv.add(HistoryModel.connectDate.map(connectDate));
				sv.add(HistoryModel.thread.map(thread));
				sv.add(HistoryModel.running.map(running));
				sv.addAll(HistoryModel.map(connectionPoolInfo));
				sv.add(HistoryModel.nextTransactionId.map(nextTransactionId));
				sv.addAll(HistoryModel.map(transactionCounters));
				sv.addAll(HistoryModel.map(itemCacheSummary));
				sv.addAll(HistoryModel.map(queryCacheInfo));
				sv.add(HistoryModel.mediasNoSuchPath.map(mediasNoSuchPath));
				sv.addAll(HistoryModel.map(mediaSummary));
				sv.addAll(HistoryModel.map(clusterSenderInfo));
				sv.addAll(HistoryModel.map(clusterListenerInfo));
				model = HistoryModel.TYPE.newItem(sv);
			}
			{
				for(final ItemCacheInfo info : itemCacheInfos)
				{
					sv.clear();
					sv.addAll(HistoryItemCache.map(model));
					sv.addAll(HistoryItemCache.map(info));
					HistoryItemCache.TYPE.newItem(sv);
				}
			}
			{
				for(final MediaInfo info : mediaInfos)
				{
					sv.clear();
					sv.addAll(HistoryMedia.map(model));
					sv.addAll(HistoryMedia.map(info));
					HistoryMedia.TYPE.newItem(sv);
				}
			}
			if(clusterListenerInfo!=null)
			{
				for(final ClusterListenerInfo.Node node : clusterListenerInfo.getNodes())
				{
					sv.clear();
					sv.addAll(HistoryClusterNode.map(model));
					sv.addAll(HistoryClusterNode.map(node));
					HistoryClusterNode.TYPE.newItem(sv);
				}
			}
			historyModel.commit();
		}
		finally
		{
			historyModel.rollbackIfNotCommitted();
		}
	}

	@Override
	public String toString()
	{
		return name;
	}

	int analyzeCount(final Type type)
	{
		final int result;
		try
		{
			historyModel.startTransaction("history analyze count");
			result = type.newQuery().total();
			historyModel.commit();
		}
		finally
		{
			historyModel.rollbackIfNotCommitted();
		}
		return result;
	}

	Date[] analyzeDate(final Type type)
	{
		final DateField date = (DateField)type.getFeature("date");
		final List dates;
		try
		{
			historyModel.startTransaction("history analyze dates");
			dates = newQuery(new Selectable[]{date.min(), date.max()}, type, null).searchSingleton();
			historyModel.commit();
		}
		finally
		{
			historyModel.rollbackIfNotCommitted();
		}
		return new Date[] {
				(Date)dates.get(0),
				(Date)dates.get(1),
			};
	}
}

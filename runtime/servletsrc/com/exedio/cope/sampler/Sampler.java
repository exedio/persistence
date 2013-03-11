/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.SchemaInfo.newConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.exedio.cope.ChangeListenerDispatcherInfo;
import com.exedio.cope.ChangeListenerInfo;
import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.ClusterSenderInfo;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.Selectable;
import com.exedio.cope.SetValue;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ItemCacheSummary;
import com.exedio.cope.misc.MediaSummary;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Pool;
import com.exedio.cope.util.Properties;

public class Sampler
{
	private final Model samplerModel;

	private final Model sampledModel;
	private final AtomicInteger runningSource = new AtomicInteger(0);
	private final MediaPath[] medias;

	public Sampler(final Model sampledModel)
	{
		if(sampledModel==null)
			throw new NullPointerException("sampledModel");

		this.sampledModel = sampledModel;

		this.samplerModel =
			new Model(
				new SamplerRevisions(),
				SamplerTypeId.TYPE,
				SamplerModel.TYPE,
				SamplerTransaction.TYPE,
				SamplerItemCache.TYPE,
				SamplerClusterNode.TYPE,
				SamplerMedia.TYPE,
				SamplerPurge.TYPE);
		// TODO make a meaningful samplerModel#toString()
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		for(final Type<?> type : sampledModel.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
		this.medias = medias.toArray(new MediaPath[medias.size()]);
	}

	public final Model getModel()
	{
		return samplerModel;
	}

	public static final Properties.Source maskConnectSource(final Properties.Source original)
	{
		return new Properties.Source(){

			public String get(final String key)
			{
				// TODO
				// implement a @CopeNoCache annotation and use it
				// for purged types
				// Then remove the lines below
				if("cache.item.limit".equals(key) || "cache.query.limit".equals(key))
					return "0";

				final String originalResult = original.get(key);
				if(originalResult!=null)
					return originalResult;

				if("schema.revision.table".equals(key))
					return "SamplerRevision";
				if("schema.revision.unique".equals(key))
					return "SamplerRevisionUnique";
				return null;
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

	public final ConnectToken connect(final String tokenName)
	{
		final ConnectToken result = ConnectToken.issue(samplerModel, tokenName);

		boolean mustReturn = true;
		try
		{
			checkInternal();
			mustReturn = false;
		}
		finally
		{
			if(mustReturn)
				result.returnIt();
		}
		// DO NOT WRITE ANYTHING HERE,
		// OTHERWISE ConnectTokens MAY BE LOST
		return result;
	}

	/**
	 * @deprecated Use {@link #connect(String)} for connecting AND checking instead
	 */
	@Deprecated
	public final void check()
	{
		checkInternal();
	}

	void checkInternal()
	{
		samplerModel.reviseIfSupportedAndAutoEnabled();
		try
		{
			samplerModel.startTransaction("check");
			samplerModel.checkSchema();
			samplerModel.commit();
		}
		finally
		{
			samplerModel.rollbackIfNotCommitted();
		}
	}

	public final void sample()
	{
		sampleInternal();
	}

	SamplerModel sampleInternal()
	{
		// prepare
		final MediaInfo[] mediaInfos = new MediaInfo[medias.length];

		// gather data
		final long start = System.nanoTime();
		final Date date = new Date();
		final Date initializeDate = sampledModel.getInitializeDate();
		final Date connectDate = sampledModel.getConnectDate();
		final Pool.Info connectionPoolInfo = sampledModel.getConnectionPoolInfo();
		final long nextTransactionId = sampledModel.getNextTransactionId();
		final TransactionCounters transactionCounters = sampledModel.getTransactionCounters();
		final Collection<Transaction> openTransactions = sampledModel.getOpenTransactions();
		final ItemCacheInfo[] itemCacheInfos = sampledModel.getItemCacheInfo();
		final QueryCacheInfo queryCacheInfo = sampledModel.getQueryCacheInfo();
		final ChangeListenerInfo changeListenerInfo = sampledModel.getChangeListenersInfo();
		final ChangeListenerDispatcherInfo changeListenerDispatcherInfo = sampledModel.getChangeListenerDispatcherInfo();
		final int mediasNoSuchPath = MediaPath.getNoSuchPath();
		int mediaValuesIndex = 0;
		for(final MediaPath path : medias)
			mediaInfos[mediaValuesIndex++] = path.getInfo();
		final ClusterSenderInfo clusterSenderInfo = sampledModel.getClusterSenderInfo();
		final ClusterListenerInfo clusterListenerInfo = sampledModel.getClusterListenerInfo();
		final long duration = System.nanoTime() - start;

		// process data
		final ItemCacheSummary itemCacheSummary = new ItemCacheSummary(itemCacheInfos);
		final MediaSummary mediaSummary = new MediaSummary(mediaInfos);
		final ArrayList<SetValue<?>> sv = new ArrayList<SetValue<?>>();
		final int running = runningSource.getAndIncrement();
		final ArrayList<Transaction> transactions = new ArrayList<Transaction>(openTransactions.size());
		{
			final long threshold = date.getTime() - getTransactionDuration();
			for(final Transaction transaction : openTransactions)
			{
				if(transaction.getStartDate().getTime()<threshold)
					transactions.add(transaction);
			}
		}

		// save data
		try
		{
			samplerModel.startTransaction(toString() + " sample");

			sv.clear();
			sv.add(SamplerModel.date.map(date));
			sv.add(SamplerModel.duration.map(duration));
			sv.add(SamplerModel.initializeDate.map(initializeDate));
			sv.add(SamplerModel.connectDate.map(connectDate));
			sv.add(SamplerModel.sampler.map(System.identityHashCode(this)));
			sv.add(SamplerModel.running.map(running));
			sv.addAll(SamplerModel.map(connectionPoolInfo));
			sv.add(SamplerModel.nextTransactionId.map(nextTransactionId));
			sv.addAll(SamplerModel.map(transactionCounters));
			sv.addAll(SamplerModel.map(itemCacheSummary));
			sv.addAll(SamplerModel.map(queryCacheInfo));
			sv.addAll(SamplerModel.map(changeListenerInfo));
			sv.addAll(SamplerModel.map(changeListenerDispatcherInfo));
			sv.add(SamplerModel.mediasNoSuchPath.map(mediasNoSuchPath));
			sv.addAll(SamplerModel.map(mediaSummary));
			sv.addAll(SamplerModel.map(clusterSenderInfo));
			sv.add(SamplerModel.map(clusterListenerInfo));
			final SamplerModel model = SamplerModel.TYPE.newItem(sv);

			for(final Transaction transaction : transactions)
			{
				sv.clear();
				sv.addAll(SamplerTransaction.map(model));
				sv.addAll(SamplerTransaction.map(transaction));
				SamplerTransaction.TYPE.newItem(sv);
			}
			for(final ItemCacheInfo info : itemCacheInfos)
			{
				sv.clear();
				sv.addAll(SamplerItemCache.map(model));
				sv.addAll(SamplerItemCache.map(info));
				SamplerItemCache.TYPE.newItem(sv);
			}
			for(final MediaInfo info : mediaInfos)
			{
				sv.clear();
				sv.addAll(SamplerMedia.map(model));
				sv.addAll(SamplerMedia.map(info));
				SamplerMedia.TYPE.newItem(sv);
			}
			if(clusterListenerInfo!=null)
			{
				for(final ClusterListenerInfo.Node node : clusterListenerInfo.getNodes())
				{
					sv.clear();
					sv.addAll(SamplerClusterNode.map(model));
					sv.addAll(SamplerClusterNode.map(node));
					SamplerClusterNode.TYPE.newItem(sv);
				}
			}
			samplerModel.commit();
			return model;
		}
		finally
		{
			samplerModel.rollbackIfNotCommitted();
		}
	}

	/**
	 * Return the minimum duration (in milliseconds)
	 * for a transaction to be recorded by the sampler.
	 * This default implementation returns 10 seconds.
	 */
	public long getTransactionDuration()
	{
		return (10*1000);
	}

	int analyzeCount(final Type<?> type)
	{
		final int result;
		try
		{
			samplerModel.startTransaction("sampler analyzeCount");
			result = type.newQuery().total();
			samplerModel.commit();
		}
		finally
		{
			samplerModel.rollbackIfNotCommitted();
		}
		return result;
	}

	Date[] analyzeDate(final Type<?> type)
	{
		final DateField date = (DateField)type.getFeature("date");
		final List<?> dates;
		try
		{
			samplerModel.startTransaction("sampler analyzeDate");
			dates = newQuery(new Selectable<?>[]{date.min(), date.max()}, type, null).searchSingleton();
			samplerModel.commit();
		}
		finally
		{
			samplerModel.rollbackIfNotCommitted();
		}
		return new Date[] {
				(Date)dates.get(0),
				(Date)dates.get(1),
			};
	}

	public final void purge(final int days, final JobContext ctx)
	{
		if(days<=0)
			throw new IllegalArgumentException(String.valueOf(days));

		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(GregorianCalendar.DATE, -days);
		purge(cal.getTime(), ctx);
	}

	public final void purge(final Date limit, final JobContext ctx)
	{
		final ArrayList<Type<?>> types = new ArrayList<Type<?>>();
		for(final Type<?> type : samplerModel.getTypes())
		{
			if(SamplerModel.TYPE!=type && // purge SamplerModel at the end
					SamplerTypeId.TYPE!=type && SamplerPurge.TYPE!=type)
			{
				types.add(type);
			}
		}
		types.add(SamplerModel.TYPE);

		final String samplerString = toString();
		try
		{
			final Connection connection = newConnection(samplerModel);
			try
			{
				for(final Type<?> type : types)
					SamplerPurge.purge(connection, type, limit, ctx, samplerString);
			}
			finally
			{
				connection.close();
			}
		}
		catch (final SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public final List<Query<List<Object>>> differentiate()
	{
		return differentiate(null, null);
	}

	@SuppressWarnings("static-method")
	public final List<Query<List<Object>>> differentiate(final Date from, final Date until)
	{
		return Differentiate.differentiate(from, until);
	}

	@Override
	public final String toString()
	{
		// NOTE:
		// The result of sampledModel.toString() may not be
		// constant over time, therefore we need to compute
		// the result live.
		return "Sampler#" + sampledModel.toString();
	}
}

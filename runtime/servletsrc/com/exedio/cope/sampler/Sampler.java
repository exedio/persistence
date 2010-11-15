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

package com.exedio.cope.sampler;

import static com.exedio.cope.Query.newQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Pool;
import com.exedio.cope.util.PrefixSource;
import com.exedio.cope.util.Properties;

public final class Sampler
{
	private final Model samplerModel;

	private final String name;
	private final Model sampledModel;
	private final AtomicInteger runningSource = new AtomicInteger(0);
	private final MediaPath[] medias;

	public Sampler(final Model sampledModel)
	{
		if(sampledModel==null)
			throw new NullPointerException("sampledModel");

		this.name = getClass().getSimpleName() + '#' + sampledModel.toString();
		this.sampledModel = sampledModel;

		this.samplerModel =
			new Model(
				SamplerRevisions.REVISIONS,
				SamplerModel.TYPE,
				SamplerItemCache.TYPE,
				SamplerClusterNode.TYPE,
				SamplerMedia.TYPE,
				SamplerPurge.TYPE);
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		for(final Type<?> type : sampledModel.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
		this.medias = medias.toArray(new MediaPath[medias.size()]);
	}

	public ConnectToken connect()
	{
		final Properties.Source sampledContext = sampledModel.getConnectProperties().getContext();
		return
			ConnectToken.issue(
					samplerModel,
					new ConnectProperties(
							filterSource(new PrefixSource(
									sampledContext,
									"sampler.")),
							sampledContext),
					name);
	}

	private static Properties.Source filterSource(final Properties.Source original)
	{
		return new Properties.Source(){

			public String get(final String key)
			{
				final String originalResult = original.get(key);
				if(originalResult!=null)
					return originalResult;

				if("cache.item.limit".equals(key) || "cache.query.limit".equals(key))
					return "0";
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

	void check()
	{
		samplerModel.reviseIfSupported();
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

	public void sample()
	{
		// prepare
		final int thread = System.identityHashCode(this);
		final MediaInfo[] mediaInfos = new MediaInfo[medias.length];

		// gather data
		final long start = System.nanoTime();
		final Date date = new Date();
		final Date initializeDate = sampledModel.getInitializeDate();
		final Date connectDate = sampledModel.getConnectDate();
		final Pool.Info connectionPoolInfo = sampledModel.getConnectionPoolInfo();
		final long nextTransactionId = sampledModel.getNextTransactionId();
		final TransactionCounters transactionCounters = sampledModel.getTransactionCounters();
		final ItemCacheInfo[] itemCacheInfos = sampledModel.getItemCacheInfo();
		final QueryCacheInfo queryCacheInfo = sampledModel.getQueryCacheInfo();
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
		final ArrayList<SetValue> sv = new ArrayList<SetValue>();
		final int running = runningSource.getAndIncrement();

		// save data
		try
		{
			samplerModel.startTransaction(name);
			final SamplerModel model;
			{
				sv.clear();
				sv.add(SamplerModel.date.map(date));
				sv.add(SamplerModel.duration.map(duration));
				sv.add(SamplerModel.initializeDate.map(initializeDate));
				sv.add(SamplerModel.connectDate.map(connectDate));
				sv.add(SamplerModel.thread.map(thread));
				sv.add(SamplerModel.running.map(running));
				sv.addAll(SamplerModel.map(connectionPoolInfo));
				sv.add(SamplerModel.nextTransactionId.map(nextTransactionId));
				sv.addAll(SamplerModel.map(transactionCounters));
				sv.addAll(SamplerModel.map(itemCacheSummary));
				sv.addAll(SamplerModel.map(queryCacheInfo));
				sv.add(SamplerModel.mediasNoSuchPath.map(mediasNoSuchPath));
				sv.addAll(SamplerModel.map(mediaSummary));
				sv.addAll(SamplerModel.map(clusterSenderInfo));
				sv.addAll(SamplerModel.map(clusterListenerInfo));
				model = SamplerModel.TYPE.newItem(sv);
			}
			{
				for(final ItemCacheInfo info : itemCacheInfos)
				{
					sv.clear();
					sv.addAll(SamplerItemCache.map(model));
					sv.addAll(SamplerItemCache.map(info));
					SamplerItemCache.TYPE.newItem(sv);
				}
			}
			{
				for(final MediaInfo info : mediaInfos)
				{
					sv.clear();
					sv.addAll(SamplerMedia.map(model));
					sv.addAll(SamplerMedia.map(info));
					SamplerMedia.TYPE.newItem(sv);
				}
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
		}
		finally
		{
			samplerModel.rollbackIfNotCommitted();
		}
	}

	int analyzeCount(final Type type)
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

	Date[] analyzeDate(final Type type)
	{
		final DateField date = (DateField)type.getFeature("date");
		final List dates;
		try
		{
			samplerModel.startTransaction("sampler analyzeDate");
			dates = newQuery(new Selectable[]{date.min(), date.max()}, type, null).searchSingleton();
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

	void purge(final int days, final JobContext ctx) // TODO make public
	{
		if(days<=0)
			throw new IllegalArgumentException(String.valueOf(days));

		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(cal.DATE, -days);
		purge(cal.getTime(), ctx);
	}

	void purge(final Date limit, final JobContext ctx) // TODO make public
	{
		for(final Type type : samplerModel.getTypes())
			if(SamplerModel.TYPE!=type && // purge SamplerModel at the end
				SamplerPurge.TYPE!=type)
			{
				SamplerPurge.purge(type, limit, ctx);
			}

		SamplerPurge.purge(SamplerModel.TYPE, limit, ctx);
	}

	@Override
	public String toString()
	{
		return name;
	}
}

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

import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.sampler.Util.maD;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Selectable;
import com.exedio.cope.SetValue;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sampler
{
	private static final MediaPath[] EMPTY_MEDIA_PATH_ARRAY = new MediaPath[0];

	private final Model samplerModel;

	private final Model sampledModel;
	private final MediaPath[] medias;

	public Sampler(final Model sampledModel)
	{
		this.sampledModel = requireNonNull(sampledModel, "sampledModel");

		this.samplerModel = Model.builder().
				name("Sampler(" + sampledModel + ')').
				add(new SamplerRevisions()).
				add(
					SamplerTypeId.TYPE,
					SamplerMediaId.TYPE,

					SamplerModel.TYPE,
					SamplerTransaction.TYPE,
					SamplerItemCache.TYPE,
					SamplerClusterNode.TYPE,
					SamplerMedia.TYPE,

					SamplerEnvironment.TYPE,
					SamplerPurge.TYPE).
				build();
		final ArrayList<MediaPath> medias = new ArrayList<>();
		for(final Type<?> type : sampledModel.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
		this.medias = medias.toArray(EMPTY_MEDIA_PATH_ARRAY);
	}

	public final Model getModel()
	{
		return samplerModel;
	}

	/**
	 * @deprecated Use {@link SamplerProperties} instead
	 */
	@Deprecated
	public static final Properties.Source maskConnectSource(final Properties.Source original)
	{
		return original;
	}

	public final ConnectToken connect(final String tokenName)
	{
		//noinspection resource OK: is closed outside this factory method
		return ConnectToken.issue(samplerModel, tokenName).returnOnFailureOf(t -> checkInternal());
		// DO NOT WRITE ANYTHING HERE,
		// OTHERWISE ConnectTokens MAY BE LOST
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
	}

	private SamplerStep lastStep = null;

	SamplerModel sampleInternal(final Duration transactionDuration, final String buildTag)
	{
		try(TransactionTry tx = samplerModel.startTransactionTry(this + " sample environment"))
		{
			SamplerEnvironment.sample(sampledModel, buildTag);
			tx.commit();
		}

		final SamplerStep to = new SamplerStep(sampledModel, medias, transactionDuration);
		final SamplerStep from = lastStep;
		lastStep = to;
		if(!to.isCompatibleTo(from))
			return null;

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		// save data
		try(TransactionTry tx = samplerModel.startTransactionTry(this + " sample"))
		{
			sv.clear();
			sv.add(SamplerModel.from.map(from.date));
			sv.add(SamplerModel.date.map(to.date));
			sv.add(SamplerModel.duration.map(to.duration));
			sv.add(SamplerModel.initialized.map(to.initialized));
			sv.add(SamplerModel.connected.map(to.connected));
			sv.addAll(SamplerModel.mapIt(from.connectionPoolInfo, to.connectionPoolInfo));
			sv.add(maD(SamplerModel.nextTransactionId, from.nextTransactionId, to.nextTransactionId));
			sv.addAll(SamplerModel.mapIt(from.transactionCounters, to.transactionCounters));
			sv.addAll(SamplerModel.mapIt(from.itemCacheStatistics, to.itemCacheStatistics));
			sv.addAll(SamplerModel.mapIt(from.queryCacheInfo, to.queryCacheInfo));
			sv.addAll(SamplerModel.mapIt(from.changeListenerInfo, to.changeListenerInfo));
			sv.addAll(SamplerModel.mapIt(from.changeListenerDispatcherInfo, to.changeListenerDispatcherInfo));
			sv.add(maD(SamplerModel.mediasNoSuchPath, from.mediasNoSuchPath, to.mediasNoSuchPath));
			sv.addAll(SamplerModel.mapIt(from.mediaSummary, to.mediaSummary));
			sv.add(SamplerModel.mapIt(from.clusterSenderInfo, to.clusterSenderInfo));
			sv.add(SamplerModel.mapIt(from.clusterListenerInfo, to.clusterListenerInfo));
			final SamplerModel model = SamplerModel.TYPE.newItem(sv);

			for(final Transaction transaction : to.transactions)
			{
				sv.clear();
				sv.add(SamplerTransaction.mapIt(model));
				sv.addAll(SamplerTransaction.mapIt(transaction));
				SamplerTransaction.TYPE.newItem(sv);
			}
			for(int i = 0; i<to.itemCacheInfos.length; i++)
			{
				final List<SetValue<?>> payLoad = SamplerItemCache.mapIt(from.itemCacheInfos[i], to.itemCacheInfos[i]);
				if(payLoad!=null)
				{
					sv.clear();
					sv.add(SamplerItemCache.mapIt(model));
					sv.addAll(payLoad);
					SamplerItemCache.TYPE.newItem(sv);
				}
			}
			for(int i = 0; i<to.mediaInfos.length; i++)
			{
				final List<SetValue<?>> payLoad = SamplerMedia.mapIt(from.mediaInfos[i], to.mediaInfos[i]);
				if(payLoad!=null)
				{
					sv.clear();
					sv.add(SamplerMedia.mapIt(model));
					sv.addAll(payLoad);
					SamplerMedia.TYPE.newItem(sv);
				}
			}
			if(to.clusterListenerInfo!=null)
			{
				for(final ClusterListenerInfo.Node toNode : to.clusterListenerInfo.getNodes())
				{
					final ClusterListenerInfo.Node fromNode = from.map(toNode);
					if(fromNode!=null)
					{
						sv.clear();
						sv.add(SamplerClusterNode.mapIt(model));
						sv.addAll(SamplerClusterNode.mapIt(fromNode, toNode));
						SamplerClusterNode.TYPE.newItem(sv);
					}
				}
			}
			tx.commit();
			return model;
		}
	}

	void reset()
	{
		lastStep = null;
	}

	@SuppressFBWarnings("NP_LOAD_OF_KNOWN_NULL_VALUE")
	int analyzeCount(final Type<?> type)
	{
		try(TransactionTry tx = samplerModel.startTransactionTry("sampler analyzeCount"))
		{
			return tx.commit(
					type.newQuery().total()
			);
		}
	}

	Date[] analyzeDate(final Type<?> type)
	{
		final DateField date = (DateField)type.getFeature("date");
		final List<?> dates;
		try(TransactionTry tx = samplerModel.startTransactionTry("sampler analyzeDate"))
		{
			dates = newQuery(new Selectable<?>[]{date.min(), date.max()}, type, null).searchSingleton();
			tx.commit();
		}
		//noinspection ConstantConditions OK: searchSingleton cannot return null on aggregation with multiple selects
		return new Date[] {
				(Date)dates.get(0),
				(Date)dates.get(1),
			};
	}

	public final void purge(final int days, final JobContext ctx)
	{
		if(days<=0)
			throw new IllegalArgumentException(String.valueOf(days));

		purge(beforeNow(Clock.currentTimeMillis(), Duration.ofDays(days)), ctx);
	}

	static Date beforeNow(final long now, final Duration duration)
	{
		return new Date(now - duration.toMillis());
	}

	public final void purge(final Date limit, final JobContext ctx)
	{
		requireNonNull(limit, "limit");
		requireNonNull(ctx, "ctx");
		final HashMap<Type<?>, Date> map = new HashMap<>();
		for(final Type<?> type : samplerModel.getTypes())
			if(type.isAnnotationPresent(Purgeable.class))
				map.put(type, limit);
		purge(map, ctx);
	}

	final void purge(final Map<Type<?>,Date> limit, final JobContext ctx)
	{
		final ArrayList<Type<?>> types = new ArrayList<>();
		final ArrayList<Type<?>> lastTypes = new ArrayList<>();
		for(final Type<?> type : samplerModel.getTypes())
		{
			final Purgeable purgeable = type.getAnnotation(Purgeable.class);
			if(purgeable!=null)
			{
				(purgeable.last() ? lastTypes : types).add(type);
			}
		}
		types.addAll(lastTypes);

		final String samplerString = toString();
		try(Connection connection = newConnection(samplerModel))
		{
			for(final Type<?> type : types)
				SamplerPurge.purge(connection, type, limit.get(type), ctx, samplerString);
		}
		catch (final SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public final String toString()
	{
		// NOTE:
		// The result of sampledModel.toString() may not be
		// constant over time, therefore we need to compute
		// the result live.
		return "Sampler#" + sampledModel;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link SamplerProperties#sample(Sampler)} instead.
	 */
	@Deprecated
	public final void sample()
	{
		sampleInternal(Duration.ofMillis(getTransactionDuration()), null);
	}

	/**
	 * Return the minimum duration (in milliseconds)
	 * for a transaction to be recorded by the sampler.
	 * This default implementation returns 10 seconds.
	 * @deprecated Use {@link SamplerProperties#sample(Sampler)} instead.
	 */
	@Deprecated
	public long getTransactionDuration()
	{
		return (10*1000);
	}

	/**
	 * @deprecated
	 * This method is no longer needed.
	 * Always returns an empty list.
	 */
	@Deprecated
	public final List<Query<List<Object>>> differentiate()
	{
		return differentiate(null, null);
	}

	/**
	 * @deprecated
	 * This method is no longer needed.
	 * Always returns an empty list.
	 */
	@Deprecated
	@SuppressWarnings({"static-method", "MethodMayBeStatic", "unused"})
	public final List<Query<List<Object>>> differentiate(final Date from, final Date until)
	{
		return Collections.emptyList();
	}
}

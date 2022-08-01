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

import com.exedio.cope.DateField;
import com.exedio.cope.Model;
import com.exedio.cope.Selectable;
import com.exedio.cope.SetValue;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sampler
{
	private final Model samplerModel;

	private final Model sampledModel;

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

	@SuppressWarnings("unused") // TODO test
	public final ConnectToken connect(final String tokenName)
	{
		//noinspection resource OK: is closed outside this factory method
		return ConnectToken.issue(samplerModel, tokenName).returnOnFailureOf(t -> checkInternal());
		// DO NOT WRITE ANYTHING HERE,
		// OTHERWISE ConnectTokens MAY BE LOST
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

		final SamplerStep to = new SamplerStep(sampledModel, transactionDuration);
		final SamplerStep from = lastStep;
		lastStep = to;
		if(!to.isCompatibleTo(from))
			return null;

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		// save data
		final Timer.Sample start = Timer.start();
		try(TransactionTry tx = samplerModel.startTransactionTry(this + " sample"))
		{
			//noinspection RedundantOperationOnEmptyContainer
			sv.clear();
			sv.add(SamplerModel.from.map(from.date));
			sv.add(SamplerModel.date.map(to.date));
			sv.add(SamplerModel.duration.map(to.duration));
			sv.add(SamplerModel.initialized.map(Date.from(to.initialized)));
			sv.add(SamplerModel.connected.map(Date.from(to.connected)));
			sv.addAll(SamplerModel.mapIt(from.connectionPoolInfo, to.connectionPoolInfo));
			sv.add(maD(SamplerModel.nextTransactionId, from.nextTransactionId, to.nextTransactionId));
			sv.addAll(SamplerModel.mapIt(from.transactionCounters, to.transactionCounters));
			sv.addAll(SamplerModel.mapItemCacheStatisticsDummy());
			sv.addAll(SamplerModel.mapQueryCacheInfoDummy());
			sv.addAll(SamplerModel.mapIt(from.changeListenerInfo, to.changeListenerInfo));
			sv.addAll(SamplerModel.mapChangeListenerDispatcherInfoDummy());
			sv.add(SamplerModel.mediasNoSuchPath.map(SamplerModel.DUMMY));
			sv.addAll(SamplerModel.mapMediaSummaryDummy());
			sv.add(SamplerModel.mapClusterSenderInfoDummy());
			sv.add(SamplerModel.mapClusterListenerInfoDummy());
			final SamplerModel model = SamplerModel.TYPE.newItem(sv);

			for(final Transaction transaction : to.transactions)
			{
				sv.clear();
				sv.add(SamplerTransaction.mapIt(model));
				sv.addAll(SamplerTransaction.mapIt(transaction));
				SamplerTransaction.TYPE.newItem(sv);
			}
			tx.commit();
			stop(start, sampledModel, "store");
			return model;
		}
	}

	static final long stop(final Timer.Sample start, final Model sampledModel, final String phase)
	{
		return start.stop(Metrics.timer(
				Sampler.class.getName(),
				Tags.of("model", sampledModel.toString(), "phase", phase)));
	}

	void reset()
	{
		lastStep = null;
	}

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
}

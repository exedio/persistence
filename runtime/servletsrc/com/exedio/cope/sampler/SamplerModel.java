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

import static com.exedio.cope.sampler.StringUtil.diff;

import java.util.Arrays;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ChangeListenerDispatcherInfo;
import com.exedio.cope.ChangeListenerInfo;
import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.ClusterSenderInfo;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.misc.ItemCacheSummary;
import com.exedio.cope.misc.MediaSummary;
import com.exedio.cope.pattern.CompositeField;
import com.exedio.cope.util.Pool;

@Purgeable(last=true)
@CopeSchemaName("SamplerModelDiff")
final class SamplerModel extends Item
{
	static final DateField from = new DateField().toFinal().unique();
	static final DateField date = new DateField().toFinal().unique();
	static final LongField duration = new LongField().toFinal();
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();

	private static final IntegerField connectionPoolIdle = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolPut = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnPut = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final Pool.Info from,
			final Pool.Info to)
	{
		return Arrays.asList((SetValue<?>)
			connectionPoolIdle.map(to.getIdleLevel()),
			diff(connectionPoolGet, from.getCounter().getGetCounter(), to.getCounter().getGetCounter()),
			diff(connectionPoolPut, from.getCounter().getPutCounter(), to.getCounter().getPutCounter()),
			diff(connectionPoolInvalidOnGet, from.getInvalidOnGet(), to.getInvalidOnGet()),
			diff(connectionPoolInvalidOnPut, from.getInvalidOnPut(), to.getInvalidOnPut()));
	}


	static final LongField nextTransactionId = new LongField().toFinal().min(0);

	@CopeSchemaName("commitOutConnection")
	private static final LongField commitWithoutConnection = new LongField().toFinal().min(0);
	private static final LongField commitWithConnection    = new LongField().toFinal().min(0);
	@CopeSchemaName("rollbackOutConnection")
	private static final LongField rollbackWithoutConnection = new LongField().toFinal().min(0);
	private static final LongField rollbackWithConnection    = new LongField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final TransactionCounters from,
			final TransactionCounters to)
	{
		return Arrays.asList((SetValue<?>)
			diff(commitWithoutConnection,   from.getCommitWithoutConnection(),   to.getCommitWithoutConnection()),
			diff(commitWithConnection,      from.getCommitWithConnection(),      to.getCommitWithConnection()),
			diff(rollbackWithoutConnection, from.getRollbackWithoutConnection(), to.getRollbackWithoutConnection()),
			diff(rollbackWithConnection,    from.getRollbackWithConnection(),    to.getRollbackWithConnection()));
	}


	private static final LongField itemCacheHits = new LongField().toFinal().min(0);
	private static final LongField itemCacheMisses = new LongField().toFinal().min(0);
	private static final LongField itemCacheConcurrentLoads = new LongField().toFinal().min(0);
	private static final IntegerField itemCacheReplacementRuns = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheReplacements = new IntegerField().toFinal().min(0);
	private static final LongField itemCacheInvalidationsOrdered = new LongField().toFinal().min(0);
	private static final LongField itemCacheInvalidationsDone = new LongField().toFinal().min(0);

	private static final IntegerField itemCacheInvalidateLastSize = new IntegerField().toFinal().min(0);
	private static final LongField itemCacheInvalidateLastHits = new LongField().toFinal().min(0);
	private static final LongField itemCacheInvalidateLastPurged = new LongField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final ItemCacheSummary from,
			final ItemCacheSummary to)
	{
		return Arrays.asList((SetValue<?>)
			diff(itemCacheHits,   from.getHits(), to.getHits()),
			diff(itemCacheMisses, from.getMisses(), to.getMisses()),

			diff(itemCacheConcurrentLoads, from.getConcurrentLoads(), to.getConcurrentLoads()),
			diff(itemCacheReplacementRuns, from.getReplacementRuns(), to.getReplacementRuns()),
			diff(itemCacheReplacements,    from.getReplacements(),    to.getReplacements()),

			diff(itemCacheInvalidationsOrdered, from.getInvalidationsOrdered(), to.getInvalidationsOrdered()),
			diff(itemCacheInvalidationsDone,    from.getInvalidationsDone(),    to.getInvalidationsDone()),

			itemCacheInvalidateLastSize  .map(to.getInvalidateLastSize()),
			diff(itemCacheInvalidateLastHits,   from.getInvalidateLastHits(),   to.getInvalidateLastHits()),
			diff(itemCacheInvalidateLastPurged, from.getInvalidateLastPurged(), to.getInvalidateLastPurged()));
	}


	private static final LongField queryCacheHits = new LongField().toFinal().min(0);
	private static final LongField queryCacheMisses = new LongField().toFinal().min(0);
	private static final LongField queryCacheReplacements = new LongField().toFinal().min(0);
	private static final LongField queryCacheInvalidations = new LongField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final QueryCacheInfo from,
			final QueryCacheInfo to)
	{
		return Arrays.asList((SetValue<?>)
			diff(queryCacheHits,          from.getHits(),          to.getHits()),
			diff(queryCacheMisses,        from.getMisses(),        to.getMisses()),
			diff(queryCacheReplacements,  from.getReplacements(),  to.getReplacements()),
			diff(queryCacheInvalidations, from.getInvalidations(), to.getInvalidations()));
	}


	private static final IntegerField changeListenerCleared  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerRemoved  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerFailed   = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final ChangeListenerInfo from,
			final ChangeListenerInfo to)
	{
		return Arrays.asList((SetValue<?>)
			diff(changeListenerCleared, from.getCleared(), to.getCleared()),
			diff(changeListenerRemoved, from.getRemoved(), to.getRemoved()),
			diff(changeListenerFailed,  from.getFailed(),  to.getFailed()));
	}


	private static final    LongField changeListenerOverflow  = new LongField   ().toFinal().min(0);
	private static final    LongField changeListenerException = new LongField   ().toFinal().min(0);
	private static final IntegerField changeListenerPending   = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final ChangeListenerDispatcherInfo from,
			final ChangeListenerDispatcherInfo to)
	{
		return Arrays.asList((SetValue<?>)
			diff(changeListenerOverflow,  from.getOverflow (), to.getOverflow ()),
			diff(changeListenerException, from.getException(), to.getException()),
			changeListenerPending.map( to.getPending()));
	}


	static final IntegerField mediasNoSuchPath = new IntegerField().toFinal().min(0);

	private static final IntegerField mediasRedirectFrom  = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasException     = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasGuessedUrl    = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotAnItem     = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNoSuchItem    = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasMoved         = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasIsNull        = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotComputable = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotModified   = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasDelivered     = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(
			final MediaSummary from,
			final MediaSummary to)
	{
		return Arrays.asList((SetValue<?>)
			diff(mediasRedirectFrom,  from.getRedirectFrom(),  to.getRedirectFrom()),
			diff(mediasException,     from.getException(),     to.getException()),
			diff(mediasGuessedUrl,    from.getGuessedUrl(),    to.getGuessedUrl()),
			diff(mediasNotAnItem,     from.getNotAnItem(),     to.getNotAnItem()),
			diff(mediasNoSuchItem,    from.getNoSuchItem(),    to.getNoSuchItem()),
			diff(mediasMoved,         from.getMoved(),         to.getMoved()),
			diff(mediasIsNull,        from.getIsNull(),        to.getIsNull()),
			diff(mediasNotComputable, from.getNotComputable(), to.getNotComputable()),
			diff(mediasNotModified,   from.getNotModified(),   to.getNotModified()),
			diff(mediasDelivered,     from.getDelivered(),     to.getDelivered()));
	}


	private static final LongField clusterSenderInvalidationSplit = new LongField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<Long>> map(
			final ClusterSenderInfo from,
			final ClusterSenderInfo to)
	{
		return Arrays.asList(
			(from!=null&&to!=null)
			? diff(clusterSenderInvalidationSplit, from.getInvalidationSplit(), to.getInvalidationSplit())
			: clusterSenderInvalidationSplit.map(0l));
	}


	private static final CompositeField<SamplerClusterListener> clusterListener = CompositeField.create(SamplerClusterListener.class).toFinal().optional();

	static SetValue<?> map(
			final ClusterListenerInfo from,
			final ClusterListenerInfo to)
	{
		return clusterListener.map(
			(from!=null&&to!=null) ? new SamplerClusterListener(from, to) : null
		);
	}


	@SuppressWarnings("unused")
	private SamplerModel(final ActivationParameters ap)
	{
		super(ap);
	}

	private static final long serialVersionUID = 1l;

	static final Type<SamplerModel> TYPE = TypesBound.newType(SamplerModel.class);
}

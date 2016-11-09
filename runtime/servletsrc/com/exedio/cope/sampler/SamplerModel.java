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

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.sampler.Util.maD;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ChangeListenerDispatcherInfo;
import com.exedio.cope.ChangeListenerInfo;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.ClusterSenderInfo;
import com.exedio.cope.CopeExternal;
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
import java.util.Arrays;
import java.util.List;

@Purgeable(last=true)
@CopeExternal
@CopeSchemaName("DiffModel")
final class SamplerModel extends Item
{
	static final DateField from        = new DateField().toFinal().unique();
	static final DateField date        = new DateField().toFinal().unique();
	static final LongField duration    = new LongField().toFinal();
	static final DateField initialized = new DateField().toFinal();
	static final DateField connected   = new DateField().toFinal();
	@SuppressWarnings("unused")
	private static final CheckConstraint fromBeforeDate = new CheckConstraint(from.less(date));

	private static final IntegerField connectionPoolIdle         = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolGet          = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolPut          = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnPut = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final Pool.Info from,
			final Pool.Info to)
	{
		return Arrays.asList((SetValue<?>)
			map(connectionPoolIdle, to.getIdleLevel()),
			maD(connectionPoolGet, from.getCounter().getGetCounter(), to.getCounter().getGetCounter()),
			maD(connectionPoolPut, from.getCounter().getPutCounter(), to.getCounter().getPutCounter()),
			maD(connectionPoolInvalidOnGet, from.getInvalidOnGet(), to.getInvalidOnGet()),
			maD(connectionPoolInvalidOnPut, from.getInvalidOnPut(), to.getInvalidOnPut()));
	}


	static final IntegerField nextTransactionId = new IntegerField().toFinal().min(0);

	@CopeSchemaName("commitOutConnection")
	private static final IntegerField commitWithoutConnection   = new IntegerField().toFinal().min(0);
	private static final IntegerField commitWithConnection      = new IntegerField().toFinal().min(0);
	@CopeSchemaName("rollbackOutConnection")
	private static final IntegerField rollbackWithoutConnection = new IntegerField().toFinal().min(0);
	private static final IntegerField rollbackWithConnection    = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final TransactionCounters from,
			final TransactionCounters to)
	{
		return Arrays.asList((SetValue<?>)
			maD(commitWithoutConnection,   from.getCommitWithoutConnection  (), to.getCommitWithoutConnection  ()),
			maD(commitWithConnection,      from.getCommitWithConnection     (), to.getCommitWithConnection     ()),
			maD(rollbackWithoutConnection, from.getRollbackWithoutConnection(), to.getRollbackWithoutConnection()),
			maD(rollbackWithConnection,    from.getRollbackWithConnection   (), to.getRollbackWithConnection   ()));
	}


	private static final IntegerField itemCacheHits                 = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheMisses               = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheConcurrentLoads      = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheReplacementRuns      = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheReplacements         = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheInvalidationsOrdered = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheInvalidationsDone    = new IntegerField().toFinal().min(0);

	@CopeSchemaName("itemCacheInvalidateLastSize")
	private static final IntegerField itemCacheStampsSize   = new IntegerField().toFinal().min(0);
	@CopeSchemaName("itemCacheInvalidateLastHits")
	private static final IntegerField itemCacheStampsHits   = new IntegerField().toFinal().min(0);
	@CopeSchemaName("itemCacheInvalidateLastPurged")
	private static final IntegerField itemCacheStampsPurged = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final ItemCacheSummary from,
			final ItemCacheSummary to)
	{
		return Arrays.asList((SetValue<?>)
			maD(itemCacheHits,   from.getHits  (), to.getHits  ()),
			maD(itemCacheMisses, from.getMisses(), to.getMisses()),

			maD(itemCacheConcurrentLoads, from.getConcurrentLoads(), to.getConcurrentLoads()),
			maD(itemCacheReplacementRuns, 0,									0),
			maD(itemCacheReplacements,    from.getReplacementsL  (), to.getReplacementsL  ()),

			maD(itemCacheInvalidationsOrdered, from.getInvalidationsOrdered(), to.getInvalidationsOrdered()),
			maD(itemCacheInvalidationsDone,    from.getInvalidationsDone   (), to.getInvalidationsDone   ()),

			map(itemCacheStampsSize,   to.getStampsSize()),
			maD(itemCacheStampsHits,   from.getStampsHits  (), to.getStampsHits  ()),
			maD(itemCacheStampsPurged, from.getStampsPurged(), to.getStampsPurged()));
	}


	private static final IntegerField queryCacheHits            = new IntegerField().toFinal().min(0);
	private static final IntegerField queryCacheMisses          = new IntegerField().toFinal().min(0);
	private static final IntegerField queryCacheReplacements    = new IntegerField().toFinal().min(0);
	private static final IntegerField queryCacheInvalidations   = new IntegerField().toFinal().min(0);
	private static final IntegerField queryCacheConcurrentLoads = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final QueryCacheInfo from,
			final QueryCacheInfo to)
	{
		return Arrays.asList((SetValue<?>)
			maD(queryCacheHits,            from.getHits           (), to.getHits           ()),
			maD(queryCacheMisses,          from.getMisses         (), to.getMisses         ()),
			maD(queryCacheReplacements,    from.getReplacements   (), to.getReplacements   ()),
			maD(queryCacheInvalidations,   from.getInvalidations  (), to.getInvalidations  ()),
			maD(queryCacheConcurrentLoads, from.getConcurrentLoads(), to.getConcurrentLoads()));
	}


	private static final IntegerField changeListenerSize     = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerCleared  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerRemoved  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerFailed   = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final ChangeListenerInfo from,
			final ChangeListenerInfo to)
	{
		return Arrays.asList((SetValue<?>)
			map(changeListenerSize,    to.getSize()),
			maD(changeListenerCleared, from.getCleared(), to.getCleared()),
			maD(changeListenerRemoved, from.getRemoved(), to.getRemoved()),
			maD(changeListenerFailed,  from.getFailed (), to.getFailed ()));
	}


	private static final IntegerField changeListenerOverflow  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerException = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerPending   = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final ChangeListenerDispatcherInfo from,
			final ChangeListenerDispatcherInfo to)
	{
		return Arrays.asList((SetValue<?>)
			maD(changeListenerOverflow,  from.getOverflow (), to.getOverflow ()),
			maD(changeListenerException, from.getException(), to.getException()),
			map(changeListenerPending,   to.getPending()));
	}


	static final IntegerField mediasNoSuchPath = new IntegerField().toFinal().min(0);

	private static final IntegerField mediasRedirectFrom   = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasException      = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasInvalidSpecial = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasGuessedUrl     = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotAnItem      = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNoSuchItem     = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasMoved          = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasIsNull         = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotComputable  = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotModified    = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasDelivered      = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final MediaSummary from,
			final MediaSummary to)
	{
		return Arrays.asList((SetValue<?>)
			maD(mediasRedirectFrom,   from.getRedirectFrom  (), to.getRedirectFrom  ()),
			maD(mediasException,      from.getException     (), to.getException     ()),
			maD(mediasInvalidSpecial, from.getInvalidSpecial(), to.getInvalidSpecial()),
			maD(mediasGuessedUrl,     from.getGuessedUrl    (), to.getGuessedUrl    ()),
			maD(mediasNotAnItem,      from.getNotAnItem     (), to.getNotAnItem     ()),
			maD(mediasNoSuchItem,     from.getNoSuchItem    (), to.getNoSuchItem    ()),
			maD(mediasMoved,          from.getMoved         (), to.getMoved         ()),
			maD(mediasIsNull,         from.getIsNull        (), to.getIsNull        ()),
			maD(mediasNotComputable,  from.getNotComputable (), to.getNotComputable ()),
			maD(mediasNotModified,    from.getNotModified   (), to.getNotModified   ()),
			maD(mediasDelivered,      from.getDelivered     (), to.getDelivered     ()));
	}


	private static final CompositeField<SamplerClusterSender> clusterSender = CompositeField.create(SamplerClusterSender.class).toFinal().optional();

	static SetValue<?> mapIt(
			final ClusterSenderInfo from,
			final ClusterSenderInfo to)
	{
		return map(clusterSender,
			(from!=null&&to!=null) ? new SamplerClusterSender(from, to) : null
		);
	}


	private static final CompositeField<SamplerClusterListener> clusterListener = CompositeField.create(SamplerClusterListener.class).toFinal().optional();

	static SetValue<?> mapIt(
			final ClusterListenerInfo from,
			final ClusterListenerInfo to)
	{
		return map(clusterListener,
			(from!=null&&to!=null) ? new SamplerClusterListener(from, to) : null
		);
	}


	@SuppressWarnings("unused") private SamplerModel(final ActivationParameters ap){ super(ap); }
	private static final long serialVersionUID = 1l;
	static final Type<SamplerModel> TYPE = TypesBound.newType(SamplerModel.class);
}

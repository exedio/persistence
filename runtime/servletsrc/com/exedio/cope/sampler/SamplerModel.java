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
import static com.exedio.cope.sampler.Util.field;
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
import com.exedio.cope.ItemCacheStatistics;
import com.exedio.cope.LongField;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
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

	private static final IntegerField connectionPoolIdle         = field(0);
	private static final IntegerField connectionPoolGet          = field(0);
	private static final IntegerField connectionPoolPut          = field(0);
	private static final IntegerField connectionPoolInvalidOnGet = field(0);
	private static final IntegerField connectionPoolInvalidOnPut = field(0);

	static List<SetValue<?>> mapIt(
			final Pool.Info from,
			final Pool.Info to)
	{
		return Arrays.asList(
			map(connectionPoolIdle, to.getIdleLevel()),
			maD(connectionPoolGet, from.getCounter().getGetCounter(), to.getCounter().getGetCounter()),
			maD(connectionPoolPut, from.getCounter().getPutCounter(), to.getCounter().getPutCounter()),
			maD(connectionPoolInvalidOnGet, from.getInvalidOnGet(), to.getInvalidOnGet()),
			maD(connectionPoolInvalidOnPut, from.getInvalidOnPut(), to.getInvalidOnPut()));
	}


	static final IntegerField nextTransactionId = field(0);

	@CopeSchemaName("commitOutConnection")
	private static final IntegerField commitWithoutConnection   = field(0);
	private static final IntegerField commitWithConnection      = field(0);
	@CopeSchemaName("rollbackOutConnection")
	private static final IntegerField rollbackWithoutConnection = field(0);
	private static final IntegerField rollbackWithConnection    = field(0);

	static List<SetValue<?>> mapIt(
			final TransactionCounters from,
			final TransactionCounters to)
	{
		return Arrays.asList(
			maD(commitWithoutConnection,   from.getCommitWithoutConnection  (), to.getCommitWithoutConnection  ()),
			maD(commitWithConnection,      from.getCommitWithConnection     (), to.getCommitWithConnection     ()),
			maD(rollbackWithoutConnection, from.getRollbackWithoutConnection(), to.getRollbackWithoutConnection()),
			maD(rollbackWithConnection,    from.getRollbackWithConnection   (), to.getRollbackWithConnection   ()));
	}


	private static final IntegerField itemCacheLimit                = field(0);
	private static final IntegerField itemCacheLevel                = field(0);
	private static final IntegerField itemCacheHits                 = field(0);
	private static final IntegerField itemCacheMisses               = field(0);
	private static final IntegerField itemCacheConcurrentLoads      = field(0);
	private static final IntegerField itemCacheReplacements         = field(0);
	private static final IntegerField itemCacheInvalidationsOrdered = field(0);
	private static final IntegerField itemCacheInvalidationsDone    = field(0);
	private static final IntegerField itemCacheStampsSize   = field(0);
	private static final IntegerField itemCacheStampsHits   = field(0);
	private static final IntegerField itemCacheStampsPurged = field(0);

	static List<SetValue<?>> mapIt(
			final ItemCacheStatistics from,
			final ItemCacheStatistics to)
	{
		return Arrays.asList(
			map(itemCacheLimit, to.getLimit()),
			map(itemCacheLevel, to.getLevel()),

			maD(itemCacheHits,   from.getSummarizedHits  (), to.getSummarizedHits  ()),
			maD(itemCacheMisses, from.getSummarizedMisses(), to.getSummarizedMisses()),

			maD(itemCacheConcurrentLoads, from.getSummarizedConcurrentLoads(), to.getSummarizedConcurrentLoads()),
			maD(itemCacheReplacements,    from.getSummarizedReplacements   (), to.getSummarizedReplacements   ()),

			maD(itemCacheInvalidationsOrdered, from.getSummarizedInvalidationsOrdered(), to.getSummarizedInvalidationsOrdered()),
			maD(itemCacheInvalidationsDone,    from.getSummarizedInvalidationsDone   (), to.getSummarizedInvalidationsDone   ()),

			map(itemCacheStampsSize,                                     to.getSummarizedStampsSize  ()),
			maD(itemCacheStampsHits,   from.getSummarizedStampsHits  (), to.getSummarizedStampsHits  ()),
			maD(itemCacheStampsPurged, from.getSummarizedStampsPurged(), to.getSummarizedStampsPurged()));
	}


	private static final IntegerField queryCacheHits            = field(0);
	private static final IntegerField queryCacheMisses          = field(0);
	private static final IntegerField queryCacheReplacements    = field(0);
	private static final IntegerField queryCacheInvalidations   = field(0);
	private static final IntegerField queryCacheConcurrentLoads = field(0);
	private static final IntegerField queryCacheStampsSize      = field(0);
	private static final IntegerField queryCacheStampsHits      = field(0);
	private static final IntegerField queryCacheStampsPurged    = field(0);

	static List<SetValue<?>> mapIt(
			final QueryCacheInfo from,
			final QueryCacheInfo to)
	{
		return Arrays.asList(
			maD(queryCacheHits,            from.getHits           (), to.getHits           ()),
			maD(queryCacheMisses,          from.getMisses         (), to.getMisses         ()),
			maD(queryCacheReplacements,    from.getReplacements   (), to.getReplacements   ()),
			maD(queryCacheInvalidations,   from.getInvalidations  (), to.getInvalidations  ()),
			maD(queryCacheConcurrentLoads, from.getConcurrentLoads(), to.getConcurrentLoads()),
			map(queryCacheStampsSize,                                 to.getStampsSize     ()),
			maD(queryCacheStampsHits,      from.getStampsHits     (), to.getStampsHits     ()),
			maD(queryCacheStampsPurged,    from.getStampsPurged   (), to.getStampsPurged   ()));
	}


	private static final IntegerField changeListenerSize     = field(0);
	private static final IntegerField changeListenerCleared  = field(0);
	private static final IntegerField changeListenerRemoved  = field(0);
	private static final IntegerField changeListenerFailed   = field(0);

	static List<SetValue<?>> mapIt(
			final ChangeListenerInfo from,
			final ChangeListenerInfo to)
	{
		return Arrays.asList(
			map(changeListenerSize,                       to.getSize   ()),
			maD(changeListenerCleared, from.getCleared(), to.getCleared()),
			maD(changeListenerRemoved, from.getRemoved(), to.getRemoved()),
			maD(changeListenerFailed,  from.getFailed (), to.getFailed ()));
	}


	private static final IntegerField changeListenerOverflow  = field(0);
	private static final IntegerField changeListenerException = field(0);
	private static final IntegerField changeListenerPending   = field(0);

	static List<SetValue<?>> mapIt(
			final ChangeListenerDispatcherInfo from,
			final ChangeListenerDispatcherInfo to)
	{
		return Arrays.asList(
			maD(changeListenerOverflow,  from.getOverflow (), to.getOverflow ()),
			maD(changeListenerException, from.getException(), to.getException()),
			map(changeListenerPending,                        to.getPending  ()));
	}


	static final IntegerField mediasNoSuchPath = field(0);

	private static final IntegerField mediasRedirectFrom   = field(0);
	private static final IntegerField mediasException      = field(0);
	private static final IntegerField mediasInvalidSpecial = field(0);
	private static final IntegerField mediasGuessedUrl     = field(0);
	private static final IntegerField mediasNotAnItem      = field(0);
	private static final IntegerField mediasNoSuchItem     = field(0);
	private static final IntegerField mediasMoved          = field(0);
	private static final IntegerField mediasIsNull         = field(0);
	private static final IntegerField mediasNotComputable  = field(0);
	private static final IntegerField mediasNotModified    = field(0);
	private static final IntegerField mediasDelivered      = field(0);

	static List<SetValue<?>> mapIt(
			final MediaSummary from,
			final MediaSummary to)
	{
		return Arrays.asList(
			maD(mediasRedirectFrom,   from.getRedirectFrom  (), to.getRedirectFrom  ()),
			maD(mediasException,      from.getException     (), to.getException     ()),
			maD(mediasInvalidSpecial, from.getInvalidSpecial(), to.getInvalidSpecial()),
			maD(mediasGuessedUrl,     from.getGuessedUrl    (), to.getGuessedUrl    ()),
			maD(mediasNotAnItem,      from.getNotAnItem     (), to.getNotAnItem     ()),
			maD(mediasNoSuchItem,     from.getNoSuchItem    (), to.getNoSuchItem    ()),
			maD(mediasMoved,          from.getMoved         (), to.getMoved         ()),
			maD(mediasIsNull,         from.getIsNull        (), to.getIsNull        ()),
			mediasNotComputable.map(0),
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

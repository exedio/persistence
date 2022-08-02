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
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
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

	static List<SetValue<?>> mapItemCacheStatisticsDummy()
	{
		return Arrays.asList(
			map(itemCacheLimit, DUMMY),
			map(itemCacheLevel, DUMMY),

			map(itemCacheHits,   DUMMY),
			map(itemCacheMisses, DUMMY),

			map(itemCacheConcurrentLoads, DUMMY),
			map(itemCacheReplacements,    DUMMY),

			map(itemCacheInvalidationsOrdered, DUMMY),
			map(itemCacheInvalidationsDone,    DUMMY),

			map(itemCacheStampsSize,   DUMMY),
			map(itemCacheStampsHits,   DUMMY),
			map(itemCacheStampsPurged, DUMMY));
	}


	private static final IntegerField queryCacheHits            = field(0);
	private static final IntegerField queryCacheMisses          = field(0);
	private static final IntegerField queryCacheReplacements    = field(0);
	private static final IntegerField queryCacheInvalidations   = field(0);
	private static final IntegerField queryCacheConcurrentLoads = field(0);
	private static final IntegerField queryCacheStampsSize      = field(0);
	private static final IntegerField queryCacheStampsHits      = field(0);
	private static final IntegerField queryCacheStampsPurged    = field(0);

	static List<SetValue<?>> mapQueryCacheInfoDummy()
	{
		return Arrays.asList(
			map(queryCacheHits,            DUMMY),
			map(queryCacheMisses,          DUMMY),
			map(queryCacheReplacements,    DUMMY),
			map(queryCacheInvalidations,   DUMMY),
			map(queryCacheConcurrentLoads, DUMMY),
			map(queryCacheStampsSize,      DUMMY),
			map(queryCacheStampsHits,      DUMMY),
			map(queryCacheStampsPurged,    DUMMY));
	}


	private static final IntegerField changeListenerSize     = field(0);
	private static final IntegerField changeListenerCleared  = field(0);
	private static final IntegerField changeListenerRemoved  = field(0);
	private static final IntegerField changeListenerFailed   = field(0);

	static List<SetValue<?>> mapChangeListenerInfoDummy()
	{
		return Arrays.asList(
			map(changeListenerSize,    DUMMY),
			map(changeListenerCleared, DUMMY),
			map(changeListenerRemoved, DUMMY),
			map(changeListenerFailed,  DUMMY));
	}


	private static final IntegerField changeListenerOverflow  = field(0);
	private static final IntegerField changeListenerException = field(0);
	private static final IntegerField changeListenerPending   = field(0);

	static List<SetValue<?>> mapChangeListenerDispatcherInfoDummy()
	{
		return Arrays.asList(
			map(changeListenerOverflow,  DUMMY),
			map(changeListenerException, DUMMY),
			map(changeListenerPending,   DUMMY));
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

	static List<SetValue<?>> mapMediaSummaryDummy()
	{
		return Arrays.asList(
			map(mediasRedirectFrom,   DUMMY),
			map(mediasException,      DUMMY),
			map(mediasInvalidSpecial, DUMMY),
			map(mediasGuessedUrl,     DUMMY),
			map(mediasNotAnItem,      DUMMY),
			map(mediasNoSuchItem,     DUMMY),
			map(mediasMoved,          DUMMY),
			map(mediasIsNull,         DUMMY),
			map(mediasNotComputable,  DUMMY),
			map(mediasNotModified,    DUMMY),
			map(mediasDelivered,      DUMMY));
	}


	private static final CompositeField<SamplerClusterSender> clusterSender = CompositeField.create(SamplerClusterSender.class).toFinal().optional();

	static SetValue<?> mapClusterSenderInfoDummy()
	{
		return map(clusterSender,
			null
		);
	}


	private static final CompositeField<SamplerClusterListener> clusterListener = CompositeField.create(SamplerClusterListener.class).toFinal().optional();

	static SetValue<?> mapClusterListenerInfoDummy()
	{
		return map(clusterListener,
			null
		);
	}


	static final Integer DUMMY = 42;

	@SuppressWarnings("unused") private SamplerModel(final ActivationParameters ap){ super(ap); }
	private static final long serialVersionUID = 1l;
	static final Type<SamplerModel> TYPE = TypesBound.newType(SamplerModel.class);
}

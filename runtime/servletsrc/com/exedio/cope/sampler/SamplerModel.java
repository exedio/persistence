/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

final class SamplerModel extends Item
{
	static final DateField date = new DateField().toFinal().unique();
	@NoConsolidate
	static final LongField duration = new LongField().toFinal();
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();
	@CopeSchemaName("thread") static final IntegerField sampler = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);

	@NoConsolidate
	private static final IntegerField connectionPoolIdle = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolPut = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnPut = new IntegerField().toFinal().min(0);

	static List<SetValue> map(final Pool.Info info)
	{
		return Arrays.asList((SetValue)
			connectionPoolIdle.map(info.getIdleLevel()),
			connectionPoolGet.map(info.getCounter().getGetCounter()),
			connectionPoolPut.map(info.getCounter().getPutCounter()),
			connectionPoolInvalidOnGet.map(info.getInvalidOnGet()),
			connectionPoolInvalidOnPut.map(info.getInvalidOnPut()));
	}


	static final LongField nextTransactionId = new LongField().toFinal().min(0);

	@CopeSchemaName("commitOutConnection")
	private static final LongField commitWithoutConnection = new LongField().toFinal().min(0);
	private static final LongField commitWithConnection    = new LongField().toFinal().min(0);
	@CopeSchemaName("rollbackOutConnection")
	private static final LongField rollbackWithoutConnection = new LongField().toFinal().min(0);
	private static final LongField rollbackWithConnection    = new LongField().toFinal().min(0);

	static List<SetValue> map(final TransactionCounters info)
	{
		return Arrays.asList((SetValue)
			commitWithoutConnection  .map(info.getCommitWithoutConnection()),
			commitWithConnection     .map(info.getCommitWithConnection()),
			rollbackWithoutConnection.map(info.getRollbackWithoutConnection()),
			rollbackWithConnection   .map(info.getRollbackWithConnection()));
	}


	private static final LongField itemCacheHits = new LongField().toFinal().min(0);
	private static final LongField itemCacheMisses = new LongField().toFinal().min(0);
	private static final LongField itemCacheConcurrentLoads = new LongField().toFinal().min(0);
	private static final IntegerField itemCacheReplacementRuns = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheReplacements = new IntegerField().toFinal().min(0);
	private static final LongField itemCacheInvalidationsOrdered = new LongField().toFinal().min(0);
	private static final LongField itemCacheInvalidationsDone = new LongField().toFinal().min(0);

	@NoConsolidate
	private static final IntegerField itemCacheInvalidateLastSize = new IntegerField().toFinal().min(0);
	private static final LongField itemCacheInvalidateLastHits = new LongField().toFinal().min(0);
	private static final LongField itemCacheInvalidateLastPurged = new LongField().toFinal().min(0);

	static List<SetValue> map(final ItemCacheSummary info)
	{
		return Arrays.asList((SetValue)
			itemCacheHits  .map(info.getHits()),
			itemCacheMisses.map(info.getMisses()),

			itemCacheConcurrentLoads.map(info.getConcurrentLoads()),
			itemCacheReplacementRuns.map(info.getReplacementRuns()),
			itemCacheReplacements   .map(info.getReplacements()),

			itemCacheInvalidationsOrdered.map(info.getInvalidationsOrdered()),
			itemCacheInvalidationsDone   .map(info.getInvalidationsDone()),

			itemCacheInvalidateLastSize  .map(info.getInvalidateLastSize()),
			itemCacheInvalidateLastHits  .map(info.getInvalidateLastHits()),
			itemCacheInvalidateLastPurged.map(info.getInvalidateLastPurged()));
	}


	private static final LongField queryCacheHits = new LongField().toFinal().min(0);
	private static final LongField queryCacheMisses = new LongField().toFinal().min(0);
	private static final LongField queryCacheReplacements = new LongField().toFinal().min(0);
	private static final LongField queryCacheInvalidations = new LongField().toFinal().min(0);

	static List<SetValue> map(final QueryCacheInfo info)
	{
		return Arrays.asList((SetValue)
			queryCacheHits         .map(info.getHits()),
			queryCacheMisses       .map(info.getMisses()),
			queryCacheReplacements .map(info.getReplacements()),
			queryCacheInvalidations.map(info.getInvalidations()));
	}


	private static final IntegerField changeListenerCleared  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerRemoved  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerFailed   = new IntegerField().toFinal().min(0);

	static List<SetValue> map(final ChangeListenerInfo info)
	{
		return Arrays.asList((SetValue)
			changeListenerCleared.map(info.getCleared()),
			changeListenerRemoved.map(info.getRemoved()),
			changeListenerFailed .map(info.getFailed()));
	}


	private static final    LongField changeListenerOverflow  = new LongField   ().toFinal().min(0);
	private static final    LongField changeListenerException = new LongField   ().toFinal().min(0);
	@NoConsolidate
	private static final IntegerField changeListenerPending   = new IntegerField().toFinal().min(0);

	static List<SetValue> map(final ChangeListenerDispatcherInfo info)
	{
		return Arrays.asList((SetValue)
			changeListenerOverflow .map(info.getOverflow ()),
			changeListenerException.map(info.getException()),
			changeListenerPending  .map(info.getPending  ()));
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

	static List<SetValue> map(final MediaSummary info)
	{
		return Arrays.asList((SetValue)
			mediasRedirectFrom .map(info.getRedirectFrom()),
			mediasException    .map(info.getException()),
			mediasGuessedUrl   .map(info.getGuessedUrl()),
			mediasNotAnItem    .map(info.getNotAnItem()),
			mediasNoSuchItem   .map(info.getNoSuchItem()),
			mediasMoved        .map(info.getMoved()),
			mediasIsNull       .map(info.getIsNull()),
			mediasNotComputable.map(info.getNotComputable()),
			mediasNotModified  .map(info.getNotModified()),
			mediasDelivered    .map(info.getDelivered()));
	}


	private static final LongField clusterSenderInvalidationSplit = new LongField().toFinal().min(0);

	static List<SetValue> map(final ClusterSenderInfo info)
	{
		return Arrays.asList((SetValue)
			clusterSenderInvalidationSplit.map(info!=null ? info.getInvalidationSplit() : 0));
	}


	private static final CompositeField<SamplerClusterListener> clusterListener = CompositeField.create(SamplerClusterListener.class).toFinal().optional();

	static SetValue map(final ClusterListenerInfo info)
	{
		return clusterListener.map(
			info!=null ? new SamplerClusterListener(info) : null
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

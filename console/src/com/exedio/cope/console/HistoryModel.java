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

package com.exedio.cope.console;

import java.util.Arrays;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.info.ClusterListenerInfo;
import com.exedio.cope.info.ClusterSenderInfo;
import com.exedio.cope.info.QueryCacheInfo;
import com.exedio.cope.util.Pool;

final class HistoryModel extends Item
{
	static final DateField date = new DateField().toFinal().unique();
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();
	static final IntegerField thread = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);
	
	private static final IntegerField connectionPoolIdle = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolPut = new IntegerField().toFinal().min(0);
	@CopeSchemaName("connectionPoolInvalidFromIdle") private static final IntegerField connectionPoolInvalidOnGet = new IntegerField().toFinal().min(0);
	@CopeSchemaName("connectionPoolInvalidIntoIdle") private static final IntegerField connectionPoolInvalidOnPut = new IntegerField().toFinal().min(0);
	
	static List<SetValue> map(final Pool.Info info)
	{
		return Arrays.asList((SetValue)
			connectionPoolIdle.map(info.getIdleLevel()),
			connectionPoolGet.map(info.getCounter().getGetCounter()),
			connectionPoolPut.map(info.getCounter().getPutCounter()),
			connectionPoolInvalidOnGet.map(info.getInvalidOnGet()),
			connectionPoolInvalidOnPut.map(info.getInvalidOnPut()));
	}
	
	
	static final LongField nextTransactionId = new LongField().toFinal();
	
	@CopeSchemaName("commitOutConnection")
	private static final LongField commitWithoutConnection = new LongField().toFinal();
	private static final LongField commitWithConnection = new LongField().toFinal();
	@CopeSchemaName("rollbackOutConnection")
	private static final LongField rollbackWithoutConnection = new LongField().toFinal();
	private static final LongField rollbackWithConnection = new LongField().toFinal();
	
	static List<SetValue> map(final TransactionCounters info)
	{
		return Arrays.asList((SetValue)
			commitWithoutConnection  .map(info.getCommitWithoutConnection()),
			commitWithConnection     .map(info.getCommitWithConnection()),
			rollbackWithoutConnection.map(info.getRollbackWithoutConnection()),
			rollbackWithConnection   .map(info.getRollbackWithConnection()));
	}
	
	
	static final LongField itemCacheHits = new LongField().toFinal();
	static final LongField itemCacheMisses = new LongField().toFinal();
	static final LongField itemCacheConcurrentLoads = new LongField().toFinal();
	@CopeSchemaName("itemCacheNumberOfCleanups") static final IntegerField itemCacheReplacementRuns = new IntegerField().toFinal().min(0);
	@CopeSchemaName("itemCacheItemsCleanedUp") static final IntegerField itemCacheReplacements = new IntegerField().toFinal().min(0);
	
	private static final LongField queryCacheHits = new LongField().toFinal();
	private static final LongField queryCacheMisses = new LongField().toFinal();
	private static final LongField queryCacheReplacements = new LongField().toFinal();
	private static final LongField queryCacheInvalidations = new LongField().toFinal();
	
	static List<SetValue> map(final QueryCacheInfo info)
	{
		return Arrays.asList((SetValue)
			queryCacheHits         .map(info.getHits()),
			queryCacheMisses       .map(info.getMisses()),
			queryCacheReplacements .map(info.getReplacements()),
			queryCacheInvalidations.map(info.getInvalidations()));
	}
	
	
	static final IntegerField mediasNoSuchPath = new IntegerField().toFinal().min(0);
	static final IntegerField mediasException = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNotAnItem = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNoSuchItem = new IntegerField().toFinal().min(0);
	static final IntegerField mediasIsNull = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNotComputable = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNotModified = new IntegerField().toFinal().min(0);
	static final IntegerField mediasDelivered = new IntegerField().toFinal().min(0);
	
	private static final LongField clusterSenderInvalidationSplit = new LongField().toFinal();
	
	static List<SetValue> map(final ClusterSenderInfo info)
	{
		return Arrays.asList((SetValue)
			clusterSenderInvalidationSplit.map(info!=null ? info.getInvalidationSplit() : 0));
	}
	
	
	private static final LongField clusterListenerException    = new LongField().toFinal();
	private static final LongField clusterListenerMissingMagic = new LongField().toFinal();
	private static final LongField clusterListenerWrongSecret  = new LongField().toFinal();
	private static final LongField clusterListenerFromMyself   = new LongField().toFinal();
	
	static List<SetValue> map(final ClusterListenerInfo info)
	{
		return Arrays.asList((SetValue)
			clusterListenerException   .map(info!=null ? info.getException()    : 0),
			clusterListenerMissingMagic.map(info!=null ? info.getMissingMagic() : 0),
			clusterListenerWrongSecret .map(info!=null ? info.getWrongSecret()  : 0),
			clusterListenerFromMyself  .map(info!=null ? info.getFromMyself()   : 0));
	}
	
	
	HistoryModel(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused")
	private HistoryModel(final ActivationParameters ap)
	{
		super(ap);
	}
	
	private static final long serialVersionUID = 1l;
	
	static final Type<HistoryModel> TYPE = TypesBound.newType(HistoryModel.class);
}

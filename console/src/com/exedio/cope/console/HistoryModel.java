/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class HistoryModel extends Item
{
	static final DateField date = new DateField().toFinal().unique();
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();
	static final IntegerField thread = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);
	
	static final IntegerField connectionPoolIdle = new IntegerField().toFinal().min(0);
	static final IntegerField connectionPoolGet = new IntegerField().toFinal().min(0);
	static final IntegerField connectionPoolPut = new IntegerField().toFinal().min(0);
	static final IntegerField connectionPoolInvalidFromIdle = new IntegerField().toFinal().min(0);
	static final IntegerField connectionPoolInvalidIntoIdle = new IntegerField().toFinal().min(0);
	
	static final LongField nextTransactionId = new LongField().toFinal();
	@CopeSchemaName("commitOutConnection")
	static final LongField commitWithoutConnection = new LongField().toFinal();
	static final LongField commitWithConnection = new LongField().toFinal();
	@CopeSchemaName("rollbackOutConnection")
	static final LongField rollbackWithoutConnection = new LongField().toFinal();
	static final LongField rollbackWithConnection = new LongField().toFinal();
	
	static final LongField itemCacheHits = new LongField().toFinal();
	static final LongField itemCacheMisses = new LongField().toFinal();
	static final IntegerField itemCacheNumberOfCleanups = new IntegerField().toFinal().min(0);
	static final IntegerField itemCacheItemsCleanedUp = new IntegerField().toFinal().min(0);
	
	static final LongField queryCacheHits = new LongField().toFinal();
	static final LongField queryCacheMisses = new LongField().toFinal();
	static final LongField queryCacheReplacements = new LongField().toFinal();
	
	static final IntegerField mediasNoSuchPath = new IntegerField().toFinal().min(0);
	static final IntegerField mediasException = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNotAnItem = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNoSuchItem = new IntegerField().toFinal().min(0);
	static final IntegerField mediasIsNull = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNotComputable = new IntegerField().toFinal().min(0);
	static final IntegerField mediasNotModified = new IntegerField().toFinal().min(0);
	static final IntegerField mediasDelivered = new IntegerField().toFinal().min(0);
	
	HistoryModel(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused")
	private HistoryModel(final ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}
	
	private static final long serialVersionUID = 1l;
	
	static final Type<HistoryModel> TYPE = newType(HistoryModel.class);
}

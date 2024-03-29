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

package com.exedio.cope.misc;

import static com.exedio.cope.util.Check.requireGreaterZero;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Model;
import com.exedio.cope.Transaction;

public final class TransactionSlicer
{
	private final Model model;
	private final int bitesPerSlice;
	private Transaction transaction;
	private final String transactionName;

	private int bitsLeft;
	private int sliceCount = 0;

	public TransactionSlicer(final Model model, final int bitesPerSlice)
	{
		this.model = requireNonNull(model, "model");
		this.bitesPerSlice = requireGreaterZero(bitesPerSlice, "bitesPerSlice");
		this.transaction = model.currentTransaction();
		this.transactionName = transaction.getName();
		this.bitsLeft = bitesPerSlice;
	}

	public boolean commitAndStartPossibly()
	{
		if((--bitsLeft)>0)
			return false;

		if(transaction!=model.currentTransaction())
			throw new IllegalStateException("inconsistent transaction, expected " + transaction + ", but was " + model.currentTransaction());

		model.commit();
		bitsLeft = bitesPerSlice;
		sliceCount++;
		final boolean isQueryCacheDisabled =
				transaction.isQueryCacheDisabled();
		transaction = model.startTransaction(
				transactionName!=null
				? (transactionName+" slice"+sliceCount)
				: ("slice"+sliceCount));
		if(isQueryCacheDisabled)
			transaction.setQueryCacheDisabled(true);
		return true;
	}

	public int getSliceCount()
	{
		return sliceCount;
	}
}

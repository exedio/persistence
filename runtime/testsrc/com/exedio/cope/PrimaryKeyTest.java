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

package com.exedio.cope;

import static com.exedio.cope.PrimaryKeyItem.TYPE;
import static com.exedio.cope.PrimaryKeyItem.next;

public class PrimaryKeyTest extends AbstractRuntimeTest
{
	/**
	 * Do not use this model in any other test.
	 * Otherwise problems may be hidden, because
	 * model has been connected before.
	 */
	private static final Model MODEL = new Model(TYPE);

	public PrimaryKeyTest()
	{
		super(MODEL);
		skipTransactionManagement();
	}

	private static final PrimaryKeyItem newPrimaryKeyItem(
			final String field,
			final int next)
	{
		try
		{
			MODEL.startTransaction();
			final PrimaryKeyItem result = new PrimaryKeyItem(field, next);
			MODEL.commit();
			return result;
		}
		finally
		{
			MODEL.rollbackIfNotCommitted();
		}
	}

	private static final PrimaryKeyItem newPrimaryKeyItem(
			final String field)
	{
		try
		{
			MODEL.startTransaction();
			final PrimaryKeyItem result = new PrimaryKeyItem(field);
			MODEL.commit();
			return result;
		}
		finally
		{
			MODEL.rollbackIfNotCommitted();
		}
	}

	@Override
	protected void restartTransaction()
	{
		// TODO remove
	}

	public void testMultipleTransactions()
	{
		final boolean c = model.getConnectProperties().cluster.booleanValue();

		assertInfo(model.getSequenceInfo(), TYPE.getThis(), next);

		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfo());

		deleteOnTearDown(newPrimaryKeyItem("first", 5));
		restartTransaction();
		assertInfo(TYPE, 1, c?(hsqldb?0:0):0, c?(hsqldb?0:0):0, TYPE.getPrimaryKeyInfo(), 0);
		assertInfo(next, next.getDefaultToNextInfo(), (c?((hsqldb||mysql)?6:5):0));

		deleteOnTearDown(newPrimaryKeyItem("second"));
		restartTransaction();
		assertInfo(TYPE, 2, c?(hsqldb?0:0):0, c?(hsqldb?1:1):1, TYPE.getPrimaryKeyInfo(), 0);
		assertInfo(next, 1, c?(hsqldb?0:0):6, c?(hsqldb?0:0):6, next.getDefaultToNextInfo(), (c&&!oracle)?5:0);

		deleteOnTearDown(newPrimaryKeyItem("third"));
		restartTransaction();
		assertInfo(TYPE, 3, c?(hsqldb?0:0):0, c?(hsqldb?2:2):2, TYPE.getPrimaryKeyInfo(), 0);
		assertInfo(next, 2, c?(hsqldb?0:0):6, c?(hsqldb?1:1):7, next.getDefaultToNextInfo(), (c&&!oracle)?4:0);
	}
}

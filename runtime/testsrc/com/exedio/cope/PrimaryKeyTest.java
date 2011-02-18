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
	}

	public void testSingleTransaction()
	{
		final boolean c = model.getConnectProperties().cluster.booleanValue();
		final boolean cm = (!hsqldb)||c;
		final boolean cx = hsqldb&&c;

		assertInfo(model.getSequenceInfo(), TYPE.getThis(), next);

		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfo());

		deleteOnTearDown(new PrimaryKeyItem("first", 5));
		if(hsqldb) // TODO --------------------------------------
			return;
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfo(), cx?6:0);


		deleteOnTearDown(new PrimaryKeyItem("second"));
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo());
		assertInfo(next, 1, cm?0:6, cm?0:6, next.getDefaultToNextInfo(), cx?5:0);

		deleteOnTearDown(new PrimaryKeyItem("third"));
		assertInfo(TYPE, 3, 0, 2, TYPE.getPrimaryKeyInfo());
		assertInfo(next, 2, cm?0:6, cm?1:7, next.getDefaultToNextInfo(), cx?4:0);
	}

	public void testMultipleTransactions()
	{
		final boolean c = model.getConnectProperties().cluster.booleanValue();

		assertInfo(model.getSequenceInfo(), TYPE.getThis(), next);

		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfo());

		deleteOnTearDown(new PrimaryKeyItem("first", 5));
		restartTransaction();
		assertInfo(TYPE, 1, c?(hsqldb?1:3):0, c?(hsqldb?1:3):0, TYPE.getPrimaryKeyInfo(), (c&&oracle)?1:0);
		assertInfo(next, next.getDefaultToNextInfo(), c?4:0);

		deleteOnTearDown(new PrimaryKeyItem("second"));
		restartTransaction();
		assertInfo(TYPE, 2, c?(hsqldb?1:3):0, c?(hsqldb?2:4):1, TYPE.getPrimaryKeyInfo(), (c&&oracle)?1:0);
		assertInfo(next, 1, c?(hsqldb?0:2):6, c?(hsqldb?0:2):6, next.getDefaultToNextInfo(), c?3:0);

		deleteOnTearDown(new PrimaryKeyItem("third"));
		restartTransaction();
		assertInfo(TYPE, 3, c?(hsqldb?1:3):0, c?(hsqldb?3:5):2, TYPE.getPrimaryKeyInfo(), (c&&oracle)?1:0);
		assertInfo(next, 2, c?(hsqldb?0:2):6, c?(hsqldb?1:3):7, next.getDefaultToNextInfo(), c?2:0);
	}
}

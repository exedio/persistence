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

package com.exedio.cope.junit;

import static com.exedio.cope.junit.JUnitTestItem.sequence;

import java.util.Collection;
import java.util.List;

import com.exedio.cope.ChangeEvent;
import com.exedio.cope.ChangeListener;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.misc.DatabaseListener;
import com.exedio.cope.util.ModificationListener;

public class CopeModelTestTest extends CopeModelTest
{
	private static final Model MODEL = new Model(JUnitTestItem.TYPE);

	public CopeModelTestTest()
	{
		super(MODEL);
	}

	public void testNotEmpty()
	{
		assertBlank();

		final JUnitTestItem i1 = new JUnitTestItem(100);
		final JUnitTestItem i2 = new JUnitTestItem(101);
		final JUnitTestItem i3 = new JUnitTestItem(102);
		final JUnitTestItem i4 = new JUnitTestItem(103);

		assertEquals("JUnitTestItem-0", i1.getCopeID());
		assertEquals("JUnitTestItem-1", i2.getCopeID());
		assertEquals("JUnitTestItem-2", i3.getCopeID());
		assertEquals("JUnitTestItem-3", i4.getCopeID());

		assertEquals(1000, i1.getNext());
		assertEquals(1001, i2.getNext());
		assertEquals(1002, i3.getNext());
		assertEquals(1003, i4.getNext());

		assertEquals(2000, sequence.next());
		assertEquals(2001, sequence.next());
		assertEquals(2002, sequence.next());
		assertEquals(2003, sequence.next());
	}

	public void testFlushSequences()
	{
		assertBlank();

		final JUnitTestItem i1 = new JUnitTestItem(200);
		assertEquals("JUnitTestItem-0", i1.getCopeID());
		assertEquals(1000, i1.getNext());
		assertEquals(2000, sequence.next());
	}

	public void testNoTransaction()
	{
		assertBlank();

		model.commit();
	}

	public void testDatabaseListener()
	{
		assertBlank();

		model.setDatabaseListener(new DatabaseListener(){

			public void onStatement(
					final String sql, final List<Object> parameters,
					final long durationPrepare, final long durationExecute, final long durationRead,
					final long durationClose)
			{
				// do nothing
			}
		});
	}

	public void testChangeListener()
	{
		assertBlank();

		model.addChangeListener(new ChangeListener()
		{
			public void onChange(final ChangeEvent event)
			{
				throw new RuntimeException();
			}
		});
	}

	public void testModificationListener()
	{
		assertBlank();

		model.addModificationListener(new ModificationListener()
		{
			@Deprecated
			public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
			{
				throw new RuntimeException();
			}
		});
	}

	public void testLast()
	{
		assertBlank();
	}

	private void assertBlank()
	{
		assertTrue(model.hasCurrentTransaction());
		assertEquals("tx:com.exedio.cope.junit.CopeModelTestTest", model.currentTransaction().getName());
		model.checkEmptySchema();
		assertEquals(null, model.getDatabaseListener());
		assertEquals(list(), model.getChangeListeners());
		assertEquals(list(), model.getModificationListeners());
	}
}

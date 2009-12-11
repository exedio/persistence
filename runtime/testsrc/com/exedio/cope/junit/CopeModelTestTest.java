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

package com.exedio.cope.junit;

import java.util.Collection;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
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
		
		assertEquals("JUnitTestItem.0", new JUnitTestItem(1).getCopeID());
	}
	
	public void testFlushSequences()
	{
		assertBlank();
		
		assertEquals("JUnitTestItem.0", new JUnitTestItem(1).getCopeID());
	}
	
	public void testNoTransaction()
	{
		assertBlank();
		
		model.commit();
	}
	
	public void testModificationListener()
	{
		assertBlank();
		
		model.addModificationListener(new ModificationListener()
		{
			public void onModifyingCommit(Collection<Item> modifiedItems, Transaction transaction)
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
		assertEquals("tx:com.exedio.cope.junit.CopeModelTestTest", model.getCurrentTransaction().getName());
		model.checkEmptySchema();
		assertEquals(list(), model.getModificationListeners());
	}
}

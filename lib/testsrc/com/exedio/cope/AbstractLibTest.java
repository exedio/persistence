/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.exedio.cope.junit.CopeTest;
import com.exedio.cope.search.Condition;

public abstract class AbstractLibTest extends CopeTest
{
	
	public AbstractLibTest(final Model model)
	{
		super(model);
	}
	
	public AbstractLibTest(final Model model, final boolean exclusive)
	{
		super(model, exclusive);
	}
	
	protected final static Integer i1 = new Integer(1);
	protected final static Integer i2 = new Integer(2);
	protected final static Integer i3 = new Integer(3);
	protected final static Integer i4 = new Integer(4);
	protected final static Integer i5 = new Integer(5);
	protected final static Integer i6 = new Integer(6);
	protected final static Integer i7 = new Integer(7);
	protected final static Integer i8 = new Integer(8);
	
	protected boolean hsqldb;
	protected boolean mysql;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		hsqldb = "com.exedio.cope.HsqldbDatabase".equals(model.getDatabase().getClass().getName()); 
		mysql  = "com.exedio.cope.MysqlDatabase".equals(model.getDatabase().getClass().getName());
	}
	
	protected void tearDown() throws Exception
	{
		final boolean hadExpectations = model.getDatabase().clearExpectedCalls();
		if ( hadExpectations && testCompletedSuccessfully() )
		{
			fail( "database still had expected calls; missing call to verifyExpectations" );
		}
		super.tearDown();
	}

	final String pkString(final Item item)
	{
		return String.valueOf(item.getCopeType().getPkSource().pk2id(((Item)item).pk));
	}

	protected static final InputStream stream(byte[] data)
	{
		return new ByteArrayInputStream(data);
	}
	
	protected void assertData(final byte[] expectedData, final InputStream actualData)
	{
		try
		{
			final byte[] actualDataArray = new byte[2*expectedData.length];
			final int actualLength = actualData.read(actualDataArray);
			actualData.close();
			assertEquals(expectedData.length, actualLength);
			for(int i = 0; i<actualLength; i++)
				assertEquals(expectedData[i], actualDataArray[i]);
		}
		catch(IOException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	protected static void assertEquals(final Function f1, final Function f2)
	{
		assertEquals((Object)f1, (Object)f2);
		assertEquals((Object)f2, (Object)f1);
		if(f1!=null)
			assertEquals(f1.hashCode(), f2.hashCode());
	}
	
	protected static void assertNotEquals(final Function f1, final Function f2)
	{
		assertTrue(!f1.equals(f2));
		assertTrue(!f2.equals(f1));
		assertTrue(f1.hashCode()!=f2.hashCode());
	}
	
	protected static void assertEquals(final Condition c1, final Condition c2)
	{
		assertEquals((Object)c1, (Object)c2);
		assertEquals((Object)c2, (Object)c1);
		if(c1!=null)
			assertEquals(c1.hashCode(), c2.hashCode());
	}
	
	protected static void assertNotEquals(final Condition c1, final Condition c2)
	{
		assertTrue(!c1.equals(c2));
		assertTrue(!c2.equals(c1));
		assertTrue(c1.hashCode()!=c2.hashCode());
	}
	
	protected void assertDelete(final Item item) throws IntegrityViolationException
	{
		assertTrue(item.existsCopeItem());
		item.deleteCopeItem();
		assertTrue(!item.existsCopeItem());
	}

	void assertDeleteFails(final Item item, final ItemAttribute attribute)
	{
		assertDeleteFails(item, attribute, item);
	}
	
	void assertDeleteFails(final Item item, final ItemAttribute attribute, final Item itemToBeDeleted)
	{
		try
		{
			item.deleteCopeItem();
			fail("should have thrown IntegrityViolationException");
		}
		catch(IntegrityViolationException e)
		{
			assertEquals(attribute, e.getAttribute());
			assertEquals(itemToBeDeleted, e.getItem());
			assertEquals("integrity violation on deletion of " + itemToBeDeleted.getCopeID() + " because of " + e.getAttribute(), e.getMessage());
		}
		assertTrue(item.existsCopeItem());
	}
	
	protected void activate(final Transaction transaction)
	{
		model.leaveTransaction();
		model.joinTransaction( transaction );
	}	
}


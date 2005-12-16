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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

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
	protected boolean cache;
	
	final ArrayList files = new ArrayList();
	
	protected void setUp() throws Exception
	{
		super.setUp();
		Database realDatabase = model.getDatabase();
		if ( realDatabase instanceof WrappingDatabase )
		{
			realDatabase = ((WrappingDatabase)realDatabase).getWrappedDatabase();
		}
		hsqldb = "com.exedio.cope.HsqldbDatabase".equals(realDatabase.getClass().getName()); 
		mysql  = "com.exedio.cope.MysqlDatabase".equals(realDatabase.getClass().getName());
		cache = model.getProperties().getCacheLimit()>0;
		files.clear();
	}
	
	protected void tearDown() throws Exception
	{
		for(Iterator i = files.iterator(); i.hasNext(); )
			((File)i.next()).delete();
		files.clear();
		
		if ( model.getDatabase() instanceof ExpectingDatabase )
		{
			ExpectingDatabase expectingDB = (ExpectingDatabase)model.getDatabase();
			model.replaceDatabase( expectingDB.getWrappedDatabase() );
			if ( testCompletedSuccessfully() )
			{
				fail( "test didn't un-install ExpectingDatabase" );
			}
		}
		super.tearDown();
	}

	final String pkString(final Item item)
	{
		return String.valueOf(item.getCopeType().getPkSource().pk2id(((Item)item).pk));
	}

	protected static final InputStream stream(final byte[] data)
	{
		return new ByteArrayInputStream(data);
	}
	
	protected final File file(final byte[] data)
	{
		final File result;
		FileOutputStream s = null;
		try
		{
			result = File.createTempFile("cope-AbstractLibTest-", ".tmp");
			s = new FileOutputStream(result);
			s.write(data);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(s!=null)
			{
				try
				{
					s.close();
				}
				catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		files.add(result);
		return result;
	}
	
	protected void assertData(final byte[] expectedData, final byte[] actualData)
	{
		assertEquals(expectedData.length, actualData.length);
		for(int i = 0; i<actualData.length; i++)
			assertEquals(expectedData[i], actualData[i]);
	}
	
	protected void assertData(final byte[] expectedData, final InputStream actualData)
	{
		try
		{
			final byte[] actualDataArray = new byte[2*expectedData.length];
			final int actualLengthRead = actualData.read(actualDataArray);
			final int actualLength = actualLengthRead<0 ? 0 : actualLengthRead;
			actualData.close();
			// TODO reuse code from assertData(byte[], byte[])
			assertEquals(expectedData.length, actualLength);
			for(int i = 0; i<actualLength; i++)
				assertEquals(expectedData[i], actualDataArray[i]);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
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
	
	protected static final void assertEqualContent(final byte[] expectedData, final File actualFile) throws IOException
	{
		if(expectedData==null)
			assertFalse(actualFile.exists());
		else
		{
			assertTrue(actualFile.exists());
			assertEquals(expectedData.length, actualFile.length());

			final byte[] actualData = new byte[20];
			FileInputStream in = new FileInputStream(actualFile);
			in.read(actualData);
			in.close();
			
			for(int i = 0; i<expectedData.length; i++)
				assertEquals(expectedData[i], actualData[i]);
			
			assertTrue(actualFile.delete());
		}
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
	
	protected void assertID(final int id, final Item item)
	{
		assertTrue(item.getCopeID()+"/"+id, item.getCopeID().endsWith("."+id));
	}

	protected void activate(final Transaction transaction)
	{
		model.leaveTransaction();
		model.joinTransaction( transaction );
	}
	
	void assertSameCache(final Object o1, final Object o2)
	{
		if(cache)
			assertSame(o1, o2);
		else
			assertNotSame(o1, o2);
	}
	
}


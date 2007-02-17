/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.exedio.cope.junit.CopeTest;

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
	
	protected static final Integer i1 = Integer.valueOf(1);
	protected static final Integer i2 = Integer.valueOf(2);
	protected static final Integer i3 = Integer.valueOf(3);
	protected static final Integer i4 = Integer.valueOf(4);
	protected static final Integer i5 = Integer.valueOf(5);
	protected static final Integer i6 = Integer.valueOf(6);
	protected static final Integer i7 = Integer.valueOf(7);
	protected static final Integer i8 = Integer.valueOf(8);
	protected static final Integer i9 = Integer.valueOf(9);
	
	protected static final byte[] data0  = {};
	protected static final byte[] data4  = {-86,122,-8,23};
	protected static final byte[] data6  = {-97,35,-126,86,19,-8};
	protected static final byte[] data8  = {-54,104,-63,23,19,-45,71,-23};
	protected static final byte[] data10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	protected static final byte[] data11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};
	protected static final byte[] data20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	protected static final byte[] data21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	
	enum Dialect
	{
		HSQLDB("timestamp", false),
		MYSQL(null, true),
		ORACLE("TIMESTAMP(3)", true),
		POSTGRESQL("timestamp (3) without time zone", true);
		
		final String dateTimestampType;
		final boolean supportsReadCommitted;
		
		Dialect(final String dateTimestampType, final boolean supportsReadCommitted)
		{
			this.dateTimestampType = dateTimestampType;
			this.supportsReadCommitted = supportsReadCommitted;
		}
	}
	
	protected Dialect dialect = null;
	protected boolean hsqldb;
	protected boolean mysql;
	protected boolean oracle;
	protected boolean postgresql;
	protected boolean cache;
	protected boolean noJoinParentheses;
	
	private final ArrayList<File> files = new ArrayList<File>();
	private TestByteArrayInputStream testStream;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final String database = model.getProperties().getDialect();
		
		if("com.exedio.cope.HsqldbDialect".equals(database))
			dialect = Dialect.HSQLDB;
		else if("com.exedio.cope.MysqlDialect".equals(database))
			dialect = Dialect.MYSQL;
		else if("com.exedio.cope.OracleDialect".equals(database))
			dialect = Dialect.ORACLE;
		else if("com.exedio.cope.PostgresqlDialect".equals(database))
			dialect = Dialect.POSTGRESQL;
		else
			fail(database);

		
		hsqldb = dialect==Dialect.HSQLDB;
		mysql  = dialect==Dialect.MYSQL;
		oracle = dialect==Dialect.ORACLE;
		postgresql = dialect==Dialect.POSTGRESQL;
		cache = model.getProperties().getItemCacheLimit()>0;
		noJoinParentheses = hsqldb;
		files.clear();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		for(Iterator i = files.iterator(); i.hasNext(); )
			((File)i.next()).delete();
		files.clear();

		final DatabaseListener listener = model.setDatabaseListener(null);
		
		if(testCompletedSuccessfully())
			assertNull("test didn't un-install ExpectingDatabase", listener);
		
		super.tearDown();
	}

	protected final TestByteArrayInputStream stream(final byte[] data)
	{
		assertNull(testStream);
		final TestByteArrayInputStream result = new TestByteArrayInputStream(data);
		testStream = result;
		return result;
	}
	
	protected final void assertStreamClosed()
	{
		assertNotNull(testStream);
		testStream.assertClosed();
		testStream = null;
	}
	
	protected final File file(final byte[] data)
	{
		final File result;
		FileOutputStream s = null;
		try
		{
			result = File.createTempFile("exedio-cope-AbstractLibTest-", ".tmp");
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
	
	protected static void assertData(final byte[] expectedData, final byte[] actualData)
	{
		if(!Arrays.equals(expectedData, actualData))
			fail("expected " + Arrays.toString(expectedData) + ", but was " + Arrays.toString(actualData));
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

			final byte[] actualData = new byte[(int)actualFile.length()];
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

	void assertDeleteFails(final Item item, final ItemField attribute)
	{
		assertDeleteFails(item, attribute, item);
	}
	
	void assertDeleteFails(final Item item, final ItemField attribute, final Item itemToBeDeleted)
	{
		try
		{
			item.deleteCopeItem();
			fail();
		}
		catch(IntegrityViolationException e)
		{
			assertEquals(attribute, e.getFeature());
			assertEquals(attribute, e.getFeature());
			assertEquals(itemToBeDeleted, e.getItem());
			assertEquals("integrity violation on deletion of " + itemToBeDeleted.getCopeID() + " because of " + e.getFeature(), e.getMessage());
		}
		assertTrue(item.existsCopeItem());
	}
	
	protected void assertID(final int id, final Item item)
	{
		assertTrue(item.getCopeID()+"/"+id, item.getCopeID().endsWith("."+id));
	}

	protected void assertIDFails(final String id, final String detail, final boolean notAnID)
	{
		try
		{
			model.findByID(id);
			fail();
		}
		catch(NoSuchIDException e)
		{
			assertEquals("no such id <" + id + ">, " + detail, e.getMessage());
			assertEquals(notAnID, e.notAnID());
		}
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

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.exedio.cope.function.LengthView;
import com.exedio.cope.function.UppercaseView;
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
	protected boolean oracle;
	protected boolean cache;
	
	private final ArrayList files = new ArrayList();
	private TestByteArrayInputStream testStream;
	
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
		oracle  = "com.exedio.cope.OracleDatabase".equals(realDatabase.getClass().getName());
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
	
	protected static Collection search(final FunctionAttribute selectAttribute)
	{
		return search(selectAttribute, null);
	}
	
	protected static Collection search(final FunctionAttribute selectAttribute, final Condition condition)
	{
		return new Query(selectAttribute, condition).search();
	}
	
	// TODO move into StringTest
	static final String makeString(final int length)
	{
		final int segmentLength = length/20 + 1;
		//System.err.println("---------------------"+length+"--start");
		final char[] buf = new char[length];
		//System.err.println("---------------------"+length+"--allocated");
		
		char val = 'A';
		for(int i = 0; i<length; i+=segmentLength)
			Arrays.fill(buf, i, Math.min(i+segmentLength, length), val++);
		
		final String lengthString = String.valueOf(length) + ':';
		System.arraycopy(lengthString.toCharArray(), 0, buf, 0, Math.min(lengthString.length(), length));
		
		//System.err.println("---------------------"+length+"--copied");
		final String result = new String(buf);
		//System.err.println("---------------------"+length+"--stringed");
		//System.err.println("---------------------"+length+"--end--"+result.substring(0, 80));
		return result;
	}

	// TODO move into StringTest
	void assertString(final Item item, final Item item2, final StringAttribute sa) throws ConstraintViolationException
	{
		final Type TYPE = item.getCopeType(); // TODO rename to type
		assertEquals(TYPE, item2.getCopeType());

		final String VALUE = "someString";
		final String VALUE2 = VALUE+"2";
		final String VALUE_UPPER = "SOMESTRING";
		final String VALUE2_UPPER = "SOMESTRING2";
		
		final UppercaseView saup = sa.uppercase();
		final LengthView saln = sa.length();
		
		assertEquals(null, sa.get(item));
		assertEquals(null, saup.get(item));
		assertEquals(null, saln.get(item));
		assertEquals(null, sa.get(item2));
		assertEquals(null, saup.get(item2));
		assertEquals(null, saln.get(item2));
		assertContains(item, item2, TYPE.search(sa.isNull()));
		
		sa.set(item, VALUE);
		assertEquals(VALUE, sa.get(item));
		assertEquals(VALUE_UPPER, saup.get(item));
		assertEquals(new Integer(VALUE.length()), saln.get(item));

		sa.set(item2, VALUE2);
		assertEquals(VALUE2, sa.get(item2));
		assertEquals(VALUE2_UPPER, saup.get(item2));
		assertEquals(new Integer(VALUE2.length()), saln.get(item2));
		
		if(!oracle||sa.getMaximumLength()<4000)
		{
			assertContains(item, TYPE.search(sa.equal(VALUE)));
			assertContains(item2, TYPE.search(sa.notEqual(VALUE)));
			assertContains(TYPE.search(sa.equal(VALUE_UPPER)));
			assertContains(item, TYPE.search(sa.like(VALUE)));
			assertContains(item, item2, TYPE.search(sa.like(VALUE+"%")));
			assertContains(item2, TYPE.search(sa.like(VALUE2+"%")));
	
			assertContains(item, TYPE.search(saup.equal(VALUE_UPPER)));
			assertContains(item2, TYPE.search(saup.notEqual(VALUE_UPPER)));
			assertContains(TYPE.search(saup.equal(VALUE)));
			assertContains(item, TYPE.search(saup.like(VALUE_UPPER)));
			assertContains(item, item2, TYPE.search(saup.like(VALUE_UPPER+"%")));
			assertContains(item2, TYPE.search(saup.like(VALUE2_UPPER+"%")));
			
			assertContains(item, TYPE.search(saln.equal(VALUE.length())));
			assertContains(item2, TYPE.search(saln.notEqual(VALUE.length())));
			assertContains(TYPE.search(saln.equal(VALUE.length()+2)));
	
			assertContains(VALUE, VALUE2, search(sa));
			assertContains(VALUE, search(sa, sa.equal(VALUE)));
			// TODO allow functions for select
			//assertContains(VALUE_UPPER, search(saup, sa.equal(VALUE)));
		}

		restartTransaction();
		assertEquals(VALUE, sa.get(item));
		assertEquals(VALUE_UPPER, saup.get(item));
		assertEquals(new Integer(VALUE.length()), saln.get(item));
		
		{
			final boolean supports = TYPE.getModel().supportsEmptyStrings();
			final String emptyString = supports ? "" : null;
			
			sa.set(item, "");
			assertEquals(emptyString, sa.get(item));
			restartTransaction();
			assertEquals(emptyString, sa.get(item));
			assertEquals(list(item), TYPE.search(sa.equal(emptyString)));
			if(!oracle||sa.getMaximumLength()<4000)
			{
				assertEquals(list(), TYPE.search(sa.equal("x")));
				assertEquals(supports ? list(item) : list(), TYPE.search(sa.equal("")));
			}
		}
		
		assertStringSet(item, sa,
			"Auml \u00c4; "
			+ "Ouml \u00d6; "
			+ "Uuml \u00dc; "
			+ "auml \u00e4; "
			+ "ouml \u00f6; "
			+ "uuml \u00fc; "
			+ "szlig \u00df; "
			+ "paragraph \u00a7; "
			+ "kringel \u00b0; "
			//+ "abreve \u0102; "
			//+ "hebrew \u05d8 "
			+ "euro \u20ac");
		
		// test SQL injection
		// if SQL injection is not prevented properly,
		// the following lines will throw a SQLException
		// due to column "hijackedColumn" not found
		assertStringSet(item, sa, "value',hijackedColumn='otherValue");
		// TODO use streams for oracle
		assertStringSet(item, sa, makeString(Math.min(sa.getMaximumLength(), oracle ? (1300/*32766-1*/) : (4 * 1000 * 1000))));

		sa.set(item, null);
		assertEquals(null, sa.get(item));
		assertEquals(null, saup.get(item));
		assertEquals(null, saln.get(item));
		assertContains(item, TYPE.search(sa.isNull()));

		sa.set(item2, null);
		assertEquals(null, sa.get(item2));
		assertEquals(null, saup.get(item2));
		assertEquals(null, saln.get(item2));
		assertContains(item, item2, TYPE.search(sa.isNull()));
	}
	
	private void assertStringSet(final Item item, final StringAttribute sa, final String value) throws ConstraintViolationException
	{
		final Type TYPE = item.getCopeType(); // TODO rename to type
		sa.set(item, value);
		assertEquals(value, sa.get(item));
		restartTransaction();
		assertEquals(value, sa.get(item));
		if(!oracle||sa.getMaximumLength()<4000)
		{
			if(!mysql || value.indexOf("Auml")<0) // TODO should work without condition
				assertEquals(list(item), TYPE.search(sa.equal(value)));
			assertEquals(list(), TYPE.search(sa.equal(value+"x")));
		}
	}
	
}

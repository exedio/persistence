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

package com.exedio.cope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.junit.CopeTest;
import com.exedio.cope.util.ItemCacheInfo;
import com.exedio.cope.util.PrimaryKeyInfo;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.UniqueConstraint;

public abstract class AbstractRuntimeTest extends CopeTest
{
	public AbstractRuntimeTest(final Model model)
	{
		super(model);
	}
	
	public AbstractRuntimeTest(final Model model, final boolean exclusive)
	{
		super(model, exclusive);
	}
	
	protected static final Integer i0 = Integer.valueOf(0);
	protected static final Integer i1 = Integer.valueOf(1);
	protected static final Integer i2 = Integer.valueOf(2);
	protected static final Integer i3 = Integer.valueOf(3);
	protected static final Integer i4 = Integer.valueOf(4);
	protected static final Integer i5 = Integer.valueOf(5);
	protected static final Integer i6 = Integer.valueOf(6);
	protected static final Integer i7 = Integer.valueOf(7);
	protected static final Integer i8 = Integer.valueOf(8);
	protected static final Integer i9 = Integer.valueOf(9);
	protected static final Integer i10= Integer.valueOf(10);
	protected static final Integer i18= Integer.valueOf(18);
	
	protected static final Long l0 = Long.valueOf(0l);
	protected static final Long l1 = Long.valueOf(1l);
	protected static final Long l2 = Long.valueOf(2l);
	protected static final Long l3 = Long.valueOf(3l);
	protected static final Long l4 = Long.valueOf(4l);
	protected static final Long l5 = Long.valueOf(5l);
	protected static final Long l6 = Long.valueOf(6l);
	protected static final Long l7 = Long.valueOf(7l);
	protected static final Long l8 = Long.valueOf(8l);
	protected static final Long l9 = Long.valueOf(9l);
	protected static final Long l10= Long.valueOf(10l);
	protected static final Long l18= Long.valueOf(18l);
	
	protected static final Double d1 = Double.valueOf(1.1);
	protected static final Double d2 = Double.valueOf(2.2);
	protected static final Double d3 = Double.valueOf(3.3);
	protected static final Double d4 = Double.valueOf(4.4);
	protected static final Double d5 = Double.valueOf(5.5);
	protected static final Double d6 = Double.valueOf(6.6);
	protected static final Double d7 = Double.valueOf(7.7);
	protected static final Double d8 = Double.valueOf(8.8);
	protected static final Double d9 = Double.valueOf(9.9);
	
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
	protected String mediaRootUrl = null;
	
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
		
		mediaRootUrl = model.getProperties().getMediaRootUrl();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		mediaRootUrl = null;
		
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
			model.getItem(id);
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
	
	final String mysqlLower(final String name)
	{
		return model.getProperties().getMysqlLowerCaseTableNames() ? name.toLowerCase() : name;
	}
	
	protected final CheckConstraint assertCheckConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition)
	{
		return assertConstraint(table, CheckConstraint.class, name, condition);
	}
	
	protected final void assertPkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition,
			final String column)
	{
		final PrimaryKeyConstraint constraint = assertConstraint(table, PrimaryKeyConstraint.class, name, condition);

		assertEquals(column, constraint.getPrimaryKeyColumn());
	}
	
	protected final void assertFkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String column,
			final String targetTable,
			final String targetColumn)
	{
		final ForeignKeyConstraint constraint = assertConstraint(table, ForeignKeyConstraint.class, name, null);

		assertEquals(column, constraint.getForeignKeyColumn());
		assertEquals(targetTable, constraint.getTargetTable());
		assertEquals(targetColumn, constraint.getTargetColumn());
	}
	
	protected final void assertUniqueConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String clause)
	{
		final UniqueConstraint constraint = assertConstraint(table, UniqueConstraint.class, name, clause);

		assertEquals(clause, constraint.getClause());
	}
	
	protected final <X extends Constraint> X assertConstraint(
			final com.exedio.dsmf.Table table,
			final Class<X> type,
			final String name,
			final String condition)
	{
		final Constraint constraint = table.getConstraint(name);
		final boolean expectedSupported = model.supportsCheckConstraints() || type!=CheckConstraint.class;
		assertNotNull("no such constraint "+name+", but has "+table.getConstraints(), constraint);
		assertEquals(name, type, constraint.getClass());
		assertEquals(name, condition, constraint.getRequiredCondition());
		assertEquals(expectedSupported, constraint.isSupported());
		assertEquals(name, expectedSupported ? null : "not supported", constraint.getError());
		assertEquals(name, Schema.Color.OK, constraint.getParticularColor());
		return type.cast(constraint);
	}
	
	protected void assertCacheInfo(final Type[] types, final int[] limits)
	{
		assertEquals(types.length, limits.length);
		
		final ItemCacheInfo[] ci = model.getItemCacheInfo();
		if(model.getProperties().getItemCacheLimit()>0)
		{
			assertEquals(types.length, ci.length);
			for(int i = 0; i<ci.length; i++)
			{
				assertEquals(types [i], ci[i].getType());
				assertEquals(limits[i], ci[i].getLimit());
			}
		}
		else
			assertEquals(0, ci.length);
	}
	
	protected void assertPrimaryKeyInfo(final Type type, final int count, final int first, final int last, final PrimaryKeyInfo info)
	{
		assertSame(type, info.getType());
		assertEquals(0, info.getMinimum());
		assertEquals(Integer.MAX_VALUE, info.getMaximum());
		assertEquals(count, info.getCount());
		assertTrue(info.isKnown());
		assertEquals(first, info.getFirst());
		assertEquals(last, info.getLast());
	}
	
	protected void assertPrimaryKeyInfo(final Type type, final PrimaryKeyInfo info)
	{
		assertSame(type, info.getType());
		assertEquals(0, info.getMinimum());
		assertEquals(Integer.MAX_VALUE, info.getMaximum());
		assertEquals(0, info.getCount());
		assertFalse(info.isKnown());
		try
		{
			info.getFirst();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("not known", e.getMessage());
		}
		try
		{
			info.getLast();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("not known", e.getMessage());
		}
	}
	
	protected void assertPrimaryKeyInfo(final List<PrimaryKeyInfo> actual, final Type... expected)
	{
		assertUnmodifiable(actual);
		final ArrayList<Type> actualTypes = new ArrayList<Type>();
		for(final PrimaryKeyInfo i : actual)
			actualTypes.add(i.getType());
		assertEquals(Arrays.asList(expected), actualTypes);
	}
}

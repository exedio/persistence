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

import static com.exedio.cope.SchemaInfo.isUpdateCounterEnabled;
import static com.exedio.cope.util.SafeFile.delete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeTest;
import com.exedio.cope.pattern.MediaPath;
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
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data4  = {-86,122,-8,23};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data6  = {-97,35,-126,86,19,-8};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data6x4= {-97,35,-126,86};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data8  = {-54,104,-63,23,19,-45,71,-23};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
	protected static final byte[] data20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_PKGPROTECT")
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
	protected boolean nullsFirst;

	private final ArrayList<File> files = new ArrayList<File>();
	private TestByteArrayInputStream testStream = null;
	protected String mediaRootUrl = null;

	static
	{
		ThreadSwarm.logger.setUseParentHandlers(false);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final String database = model.getConnectProperties().getDialect();

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
		cache = model.getConnectProperties().getItemCacheLimit()>0;
		nullsFirst = hsqldb;
		files.clear();

		mediaRootUrl = model.getConnectProperties().getMediaRootUrl();
	}

	@Override
	protected void tearDown() throws Exception
	{
		mediaRootUrl = null;

		for(final File file : files)
			delete(file);
		files.clear();

		final TestDatabaseListener testListener = model.setTestDatabaseListener(null);

		if(testCompletedSuccessfully())
			assertNull("test didn't un-install TestDatabaseListener", testListener);

		super.tearDown();
	}

	protected final String synthetic(final String name, final String global)
	{
		return
			model.getConnectProperties().longSyntheticNames.booleanValue()
			? (name + global)
			: name;
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
		catch(final IOException e)
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
				catch(final IOException e)
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
			final FileInputStream in = new FileInputStream(actualFile);
			try
			{
				in.read(actualData);
			}
			finally
			{
				in.close();
			}

			for(int i = 0; i<expectedData.length; i++)
				assertEquals(expectedData[i], actualData[i]);

			delete(actualFile);
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
		catch(final IntegrityViolationException e)
		{
			assertEquals(attribute, e.getFeature());
			assertEquals(attribute, e.getFeature());
			assertEquals(itemToBeDeleted, e.getItem());
			assertEquals("integrity violation on deletion of " + itemToBeDeleted.getCopeID() + " because of " + e.getFeature(), e.getMessage());
		}
		assertTrue(item.existsCopeItem());
	}

	protected void assertIDFails(final String id, final String detail, final boolean notAnID)
	{
		try
		{
			model.getItem(id);
			fail();
		}
		catch(final NoSuchIDException e)
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

	final String filterTableName(final String name)
	{
		return model.getConnectProperties().filterTableName(name);
	}

	protected final <T extends Item> void assertCondition(final Type<T> type, final Condition actual)
	{
		assertCondition(Collections.<T>emptyList(), type, actual);
	}

	protected final <T extends Item> void assertCondition(final T o1, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		assertCondition(l, type, actual);
	}

	protected final <T extends Item> void assertCondition(final T o1, final T o2, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		assertCondition(l, type, actual);
	}

	protected final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		assertCondition(l, type, actual);
	}

	protected final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		assertCondition(l, type, actual);
	}

	protected final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final T o5, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		l.add(o5);
		assertCondition(l, type, actual);
	}

	protected final <T extends Item> void assertCondition(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final Type<T> type, final Condition actual)
	{
		final ArrayList<T> l = new ArrayList<T>();
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		l.add(o5);
		l.add(o6);
		assertCondition(l, type, actual);
	}

	private final <T extends Item> void assertCondition(final List<T> expected, final Type<T> type, final Condition actual)
	{
		final List<T> actualResult = type.search(actual);
		assertContainsList(expected, actualResult);
		assertUnmodifiable(actualResult);
		for(final T item : type.search())
			assertEquals(expected.contains(item), actual.get(item));
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

	protected final <C extends Constraint> C assertConstraint(
			final com.exedio.dsmf.Table table,
			final Class<C> type,
			final String name,
			final String condition)
	{
		final Constraint constraint = table.getConstraint(name);
		final boolean expectedSupported = SchemaInfo.supportsCheckConstraints(model) || type!=CheckConstraint.class;
		assertNotNull("no such constraint "+name+", but has "+table.getConstraints(), constraint);
		assertEquals(name, type, constraint.getClass());
		assertEquals(name, condition, constraint.getRequiredCondition());
		assertEquals(expectedSupported, constraint.isSupported());
		assertEquals(name, expectedSupported ? null : "not supported", constraint.getError());
		assertEquals(name, Schema.Color.OK, constraint.getParticularColor());
		return type.cast(constraint);
	}

	protected void assertCacheInfo(final Type[] types, final int[] limitWeigths)
	{
		assertEquals(types.length, limitWeigths.length);

		int limitWeigthsSum = 0;
		for(final int limitWeigth : limitWeigths)
			limitWeigthsSum += limitWeigth;

		final int limit = model.getConnectProperties().getItemCacheLimit();
		final ItemCacheInfo[] ci = model.getItemCacheInfo();
		if(limit>0)
		{
			assertEquals(types.length, ci.length);
			for(int i = 0; i<ci.length; i++)
			{
				assertEquals(types [i], ci[i].getType());
				assertEquals(limitWeigths[i]*limit/limitWeigthsSum, ci[i].getLimit());
			}
		}
		else
			assertEquals(0, ci.length);
	}

	protected void assertInfo(final Type type, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertInfo(type, count, first, last, info, 0);
	}

	protected void assertInfo(final Type type, final int count, final int first, final int last, final SequenceInfo info, final int check)
	{
		assertInfoX(type.getThis(), 0, 0, Integer.MAX_VALUE, count, first, last, info);
		if(!hsqldb)
			assertEquals(check, type.checkPrimaryKey());
	}

	protected void assertInfo(final IntegerField feature, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertInfo(feature, count, first, last, info, 0);
	}

	protected void assertInfo(final IntegerField feature, final int count, final int first, final int last, final SequenceInfo info, final int check)
	{
		assertInfoX(feature, feature.getDefaultNextStart().intValue(), feature.getMinimum(), feature.getMaximum(), count, first, last, info);
		if(!hsqldb)
			assertEquals(check, feature.checkDefaultToNext());
	}

	private void assertInfoX(final Feature feature, final int start, final int minimum, final int maximum, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertSame(feature, info.getFeature());
		assertEquals(start, info.getStart());
		assertEquals(minimum, info.getMinimum());
		assertEquals(maximum, info.getMaximum());
		assertEquals(count, info.getCount());
		assertTrue(info.isKnown());
		assertEquals(first, info.getFirst());
		assertEquals(last, info.getLast());
	}

	protected void assertInfo(final Type type, final SequenceInfo info)
	{
		assertInfoX(type.getThis(), 0, 0, Integer.MAX_VALUE, info);
		if(!hsqldb)
			assertEquals(0, type.checkPrimaryKey());
	}

	protected void assertInfo(final IntegerField feature, final SequenceInfo info)
	{
		assertInfo(feature, info, 0);
	}

	protected void assertInfo(final IntegerField feature, final SequenceInfo info, final int check)
	{
		assertInfoX(feature, feature.getDefaultNextStart().intValue(), feature.getMinimum(), feature.getMaximum(), info);
		if(!hsqldb)
			assertEquals(check, feature.checkDefaultToNext());
	}

	private void assertInfoX(final Feature feature, final int start, final int minimum, final int maximum, final SequenceInfo info)
	{
		assertSame(feature, info.getFeature());
		assertEquals(start, info.getStart());
		assertEquals(minimum, info.getMinimum());
		assertEquals(maximum, info.getMaximum());
		assertEquals(0, info.getCount());
		assertFalse(info.isKnown());
		try
		{
			info.getFirst();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not known", e.getMessage());
		}
		try
		{
			info.getLast();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not known", e.getMessage());
		}
	}

	protected void assertInfo(final List<SequenceInfo> actual, final Feature... expected)
	{
		assertUnmodifiable(actual);
		final ArrayList<Feature> actualTypes = new ArrayList<Feature>();
		for(final SequenceInfo i : actual)
			actualTypes.add(i.getFeature());
		assertEquals(Arrays.asList(expected), actualTypes);
	}

	public static void assertTestAnnotationNull(final Type<?> ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation.class));
		assertNull(ae.getAnnotation(TestAnnotation.class));
	}

	public static void assertTestAnnotationNull(final Feature ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation.class));
		assertNull(ae.getAnnotation(TestAnnotation.class));
	}

	public static void assertTestAnnotation2Null(final Type<?> ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(null, ae.getAnnotation(TestAnnotation2.class));
	}

	public static void assertTestAnnotation2Null(final Feature ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation2.class));
		assertNull(ae.getAnnotation(TestAnnotation2.class));
	}

	public static void assertTestAnnotation(final String value, final Type<?> ae)
	{
		assertTrue(ae.isAnnotationPresent(TestAnnotation.class));
		assertEquals(value, ae.getAnnotation(TestAnnotation.class).value());
	}

	public static void assertTestAnnotation(final String value, final Feature ae)
	{
		assertTrue(ae.isAnnotationPresent(TestAnnotation.class));
		assertEquals(value, ae.getAnnotation(TestAnnotation.class).value());
	}

	public static void assertTestAnnotation2(final Feature ae)
	{
		assertTrue(ae.isAnnotationPresent(TestAnnotation2.class));
		assertNotNull(ae.getAnnotation(TestAnnotation2.class));
	}

	public static final void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}

	final void assertCheckUpdateCounters()
	{
		for(final Type type : model.getTypes())
		{
			if(isUpdateCounterEnabled(model))
			{
				if(type.needsCheckUpdateCounter())
					assertEquals(0, type.checkUpdateCounter());
				else
					assertCheckUpdateCounterFails(type);
			}
			else
			{
				assertFalse(type.needsCheckUpdateCounter());
				assertCheckUpdateCounterFails(type);
			}
		}
	}

	private static final void assertCheckUpdateCounterFails(final Type type)
	{
		try
		{
			type.checkUpdateCounter();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("no check for update counter needed for " + type, e.getMessage());
		}
	}

	public void assertLocator(
			final MediaPath feature,
			final String path,
			final MediaPath.Locator locator)
	{
		// locator methods must work without transaction
		final Transaction tx = model.leaveTransaction();
		try
		{
			assertSame(feature, locator.getFeature());
			assertEquals(path, locator.getPath());
			assertEquals(path, locator.toString());
			final StringBuilder bf = new StringBuilder();
			locator.appendPath(bf);
			assertEquals(path, bf.toString());
		}
		finally
		{
			model.joinTransaction(tx);
		}
	}

	@SuppressWarnings("deprecation")
	protected static java.lang.reflect.Type getInitialType(final Settable settable)
	{
		return settable.getInitialType();
	}
}

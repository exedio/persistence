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

import static com.exedio.cope.util.StrictFile.delete;

import com.exedio.cope.junit.CopeTest;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractRuntimeTest extends CopeTest
{
	private final RuntimeTester tester;

	public AbstractRuntimeTest(final Model model)
	{
		super(model);
		tester = new RuntimeTester(model);
	}

	public AbstractRuntimeTest(final Model model, final boolean exclusive)
	{
		super(model, exclusive);
		tester = new RuntimeTester(model);
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

	protected RuntimeTester.Dialect dialect = null;
	protected boolean hsqldb;
	protected boolean mysql;
	protected boolean oracle;
	protected boolean postgresql;
	protected boolean cache;

	private final FileFixture files = new FileFixture();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		tester.setUp();
		dialect = tester.dialect;
		hsqldb = tester.hsqldb;
		mysql  = tester.mysql;
		oracle = tester.oracle;
		postgresql = tester.postgresql;
		cache = tester.cache;
		files.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		files.tearDown();

		final TestDatabaseListener testListener = model.setTestDatabaseListener(null);

		if(testCompletedSuccessfully())
			assertNull("test didn't un-install TestDatabaseListener", testListener);

		super.tearDown();
	}

	protected final String synthetic(final String name, final String global)
	{
		return tester.synthetic(name, global);
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final Type<?> type)
	{
		tester.assertPrimaryKeySequenceName(sequenceNameBase, type);
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final String batchedSequenceNameBase, final Type<?> type)
	{
		tester.assertPrimaryKeySequenceName(sequenceNameBase, batchedSequenceNameBase, type);
	}

	protected final void assertDefaultToNextSequenceName(final String name, final IntegerField field)
	{
		tester.assertDefaultToNextSequenceName(name, field);
	}

	protected final TestByteArrayInputStream stream(final byte[] data)
	{
		return tester.stream(data);
	}

	protected final void assertStreamClosed()
	{
		tester.assertStreamClosed();
	}

	protected final File file(final byte[] data)
	{
		return files.file(data);
	}

	protected static final void assertEqualContent(final byte[] expectedData, final File actualFile) throws IOException
	{
		if(expectedData==null)
			assertFalse(actualFile.exists());
		else
		{
			assertTrue(actualFile.exists());
			assertEquals(expectedData.length, actualFile.length());

			final byte[] actualData = new byte[expectedData.length];
			try(FileInputStream in = new FileInputStream(actualFile))
			{
				assertEquals(expectedData.length, in.read(actualData));
			}

			for(int i = 0; i<expectedData.length; i++)
				assertEquals(expectedData[i], actualData[i]);

			delete(actualFile);
		}
	}

	protected static void assertDelete(final Item item) throws IntegrityViolationException
	{
		assertTrue(item.existsCopeItem());
		item.deleteCopeItem();
		assertTrue(!item.existsCopeItem());
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute)
	{
		assertDeleteFails(item, attribute, item);
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute, final int referrers)
	{
		assertDeleteFails(item, attribute, item, referrers);
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute, final Item itemToBeDeleted)
	{
		assertDeleteFails(item, attribute, itemToBeDeleted, 1);
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute, final Item itemToBeDeleted, final int referrers)
	{
		try
		{
			item.deleteCopeItem();
			fail();
		}
		catch(final IntegrityViolationException e)
		{
			assertSame(attribute, e.getFeature());
			assertSame(attribute, e.getFeature());
			assertEquals(itemToBeDeleted, e.getItem());
			assertEquals(
					"integrity violation " +
					"on deletion of " + itemToBeDeleted.getCopeID() +
					" because of " + e.getFeature() +
					" referring to " + referrers + " item(s)",
					e.getMessage());
		}
		assertTrue(item.existsCopeItem());
	}

	protected void assertIDFails(final String id, final String detail, final boolean notAnID)
	{
		tester.assertIDFails(id, detail, notAnID);
	}

	void assertSameCache(final Object o1, final Object o2)
	{
		tester.assertSameCache(o1, o2);
	}

	final String primaryKeySequenceName(final String nameBase)
	{
		return tester.primaryKeySequenceName(nameBase);
	}

	final String filterTableName(final String name)
	{
		return tester.filterTableName(name);
	}

	protected final void assertCause(final UniqueViolationException e)
	{
		tester.assertCause(e);
	}

	protected final String notNull(final String field, final String condition)
	{
		return tester.notNull(field, condition);
	}

	protected final CheckConstraint assertCheckConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition)
	{
		return tester.assertCheckConstraint(table, name, condition);
	}

	protected final void assertPkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition,
			final String column)
	{
		tester.assertPkConstraint(table, name, condition, column);
	}

	protected final void assertFkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String column,
			final String targetTable,
			final String targetColumn)
	{
		tester.assertFkConstraint(table, name, column, targetTable, targetColumn);
	}

	protected final void assertUniqueConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String clause)
	{
		tester.assertUniqueConstraint(table, name, clause);
	}

	protected final <C extends Constraint> C assertConstraint(
			final com.exedio.dsmf.Table table,
			final Class<C> type,
			final String name,
			final String condition)
	{
		return tester.assertConstraint(table, type, name, condition);
	}

	protected void assertCacheInfo(final Type<?>[] types, final int[] limitWeigths)
	{
		tester.assertCacheInfo(types, limitWeigths);
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

	final void assertCheckUpdateCounters()
	{
		tester.assertCheckUpdateCounters();
	}

	protected void assertSchema()
	{
		tester.assertSchema();
	}

	public static java.lang.reflect.Type getInitialType(final Settable<?> settable)
	{
		return settable.getInitialType();
	}
}

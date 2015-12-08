/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeModelTest;
import com.exedio.dsmf.CheckConstraint;
import java.io.File;

public abstract class AbstractRuntimeModelTest extends CopeModelTest
{
	private final RuntimeTester tester;

	public AbstractRuntimeModelTest(final Model model)
	{
		super(model);
		tester = new RuntimeTester(model);
	}

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
		oracle  = tester.oracle;
		postgresql = tester.postgresql;
		cache = tester.cache;
		files.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		files.tearDown();
		System.clearProperty("media.url.secret");

		super.tearDown();
	}

	protected final void startTransaction()
	{
		model.startTransaction(getClass().getName());
	}

	protected final void commit()
	{
		model.commit();
	}

	protected final File file(final byte[] data)
	{
		return files.file(data);
	}

	protected final File deleteOnTearDown(final File file)
	{
		return files.deleteOnTearDown(file);
	}

	protected void assertIDFails(final String id, final String detail, final boolean notAnID)
	{
		tester.assertIDFails(id, detail, notAnID);
	}

	protected final TestByteArrayInputStream stream(final byte[] data)
	{
		return tester.stream(data);
	}

	protected final void assertStreamClosed()
	{
		tester.assertStreamClosed();
	}

	protected void assertSchema()
	{
		tester.assertSchema();
	}

	protected final String filterTableName(final String name)
	{
		return tester.filterTableName(name);
	}

	// copied from CopeTest
	protected void restartTransaction()
	{
		final String oldName = model.currentTransaction().getName();
		model.commit();
		model.startTransaction( oldName+"-restart" );
	}

	protected final String synthetic(final String name, final String global)
	{
		return tester.synthetic(name, global);
	}

	final String primaryKeySequenceName(final String nameBase)
	{
		return tester.primaryKeySequenceName(nameBase);
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final Type<?> type)
	{
		tester.assertPrimaryKeySequenceName(sequenceNameBase, type);
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final String batchedSequenceNameBase, final Type<?> type)
	{
		tester.assertPrimaryKeySequenceName(sequenceNameBase, batchedSequenceNameBase, type);
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

	protected final CheckConstraint assertCheckConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition)
	{
		return tester.assertCheckConstraint(table, name, condition);
	}

	protected final void assertDefaultToNextSequenceName(final String name, final IntegerField field)
	{
		tester.assertDefaultToNextSequenceName(name, field);
	}

	final void assertCheckUpdateCounters()
	{
		tester.assertCheckUpdateCounters();
	}

	void assertSameCache(final Object o1, final Object o2)
	{
		tester.assertSameCache(o1, o2);
	}

	protected final void assertCause(final UniqueViolationException e)
	{
		tester.assertCause(e);
	}

	protected void assertCacheInfo(final Type<?>[] types, final int[] limitWeigths)
	{
		tester.assertCacheInfo(types, limitWeigths);
	}
}

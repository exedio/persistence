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

import static com.exedio.cope.SchemaInfo.getDefaultToNextSequenceName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeySequenceName;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.UniqueConstraint;
import java.sql.SQLException;

final class RuntimeTester
{
	private final Model model;

	public RuntimeTester(final Model model)
	{
		this.model = model;
	}

	enum Dialect
	{
		hsqldb("TIMESTAMP(3) WITHOUT TIME ZONE"),
		mysql(null),
		oracle("TIMESTAMP(3)"),
		postgresql("timestamp (3) without time zone");

		final String dateTimestampType;

		Dialect(final String dateTimestampType)
		{
			this.dateTimestampType = dateTimestampType;
		}
	}

	Dialect dialect = null;
	boolean hsqldb;
	boolean mysql;
	boolean oracle;
	boolean postgresql;
	boolean cache;

	private TestByteArrayInputStream testStream = null;

	void setUp() throws Exception
	{
		final String database = model.getConnectProperties().getDialect();

		if("com.exedio.cope.HsqldbDialect".equals(database))
			dialect = Dialect.hsqldb;
		else if("com.exedio.cope.MysqlDialect".equals(database))
			dialect = Dialect.mysql;
		else if("com.exedio.cope.OracleDialect".equals(database))
			dialect = Dialect.oracle;
		else if("com.exedio.cope.PostgresqlDialect".equals(database))
			dialect = Dialect.postgresql;
		else
			fail(database);


		hsqldb     = dialect==Dialect.hsqldb;
		mysql      = dialect==Dialect.mysql;
		oracle     = dialect==Dialect.oracle;
		postgresql = dialect==Dialect.postgresql;
		cache = model.getConnectProperties().getItemCacheLimit()>0;
	}

	protected final String synthetic(final String name, final String global)
	{
		return
			model.getConnectProperties().longSyntheticNames
			? (name + global)
			: name;
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final Type<?> type)
	{
		assertPrimaryKeySequenceName( sequenceNameBase, sequenceNameBase+"6", type );
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final String batchedSequenceNameBase, final Type<?> type)
	{
		if(model.getConnectProperties().primaryKeyGenerator.persistent)
		{
			final String name;
			if ( model.getConnectProperties().primaryKeyGenerator==PrimaryKeyGenerator.batchedSequence )
			{
				name = batchedSequenceNameBase;
			}
			else
			{
				name = sequenceNameBase;
			}
			assertEquals(filterTableName(name), getPrimaryKeySequenceName(type));
		}
		else
		{
			try
			{
				getPrimaryKeySequenceName(type);
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals("no sequence for " + type, e.getMessage());
			}
		}
	}

	protected final void assertDefaultToNextSequenceName(final String name, final IntegerField field)
	{
		assertEquals(filterTableName(name), getDefaultToNextSequenceName(field));
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

	void assertSameCache(final Object o1, final Object o2)
	{
		if(cache)
			assertSame(o1, o2);
		else
			assertNotSame(o1, o2);
	}

	final String primaryKeySequenceName(final String nameBase)
	{
		final String name;
		if ( model.getConnectProperties().primaryKeyGenerator==PrimaryKeyGenerator.batchedSequence )
		{
			name = nameBase+"6";
		}
		else
		{
			name = nameBase;
		}
		return filterTableName( name );
	}

	final String filterTableName(final String name)
	{
		return model.getConnectProperties().filterTableName(name);
	}

	protected final void assertCause(final UniqueViolationException e)
	{
		final Throwable cause = e.getCause();
		if(model.connect().executor.supportsUniqueViolation)
		{
			assertNotNull(e.getCause());
			assertTrue(cause.getClass().getName(), cause instanceof SQLException);
		}
		else
		{
			assertEquals(null, cause);
		}
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
		final ForeignKeyConstraint constraint = assertConstraint(table, ForeignKeyConstraint.class, name, column + "->" + targetTable + '.' + targetColumn);

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

	protected static void assertNotExistsConstraint(
			final com.exedio.dsmf.Table table,
			final String name)
	{
		assertNull(name, table.getConstraint(name));
	}

	protected void assertCacheInfo(final Type<?>[] types, final int[] limitWeigths)
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

	final void assertCheckUpdateCounters()
	{
		for(final Type<?> type : model.getTypes())
		{
			if(type.needsCheckUpdateCounter())
				assertEquals(0, type.checkUpdateCounter());
			else
				assertCheckUpdateCounterFails(type);
		}
	}

	private static final void assertCheckUpdateCounterFails(final Type<?> type)
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

	protected void assertSchema()
	{
		model.commit();

		final com.exedio.dsmf.Schema schema = model.getVerifiedSchema();

		model.startTransaction(RuntimeTester.class.getName() + "#assertSchema");

		for(final com.exedio.dsmf.Table table : schema.getTables())
		{
			for(final com.exedio.dsmf.Column column : table.getColumns())
				assertOk(table.getName() + '#' + column.getName() + '#' + column.getType(), column);

			if(hsqldb)
				continue;

			for(final com.exedio.dsmf.Constraint constraint : table.getConstraints())
			{
				final String message = table.getName() + '#' + constraint.getName();
				if(constraint instanceof com.exedio.dsmf.CheckConstraint &&
					!SchemaInfo.supportsCheckConstraints(model))
				{
					assertEquals(message, "not supported", constraint.getError());
					assertEquals(message, Schema.Color.OK, constraint.getParticularColor());
					assertEquals(message, Schema.Color.OK, constraint.getCumulativeColor());
				}
				else
				{
					assertOk(message, constraint);
				}
			}

			assertOk(table.getName(), table);
		}

		for(final com.exedio.dsmf.Sequence sequence : schema.getSequences())
			assertOk(sequence.getName(), sequence);

		if(hsqldb)
			return;

		assertOk("schema", schema);
	}

	private static final void assertOk(final String message, final com.exedio.dsmf.Node node)
	{
		assertEquals(message, null, node.getError());
		assertEquals(message, Schema.Color.OK, node.getParticularColor());
		assertEquals(message, Schema.Color.OK, node.getCumulativeColor());
	}
}

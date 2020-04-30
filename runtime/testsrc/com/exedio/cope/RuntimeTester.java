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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.UniqueConstraint;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class RuntimeTester
{
	private final Model model;

	RuntimeTester(final Model model)
	{
		this.model = model;
	}

	enum Dialect
	{
		hsqldb("TIMESTAMP(3) WITHOUT TIME ZONE"),
		mysql("datetime(3)"),
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

	void setUp()
	{
		final String database = model.getConnectProperties().getDialect();

		switch(database)
		{
			case "com.exedio.cope.HsqldbDialect":     dialect = Dialect.hsqldb;     break;
			case "com.exedio.cope.MysqlDialect":      dialect = Dialect.mysql;      break;
			case "com.exedio.cope.OracleDialect":     dialect = Dialect.oracle;     break;
			case "com.exedio.cope.PostgresqlDialect": dialect = Dialect.postgresql; break;
			default:
				fail(database);
				break;
		}

		hsqldb     = dialect==Dialect.hsqldb;
		mysql      = dialect==Dialect.mysql;
		oracle     = dialect==Dialect.oracle;
		postgresql = dialect==Dialect.postgresql;
		cache = model.getConnectProperties().getItemCacheLimit()>0;
	}

	String synthetic(final String name, final String global)
	{
		return
			model.getConnectProperties().longSyntheticNames
			? (name + global)
			: name;
	}

	void assertPrimaryKeySequenceName(final String sequenceNameBase, final Type<?> type)
	{
		assertPrimaryKeySequenceName( sequenceNameBase, sequenceNameBase+"6", type );
	}

	void assertPrimaryKeySequenceName(final String sequenceNameBase, final String batchedSequenceNameBase, final Type<?> type)
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

	void assertDefaultToNextSequenceName(final String name, final IntegerField field)
	{
		assertEquals(filterTableName(name), getDefaultToNextSequenceName(field));
	}

	TestByteArrayInputStream stream(final byte[] data)
	{
		assertNull(testStream);
		final TestByteArrayInputStream result = new TestByteArrayInputStream(data);
		testStream = result;
		return result;
	}

	void assertStreamClosed()
	{
		assertNotNull(testStream);
		testStream.assertClosed();
		testStream = null;
	}

	void assertIDFails(final String id, final String detail, final boolean notAnID)
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

	String primaryKeySequenceName(final String nameBase)
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

	String filterTableName(final String name)
	{
		return model.getConnectProperties().filterTableName(name);
	}

	void assertCause(final UniqueViolationException e)
	{
		final Throwable cause = e.getCause();
		if(model.connect().supportsUniqueViolation)
		{
			assertNotNull(e.getCause());
			assertTrue(cause instanceof SQLException, cause.getClass().getName());
		}
		else
		{
			assertEquals(null, cause);
		}
	}

	CheckConstraint assertCheckConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition)
	{
		return assertConstraint(table, CheckConstraint.class, name, condition);
	}

	void assertPkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition,
			final String column)
	{
		final PrimaryKeyConstraint constraint = assertConstraint(table, PrimaryKeyConstraint.class, name, condition);

		assertEquals(column, constraint.getPrimaryKeyColumn());
	}

	void assertFkConstraint(
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

	void assertUniqueConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String clause)
	{
		final UniqueConstraint constraint = assertConstraint(table, UniqueConstraint.class, name, clause);

		assertEquals(clause, constraint.getClause());
	}

	<C extends Constraint> C assertConstraint(
			final com.exedio.dsmf.Table table,
			final Class<C> type,
			final String name,
			final String condition)
	{
		final Constraint constraint = table.getConstraint(name);
		final boolean expectedSupported = SchemaInfo.supportsCheckConstraints(model) || type!=CheckConstraint.class;
		assertNotNull(constraint, "no such constraint "+name+", but has "+table.getConstraints());
		assertEquals(type, constraint.getClass(), name);
		assertEquals(condition, constraint.getRequiredCondition(), name);
		assertEquals(expectedSupported, constraint.isSupported());
		assertEquals(expectedSupported ? null : "not supported", constraint.getError(), name);
		assertEquals(Node.Color.OK, constraint.getParticularColor(), name);
		return type.cast(constraint);
	}

	static void assertNotExistsConstraint(
			final com.exedio.dsmf.Table table,
			final String name)
	{
		assertNull(table.getConstraint(name), name);
	}

	void assertCacheInfo(final Type<?>... types)
	{
		final int limit = model.getConnectProperties().getItemCacheLimit();
		final ItemCacheInfo[] ci = model.getItemCacheStatistics().getDetails();
		if(limit>0)
		{
			assertEquals(types.length, ci.length);
			for(int i = 0; i<ci.length; i++)
			{
				assertEquals(types [i], ci[i].getType());
			}
		}
		else
			assertEquals(0, ci.length);
	}

	void assertCheckUpdateCounters()
	{
		for(final Type<?> type : model.getTypes())
		{
			if(type.needsCheckUpdateCounter())
				assertEquals(0, type.checkUpdateCounterL());
			else
				assertCheckUpdateCounterFails(type);
		}
	}

	private static void assertCheckUpdateCounterFails(final Type<?> type)
	{
		try
		{
			type.checkUpdateCounterL();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("no check for update counter needed for " + type, e.getMessage());
		}
	}

	void assertSchema()
	{
		model.commit();

		final com.exedio.dsmf.Schema schema = model.getVerifiedSchema();

		model.startTransaction(RuntimeTester.class.getName() + "#assertSchema");

		for(final com.exedio.dsmf.Table table : schema.getTables())
		{
			for(final com.exedio.dsmf.Column column : table.getColumns())
				assertOk(table.getName() + '#' + column.getName() + '#' + column.getType(), column);

			for(final com.exedio.dsmf.Constraint constraint : table.getConstraints())
			{
				final String message = table.getName() + '#' + constraint.getName();
				if(constraint instanceof com.exedio.dsmf.CheckConstraint &&
					!SchemaInfo.supportsCheckConstraints(model))
				{
					assertEquals("not supported", constraint.getError(), message);
					assertEquals(Node.Color.OK, constraint.getParticularColor(), message);
					assertEquals(Node.Color.OK, constraint.getCumulativeColor(), message);
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

		assertOk("schema", schema);
	}

	private static void assertOk(final String message, final com.exedio.dsmf.Node node)
	{
		assertEquals(null, node.getError(), message);
		assertEquals(Node.Color.OK, node.getParticularColor(), message);
		assertEquals(Node.Color.OK, node.getCumulativeColor(), message);
	}

	static void assertFieldsCovered(
			final List<Field<?>> expected,
			final Selectable<?> actual)
	{
		final ArrayList<Field<?>> consumer = new ArrayList<>();
		actual.acceptFieldsCovered(consumer::add);
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(expected, consumer);
	}

	static void assertFieldsCovered(
			final List<Field<?>> expected,
			final Condition actual)
	{
		final ArrayList<Field<?>> consumer = new ArrayList<>();
		actual.acceptFieldsCovered(consumer::add);
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(expected, consumer);
	}
}

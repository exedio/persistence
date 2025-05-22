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

import static com.exedio.cope.MysqlDialect.REGEXP;
import static com.exedio.cope.SchemaInfo.getDefaultToNextSequenceName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeySequenceName;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraint;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

	protected enum Dialect
	{
		hsqldb("TIMESTAMP(3) WITHOUT TIME ZONE"),
		mysql("datetime(3)"),
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
	boolean postgresql;
	boolean cache;

	private TestByteArrayInputStream testStream = null;

	void setUp()
	{
		final String database = model.getConnectProperties().getDialect();

		dialect = switch(database)
		{
			case "com.exedio.cope.HsqldbDialect"     -> Dialect.hsqldb;
			case "com.exedio.cope.MysqlDialect"      -> Dialect.mysql;
			case "com.exedio.cope.PostgresqlDialect" -> Dialect.postgresql;
			default ->
				fail(database);
		};

		hsqldb     = dialect==Dialect.hsqldb;
		mysql      = dialect==Dialect.mysql;
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
		assertTrue(sequenceNameBase.endsWith("_Seq"), sequenceNameBase);

		if(model.getConnectProperties().primaryKeyGenerator.persistent)
		{
			final String name;
			if ( model.getConnectProperties().primaryKeyGenerator==PrimaryKeyGenerator.batchedSequence )
			{
				name = primaryKeySequenceNameToBatched(sequenceNameBase);
			}
			else
			{
				name = sequenceNameBase;
			}
			assertEquals(name, getPrimaryKeySequenceName(type));
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
		assertEquals(name, getDefaultToNextSequenceName(field));
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
		assertTrue(nameBase.endsWith("_Seq"), nameBase);

		final String name;
		if ( model.getConnectProperties().primaryKeyGenerator==PrimaryKeyGenerator.batchedSequence )
		{
			name = primaryKeySequenceNameToBatched(nameBase);
		}
		else
		{
			name = nameBase;
		}
		return name;
	}

	private static String primaryKeySequenceNameToBatched(final String name)
	{
		assertTrue(name.endsWith("_Seq"), name);

		return name.substring(0, name.length()-2) + "q6";
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

	PrimaryKeyConstraint assertPkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String column)
	{
		final PrimaryKeyConstraint constraint = assertConstraint(table, PrimaryKeyConstraint.class, name, null);

		assertEquals(column, constraint.getPrimaryKeyColumn());

		return constraint;
	}

	ForeignKeyConstraint assertFkConstraint(
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

		return constraint;
	}

	UniqueConstraint assertUniqueConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String clause)
	{
		final UniqueConstraint constraint = assertConstraint(table, UniqueConstraint.class, name, clause);

		assertEquals(clause, constraint.getClause());

		return constraint;
	}

	<C extends Constraint> C assertConstraint(
			final com.exedio.dsmf.Table table,
			final Class<C> type,
			final String name,
			final String condition)
	{
		final Constraint constraint = table.getConstraint(name);
		final boolean expectedSupported = type!=CheckConstraint.class || (supportsCheckConstraint(model)&&!condition.contains(REGEXP));
		assertNotNull(constraint, "no such constraint "+name+", but has "+table.getConstraints());
		assertEquals(type, constraint.getClass(), name);
		assertEquals(condition, constraint.getRequiredCondition(), name);
		assertEquals(expectedSupported, constraint.isSupported());
		assertEquals(expectedSupported ? null : "unsupported", constraint.getError(), name);
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
		final ItemCacheInfo[] ci = getItemCacheStatistics(model).getDetails();
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

	@SuppressWarnings("deprecation")
	void assertCheckUpdateCounters()
	{
		for(final Type<?> type : model.getTypes())
		{
			assertEquals(false, type.needsCheckUpdateCounter());
		}
	}

	void assertSchema()
	{
		model.commit();

		final com.exedio.dsmf.Schema schema = model.getVerifiedSchema();

		model.startTransaction(RuntimeTester.class.getName() + "#assertSchema");

		assertSchema(schema);
	}

	void assertSchema(final com.exedio.dsmf.Schema schema)
	{
		for(final com.exedio.dsmf.Table table : schema.getTables())
		{
			for(final com.exedio.dsmf.Column column : table.getColumns())
			{
				for(final Constraint constraint : column.getConstraints())
					assertOk(constraint);
				assertOk(column, table.getName() + '#' + column.getName() + '#' + column.getType());
			}

			for(final Constraint constraint : table.getTableConstraints())
				assertOk(constraint);

			assertOk(table, table.getName());
		}

		for(final com.exedio.dsmf.Sequence sequence : schema.getSequences())
			assertOk(sequence, sequence.getName());

		assertOk(schema, "schema");
	}

	private void assertOk(final Constraint constraint)
	{
		assertOk(
				constraint instanceof CheckConstraint &&
				(!supportsCheckConstraint(model) || constraint.getRequiredCondition().contains(REGEXP))
						? "unsupported"
						: null,
				constraint,
				constraint.getTable().getName() + '#' + constraint.getName());
	}

	private static void assertOk(final Node node, final String message)
	{
		assertOk(null, node, message);
	}

	private static void assertOk(final String error, final Node node, final String message)
	{
		assertAll(
				message,
				() -> assertEquals(error, node.getError(), "error"),
				() -> assertEquals(true, node.required(), "required"),
				() -> assertEquals(!"unsupported".equals(error), node.exists(), "exists"),
				() -> assertEqualsUnmodifiable(List.of(), node.getAdditionalErrors(), "additionalErrors"),
				() -> assertEquals(Node.Color.OK, node.getParticularColor(), "particularColor"),
				() -> assertEquals(Node.Color.OK, node.getCumulativeColor(), "cumulativeColor"));
	}

	static void assertFieldsCovered(
			final List<Field<?>> expected,
			final Selectable<?> actual)
	{
		final ArrayList<Field<?>> consumer = new ArrayList<>();
		actual.forEachFieldCovered(consumer::add);
		assertEquals(expected, consumer);
	}

	static void assertFieldsCovered(
			final List<Field<?>> expected,
			final Condition actual)
	{
		final ArrayList<Field<?>> consumer = new ArrayList<>();
		actual.forEachFieldCovered(consumer::add);
		assertEquals(expected, consumer);
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static ItemCacheStatistics getItemCacheStatistics(final Model model)
	{
		return model.getItemCacheStatistics();
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static QueryCacheInfo getQueryCacheInfo(final Model model)
	{
		return model.getQueryCacheInfo();
	}
}

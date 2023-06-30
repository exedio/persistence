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

import static com.exedio.cope.GroupByTest.AnItem.TYPE;
import static com.exedio.cope.GroupByTest.AnItem.integer;
import static com.exedio.cope.GroupByTest.AnItem.string;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.SI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupByTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public GroupByTest()
	{
		super(MODEL);
	}

	@BeforeEach final void setUp()
	{
		new AnItem("foo", 1);
		new AnItem("foo", 2);
		new AnItem("foo", 3);
		new AnItem("bar", 4);
		new AnItem("bar", 5);
		new AnItem("goo", 6);
		new AnItem("car", 7);
		new AnItem("car", 8);
	}

	@Test void testSimpleCount()
	{
		final Query<AnItem> items = TYPE.newQuery();
		assertCount(items, 8, 8);
	}

	@Test void testSimpleCountWithLimit()
	{
		final Query<AnItem> items = TYPE.newQuery();
		items.setPage(0, 3);
		assertCount(items, 3, 8);
	}

	@Test void testGroupByCount()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{string, integer.min()};
		final Query<List<Object>> items = Query.newQuery(selection, TYPE, null);
		items.setGroupBy(string);
		assertCount(items, 4, 4);
	}

	@Test void testGroupByCountWithLimit()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{string, integer.min()};
		final Query<List<Object>> items = Query.newQuery(selection, TYPE, null);
		items.setGroupBy(string);
		items.setPage(0, 3);
		assertCount(items, 3, 4);
	}

	@Test void testGroupByInvalidSelect()
	{
		final Query<Integer> query = new Query<>(integer);
		query.setGroupBy(string);

		final String table = getTableName(TYPE);
		final String column = getColumnName(integer);

		switch(dialect)
		{
			case hsqldb:
			{
				final String message =
						"expression not in aggregate or GROUP BY columns: " +
						"PUBLIC.\"" + table + "\".\"" + column + "\"" + ifPrep(" in statement ");
				notAllowed(query, message + ifPrep("[SELECT \"integer\" FROM " + SI.tab(TYPE) + " GROUP BY \"string\"]"));
				notAllowedTotal(query, message + ifPrep("[SELECT COUNT(*) FROM ( SELECT \"integer\" FROM " + SI.tab(TYPE) + " GROUP BY \"string\" )]"));
				break;
			}
			case mysql:
			{
				final String message =
						atLeastMysql57()
						?
						"Expression #1 of SELECT list is not in GROUP BY clause and " +
						"contains nonaggregated column '" + dbCat() + ".AnItem.integer' " +
						"which is not functionally dependent on columns in GROUP BY clause; " +
						"this is incompatible with sql_mode=only_full_group_by"
						:
						"'" + dbCat() + "." + table + "." + column + "' isn't in GROUP BY";

				notAllowed(query, message);
				notAllowedTotal(query, message);
				break;
			}
			case postgresql:
			{
				final String message =
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function";
				restartTransaction();
				notAllowed(query, message + postgresqlPosition(8));
				restartTransaction();
				notAllowedTotal(query, message + postgresqlPosition(31));
				break;
			}
			default:
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testGroupByInvalidOrderBy()
	{
		final Query<String> query = new Query<>(string);
		query.setGroupBy(string);
		query.setOrderBy(integer, true);

		final String table = getTableName(TYPE);
		final String column = getColumnName(integer);

		assertEquals(4, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT \"string\" FROM " + SI.tab(TYPE) + " " +
								"GROUP BY \"string\" ORDER BY \"integer\"]"));
				break;
			case mysql:
				notAllowed(query,
						atLeastMysql57()
						?
						"Expression #1 of ORDER BY clause is not in GROUP BY clause and " +
						"contains nonaggregated column '" + dbCat() + ".AnItem.integer' " +
						"which is not functionally dependent on columns in GROUP BY clause; " +
						"this is incompatible with sql_mode=only_full_group_by"
						:
						"'" + dbCat() + "." + table + "." + column + "' isn't in GROUP BY");
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function" +
						postgresqlPosition(58));
				break;
			default:
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testDistinctInvalidOrderBy()
	{
		final Query<String> query = new Query<>(string);
		query.setDistinct(true);
		query.setOrderBy(integer, true);

		assertEquals(4, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT \"string\" FROM " + SI.tab(TYPE) + " " +
								"ORDER BY \"integer\"]"));
				break;
			case mysql:
				if(atLeastMysql57())
					notAllowed(query,
							"Expression #1 of ORDER BY clause is not in SELECT list, " +
							"references column '" + dbCat() + ".AnItem.integer' " +
							"which is not in SELECT list; this is incompatible with DISTINCT");
				else
					assertContains("foo", "bar", "goo", "car", query.search());
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list" +
						postgresqlPosition(49));
				break;
			default:
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	/**
	 * DISTINCT clause is the same as the GROUP BY clause.
	 */
	@Test void testDistinctSame()
	{
		final Query<String> query = new Query<>(string);
		query.setGroupBy(string);

		assertContains("bar", "car", "foo", "goo", query.search());
		assertEquals(4, query.total());
		assertTrue(query.exists());

		query.setDistinct(true);
		assertContains("bar", "car", "foo", "goo", query.search());
		assertEquals(4, query.total());
		assertTrue(query.exists());
	}

	/**
	 * DISTINCT clause has fewer columns than the GROUP BY clause,
	 * DISTINCT is stricter and wins.
	 */
	@Test void testDistinctFewer()
	{
		final Query<String> query = new Query<>(string);
		query.setGroupBy(string, integer);

		assertContains("foo", "foo", "foo", "bar", "bar", "goo", "car", "car", query.search());
		assertEquals(8, query.total());
		assertTrue(query.exists());

		query.setDistinct(true);
		assertContains("bar", "car", "foo", "goo", query.search());
		assertEquals(4, query.total());
		assertTrue(query.exists());
	}

	/**
	 * DISTINCT clause has more columns than the GROUP BY clause,
	 * GROUP BY is stricter and wins.
	 */
	@Test void testDistinctMore()
	{
		final Query<List<Object>> query = Query.newQuery(new Selectable<?>[]{string, integer}, TYPE, null);
		query.setGroupBy(string);
		query.setDistinct(true);

		final String table = getTableName(TYPE);
		final String column = getColumnName(integer);
		switch(dialect)
		{
			case hsqldb:
			{
				final String message =
						"expression not in aggregate or GROUP BY columns: " +
						"PUBLIC.\"" + table + "\".\"" + column + "\"" + ifPrep(" in statement ");
				notAllowed(query, message + ifPrep("[SELECT DISTINCT \"string\",\"integer\" FROM " + SI.tab(TYPE) + " GROUP BY \"string\"]"));
				notAllowedTotal(query, message + ifPrep("[SELECT COUNT(*) FROM ( SELECT DISTINCT \"string\",\"integer\" FROM " + SI.tab(TYPE) + " GROUP BY \"string\" )]"));
				break;
			}
			case mysql:
			{
				final String message =
						atLeastMysql57()
						?
						"Expression #2 of SELECT list is not in GROUP BY clause and " +
						"contains nonaggregated column '" + dbCat() + ".AnItem.integer' " +
						"which is not functionally dependent on columns in GROUP BY clause; " +
						"this is incompatible with sql_mode=only_full_group_by"
						:
						"'" + dbCat() + "." + table + "." + column + "' isn't in GROUP BY";

				notAllowed(query, message);
				notAllowedTotal(query, message);
				break;
			}
			case postgresql:
			{
				final String message =
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function";
				restartTransaction();
				notAllowed(query, message + postgresqlPosition(26));
				restartTransaction();
				notAllowedTotal(query, message + postgresqlPosition(49));
				break;
			}
			default:
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	private static void assertCount(final Query<?> items, final int expectedSize, final int expectedTotal)
	{
		assertEquals(expectedSize, items.search().size());
		assertEquals(expectedTotal, items.total());
		assertEquals(expectedTotal>0, items.exists());
	}

	@SuppressWarnings("HardcodedLineSeparator") // OK: newline in sql error
	static final String postgresqlPosition(final int value)
	{
		return "\n" + "  Position: " + value;
	}

	@WrapperType(indent=2, comments=false)
	static final class AnItem extends Item
	{
		static final StringField string = new StringField().toFinal();
		static final IntegerField integer = new IntegerField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem(
					@javax.annotation.Nonnull final java.lang.String string,
					final int integer)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.string,string),
				com.exedio.cope.SetValue.map(AnItem.integer,integer),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getString()
		{
			return AnItem.string.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getInteger()
		{
			return AnItem.integer.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

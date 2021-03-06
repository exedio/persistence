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

import com.exedio.cope.tojunit.SI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator") // OK: newline in sql error
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
		final EnvironmentInfo env = model.getEnvironmentInfo();

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
				final String message;
				if(env.isDatabaseVersionAtLeast(5, 7))
					message =
							"Expression #1 of SELECT list is not in GROUP BY clause and " +
							"contains nonaggregated column '" + env.getCatalog() + ".AnItem.integer' " +
							"which is not functionally dependent on columns in GROUP BY clause; " +
							"this is incompatible with sql_mode=only_full_group_by";
				else
					message =
							"'" + env.getCatalog() + "." + table + "." + column + "' isn't in GROUP BY";

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
				throw new RuntimeException("" + dialect);
		}
	}

	@Test void testGroupByInvalidOrderBy()
	{
		final Query<String> query = new Query<>(string);
		query.setGroupBy(string);
		query.setOrderBy(integer, true);

		final String table = getTableName(TYPE);
		final String column = getColumnName(integer);
		final EnvironmentInfo env = model.getEnvironmentInfo();

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
				if(env.isDatabaseVersionAtLeast(5, 7))
					notAllowed(query,
							"Expression #1 of ORDER BY clause is not in GROUP BY clause and " +
							"contains nonaggregated column '" + env.getCatalog() + ".AnItem.integer' " +
							"which is not functionally dependent on columns in GROUP BY clause; " +
							"this is incompatible with sql_mode=only_full_group_by");
				else if(env.isDatabaseVersionAtLeast(5, 6))
					notAllowed(query,
							"'" + env.getCatalog() + "." + table + "." + column + "' isn't in GROUP BY");
				else
					assertContains("foo", "bar", "goo", "car", query.search());
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function" +
						postgresqlPosition(58));
				break;
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	@Test void testDistinctInvalidOrderBy()
	{
		final Query<String> query = new Query<>(string);
		query.setDistinct(true);
		query.setOrderBy(integer, true);

		final EnvironmentInfo env = model.getEnvironmentInfo();

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
				if(env.isDatabaseVersionAtLeast(5, 7))
					notAllowed(query,
							"Expression #1 of ORDER BY clause is not in SELECT list, " +
							"references column '" + env.getCatalog() + ".AnItem.integer' " +
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
				throw new RuntimeException("" + dialect);
		}
	}

	private static void assertCount(final Query<?> items, final int expectedSize, final int expectedTotal)
	{
		assertEquals(expectedSize, items.search().size());
		assertEquals(expectedTotal, items.total());
		assertEquals(expectedTotal>0, items.exists());
	}

	static final String postgresqlPosition(final int value)
	{
		return "\n" + "  Position: " + value;
	}

	static final class AnItem extends Item
	{
		static final StringField string = new StringField().toFinal();
		static final IntegerField integer = new IntegerField().toFinal();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param integer the initial value for field {@link #integer}.
	 * @throws com.exedio.cope.MandatoryViolationException if string is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nonnull final java.lang.String string,
				final int integer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.string.map(string),
			AnItem.integer.map(integer),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getString()
	{
		return AnItem.string.get(this);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getInteger()
	{
		return AnItem.integer.getMandatory(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}

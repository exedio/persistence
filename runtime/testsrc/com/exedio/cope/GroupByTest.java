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

import static com.exedio.cope.DistinctOrderByTest.notAllowedEquals;
import static com.exedio.cope.DistinctOrderByTest.notAllowedStartsWith;
import static com.exedio.cope.GroupByTest.AnItem.TYPE;
import static com.exedio.cope.GroupByTest.AnItem.integer;
import static com.exedio.cope.GroupByTest.AnItem.string;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.junit.CopeAssert.assertContains;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GroupByTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public GroupByTest()
	{
		super(MODEL);
	}

	@SuppressWarnings("static-method")
	@Before public final void setUp()
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

	@Test public void testSimpleCount()
	{
		final Query<AnItem> items = TYPE.newQuery();
		assertCount(items, 8, 8);
	}

	@Test public void testSimpleCountWithLimit()
	{
		final Query<AnItem> items = TYPE.newQuery();
		items.setLimit(0, 3);
		assertCount(items, 3, 8);
	}

	@Test public void testGroupByCount()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{string, integer.min()};
		final Query<List<Object>> items = Query.newQuery(selection, TYPE, null);
		items.setGroupBy(string);
		assertCount(items, 4, 4);
	}

	@Test public void testGroupByCountWithLimit()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{string, integer.min()};
		final Query<List<Object>> items = Query.newQuery(selection, TYPE, null);
		items.setGroupBy(string);
		items.setLimit(0, 3);
		assertCount(items, 3, 4);
	}

	@Test public void testGroupByInvalidSelect() throws SQLException
	{
		final Query<Integer> query = new Query<>(integer);
		query.setGroupBy(string);

		final String table = getTableName(TYPE);
		final String column = getColumnName(integer);

		switch(dialect)
		{
			case hsqldb:
				notAllowedEquals(query,
						"expression not in aggregate or GROUP BY columns: " +
						"PUBLIC.\"" + table + "\".\"" + column + "\"");
				break;
			case mysql:
				notAllowedStartsWith(query,
						"'" + catalog() + "." + table + "." + column + "' isn't in GROUP BY");
				break;
			case oracle:
				notAllowedStartsWith(query,
						"ORA-00979: ");
				break;
			case postgresql:
				notAllowedStartsWith(query,
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function");
				break;
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	@Test public void testGroupByInvalidOrderBy()
	{
		final Query<String> query = new Query<>(string);
		query.setGroupBy(string);
		query.setOrderBy(integer, true);

		final String table = getTableName(TYPE);
		final String column = getColumnName(integer);

		switch(dialect)
		{
			case hsqldb:
				notAllowedEquals(query,
						"invalid ORDER BY expression");
				break;
			case mysql:
				assertContains("foo", "bar", "goo", "car", query.search());
				break;
			case oracle:
				notAllowedStartsWith(query,
						"ORA-00979: ");
				break;
			case postgresql:
				notAllowedStartsWith(query,
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function");
				break;
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	private static void assertCount(final Query<?> items, final int expectedSize, final int expectedTotal)
	{
		assertEquals(expectedSize, items.search().size());
		assertEquals(expectedTotal, items.total());
	}

	private String catalog() throws SQLException
	{
		model.commit();
		final String result;
		try(Connection con = newConnection(model))
		{
			result = con.getCatalog();
		}
		model.startTransaction(GroupByTest.class.getName());
		return result;
	}

	static final class AnItem extends Item
	{
		static final StringField string = new StringField().toFinal();
		static final IntegerField integer = new IntegerField().toFinal();

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param integer the initial value for field {@link #integer}.
	 * @throws com.exedio.cope.MandatoryViolationException if string is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				final java.lang.String string,
				final int integer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.string.map(string),
			AnItem.integer.map(integer),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #string}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getString()
	{
		return AnItem.string.get(this);
	}/**

	 **
	 * Returns the value of {@link #integer}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final int getInteger()
	{
		return AnItem.integer.getMandatory(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}

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

import static com.exedio.cope.OverflowIntegerSumTest.AnItem.TYPE;
import static com.exedio.cope.OverflowIntegerSumTest.AnItem.field;
import static com.exedio.cope.SchemaInfo.newConnection;
import static java.lang.Integer.MAX_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.tojunit.SI;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;

public class OverflowIntegerSumTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public OverflowIntegerSumTest()
	{
		super(MODEL);
	}

	private static final Query<Integer> query = new Query<>(field.sum());

	@Test public void testIt() throws SQLException
	{
		assertEquals(null, query.searchSingleton());

		new AnItem(42);
		assertIt(42);

		new AnItem(MAX_VALUE - 42);
		assertIt(MAX_VALUE);

		new AnItem(1);
		assertIt(1l + MAX_VALUE);

		new AnItem(5);
		assertIt(6l + MAX_VALUE);
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private void assertIt(final long expected) throws SQLException
	{
		MODEL.commit();

		try(
			Connection c = newConnection(MODEL);
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(
				"SELECT SUM(" + SI.col(field) + ") " + "FROM " + SI.tab(TYPE)))
		{
			assertTrue(rs.next());

			if(expected<=MAX_VALUE)
			{
				assertEquals(expected, rs.getInt(1));
			}
			else
			{
				try
				{
					rs.getInt(1);
					fail();
				}
				catch(final SQLException ignored)
				{
					//System.out.println(e.getMessage());
				}
			}

			assertEquals(expected, rs.getLong(1));

			final Object expectedObject;
			switch(dialect)
			{
				case hsqldb:
				case postgresql:
					expectedObject = Long.valueOf(expected);
					break;
				case mysql:
				case oracle:
					expectedObject = BigDecimal.valueOf(expected);
					break;
				default:
					throw new RuntimeException("" + dialect);
			}
			assertEquals(expectedObject, rs.getObject(1));
		}

		MODEL.startTransaction(OverflowIntegerSumTest.class.getName());

		if(expected<=MAX_VALUE)
		{
			assertEquals(expected, query.searchSingleton().intValue());
		}
		else
		{
			try
			{
				query.searchSingleton();
				fail();
			}
			catch(final ArithmeticException e)
			{
				final String expectedArithmeticException;
				switch(dialect)
				{
					case hsqldb:
					case postgresql:
						expectedArithmeticException = "integer overflow"; // from Math#toIntExact
						break;
					case mysql:
					case oracle:
						expectedArithmeticException = "Overflow"; // from BigDecimal#intValueExact
						break;
					default:
						throw new RuntimeException("" + dialect, e);
				}

				assertEquals(expectedArithmeticException, e.getMessage());
			}
		}
	}


	static final class AnItem extends Item
	{
		static final IntegerField field = new IntegerField().toFinal();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				final int field)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.field.map(field),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getField()
	{
		return AnItem.field.getMandatory(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}

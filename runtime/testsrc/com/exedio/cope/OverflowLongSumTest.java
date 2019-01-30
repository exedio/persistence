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

import static com.exedio.cope.OverflowLongSumTest.AnItem.TYPE;
import static com.exedio.cope.OverflowLongSumTest.AnItem.field;
import static com.exedio.cope.SchemaInfo.newConnection;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.junit.jupiter.api.Test;

public class OverflowLongSumTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public OverflowLongSumTest()
	{
		super(MODEL);
	}

	private static final Query<Long> query = new Query<>(field.sum());

	@Test void testIt() throws SQLException
	{
		assertEquals(null, query.searchSingleton());

		new AnItem(42);
		assertIt(42);

		new AnItem(MAX_VALUE - 42);
		assertIt(MAX_VALUE);

		new AnItem(1);
		assertIt(MAX_VALUE, 1);

		new AnItem(5);
		assertIt(MAX_VALUE, 6);
	}

	private void assertIt(final long expected) throws SQLException
	{
		assertIt(BigDecimal.valueOf(expected));
	}

	private void assertIt(final long expected1, final long expected2) throws SQLException
	{
		assertIt(BigDecimal.valueOf(expected1).add(BigDecimal.valueOf(expected2)));
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private void assertIt(final BigDecimal expected) throws SQLException
	{
		MODEL.commit();

		long expectedLong = MIN_VALUE;
		boolean expectedIsLong = false;
		try
		{
			expectedLong = expected.longValueExact();
			expectedIsLong = true;
		}
		catch(final ArithmeticException ignored)
		{
			// ok
		}

		try(
			Connection c = newConnection(MODEL);
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(
				"SELECT SUM(" + SI.col(field) + ") FROM " + SI.tab(TYPE)))
		{
			assertTrue(rs.next());

			if(expectedIsLong)
			{
				assertEquals(expectedLong, rs.getLong(1));
			}
			else
			{
				try
				{
					rs.getLong(1);
					fail();
				}
				catch(final SQLException ignored)
				{
					//System.out.println(e.getMessage());
				}
			}

			assertEquals(expected, rs.getObject(1));
		}

		MODEL.startTransaction(OverflowLongSumTest.class.getName());

		if(expectedIsLong)
		{
			assertEquals(expectedLong, query.searchSingleton().longValue());
		}
		else
		{
			try
			{
				query.searchSingleton();
				fail();
			}
			catch(final SQLRuntimeException e)
			{
				final String expectedArithmeticException;
				switch(dialect)
				{
					case hsqldb:
						expectedArithmeticException = "incompatible data type in conversion: from SQL type DECIMAL to java.lang.Long, value: " + expected;
						break;
					case mysql:
						if(mariaDriver)
							expectedArithmeticException = "Out of range value for column 'SUM(`field`)' : value " + expected;
						else
							expectedArithmeticException = "Value '" + mysqlFormat(expected) + "' is outside of valid range for type java.lang.Long";
						break;
					case oracle:
						expectedArithmeticException = "Numeric Overflow";
						break;
					case postgresql:
						expectedArithmeticException = "Bad value for type long : " + expected;
						break;
					default:
						throw new RuntimeException("" + dialect, e);
				}

				assertEquals(expectedArithmeticException, e.getCause().getMessage());
			}
		}
	}

	private static String mysqlFormat(final BigDecimal v)
	{
		final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
		nfs.setDecimalSeparator('.');
		nfs.setGroupingSeparator(',');
		return new DecimalFormat("", nfs).format(v);
	}


	static final class AnItem extends Item
	{
		static final LongField field = new LongField().toFinal();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				final long field)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.field.map(field),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	long getField()
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
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}

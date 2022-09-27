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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.SQLRuntimeException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.junit.jupiter.api.Test;

public class OverflowIntegerSumTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public OverflowIntegerSumTest()
	{
		super(MODEL);
	}

	private static final Query<Integer> query = new Query<>(field.sum());

	@Test void testIt() throws SQLException
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

	private void assertIt(final long expected) throws SQLException
	{
		MODEL.commit();

		try(
			Connection c = newConnection(MODEL);
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(
				"SELECT SUM(" + SI.col(field) + ") FROM " + SI.tab(TYPE)))
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
					final int illegalResult = rs.getInt(1);
					fail("illegal result " + illegalResult + " for expected " + expected);
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
				final Integer illegalResult = query.searchSingleton();
				fail("illegal result " + illegalResult + " for expected " + expected);
			}
			catch(final SQLRuntimeException e)
			{
				final String expectedArithmeticException;
				switch(dialect)
				{
					case hsqldb:
						expectedArithmeticException = "incompatible data type in conversion: from SQL type BIGINT to java.lang.Integer, value: " + expected;
						break;
					case mysql:
						if(mariaDriver)
							expectedArithmeticException = "integer overflow";
						else
							expectedArithmeticException = "Value '" + mysqlFormat(expected) + "' is outside of valid range for type java.lang.Integer";
						break;
					case postgresql:
						expectedArithmeticException = "Bad value for type int : " + expected;
						break;
					default:
						throw new RuntimeException("" + dialect, e);
				}

				assertEquals(expectedArithmeticException, e.getCause().getMessage());
			}
		}
	}

	private static String mysqlFormat(final long v)
	{
		final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
		nfs.setDecimalSeparator('.');
		nfs.setGroupingSeparator(',');
		return new DecimalFormat("", nfs).format(v);
	}


	static final class AnItem extends Item
	{
		static final IntegerField field = new IntegerField().toFinal();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getField()
	{
		return AnItem.field.getMandatory(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}

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

import static com.exedio.cope.DatePrecisionConditionTest.date;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraints;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Table;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class DatePrecisionSchemaViolationTest extends SchemaMismatchTest
{
	public DatePrecisionSchemaViolationTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		assertEquals(name(ItemA.TYPE   ), name(ItemB.TYPE   ));
		assertEquals(name(ItemA.hours  ), name(ItemB.hours  ));
		assertEquals(name(ItemA.minutes), name(ItemB.minutes));
		assertEquals(name(ItemA.seconds), name(ItemB.seconds));

		final Date ok      = date(9, 15,  0,  0,  0);
		final Date minutes = date(9, 15, 44,  0,  0);
		final Date seconds = date(9, 15,  0, 55,  0);
		final Date millis  = date(9, 15,  0,  0, 66);

		newItemOk(ok, ok, ok);

		newItemBad(minutes, ok, ok, "ItemAB_hours_PM", "ItemAB_hours_PR", 1, 1, 0,  0, 0);
		newItemBad(seconds, ok, ok, "ItemAB_hours_PS", "ItemAB_hours_PR", 2, 1, 1,  0, 0);
		newItemBad(millis , ok, ok, "ItemAB_hours_PS", "ItemAB_hours_PR", 3, 1, 2,  0, 0);

		newItemOk (ok, minutes, ok);
		newItemBad(ok, seconds, ok, "ItemAB_minutes_PS", "ItemAB_minutes_PR", 3, 1, 2,  1, 0);
		newItemBad(ok, millis , ok, "ItemAB_minutes_PS", "ItemAB_minutes_PR", 3, 1, 2,  2, 0);

		newItemOk (ok, ok, minutes);
		newItemOk (ok, ok, seconds);
		newItemBad(ok, ok, millis , "ItemAB_seconds_PS", "ItemAB_seconds_PR", 3, 1, 2,  2, 1);
	}

	private static void newItemOk(
			final Date hours,
			final Date minutes,
			final Date seconds)
	{
		try(TransactionTry tx = modelB.startTransactionTry("itemB ok"))
		{
			new ItemB(hours, minutes, seconds);
			tx.commit();
		}
	}

	@SuppressWarnings("HardcodedLineSeparator") // OK: newline in sql error
	private void newItemBad(
			final Date hours,
			final Date minutes,
			final Date seconds,
			final String constraintNameNative,
			final String constraintNameInteger,
			final int hoursPRCheck, final int hoursPMCheck, final int hoursPSCheck,
			final int minutesCheck,
			final int secondsCheck)
	{
		final boolean sNative = supportsNativeDate(model);
		final String constraintName =
				sNative
				? constraintNameNative
				: constraintNameInteger;

		final String tableName = getTableName(ItemB.TYPE);

		try(TransactionTry tx = modelB.startTransactionTry("itemB wrong " + constraintName))
		{
			if(supportsCheckConstraints(modelB))
			{
				try
				{
					new ItemB(hours, minutes, seconds);
					fail("should fail");
				}
				catch(final SQLRuntimeException e)
				{
					final String message = e.getCause().getMessage();
					switch(dialect)
					{
						case hsqldb:
							assertEquals(
									"integrity constraint violation: " +
									"check constraint; " + constraintName + " " +
									"table: " + tableName,
									message);
							break;
						case oracle:
							assertEquals(
									"ORA-02290: " +
									"check constraint (" + schema() + "." + constraintName + ") violated\n",
									message);
							break;
						case postgresql:
							assertTrue(
									message.startsWith(
									"ERROR: new row for relation \"" + tableName + "\" " +
									"violates check constraint \"" + constraintName + "\"\n"),
									message);
							break;

						case mysql: // MySQL does not support check constraints
						default:
							throw new RuntimeException("" + dialect + '/' + message);

					}
				}
			}
			else
			{
				new ItemB(hours, minutes, seconds);
				tx.commit();

				final Table table = modelA.getSchema().getTable(tableName);
				assertNotNull(table);
				assertIt(!sNative, hoursPRCheck, table, "ItemAB_hours_PR"  );
				assertIt(!sNative, minutesCheck, table, "ItemAB_minutes_PR");
				assertIt(!sNative, secondsCheck, table, "ItemAB_seconds_PR");
				assertIt( sNative, hoursPMCheck, table, "ItemAB_hours_PM"  );
				assertIt( false,   -1,           table, "ItemAB_minutes_PM");
				assertIt( false,   -1,           table, "ItemAB_seconds_PM");
				assertIt( sNative, hoursPSCheck, table, "ItemAB_hours_PS"  );
				assertIt( sNative, minutesCheck, table, "ItemAB_minutes_PS");
				assertIt( sNative, secondsCheck, table, "ItemAB_seconds_PS");
			}
		}
	}

	private static void assertIt(
			final boolean exists,
			final int expected,
			final Table table,
			final String constraintName)
	{
		final Constraint constraint = table.getConstraint(constraintName);
		if(exists)
			assertEquals(expected, constraint.checkL(), constraintName);
		else
			assertNull(constraint, constraintName);
	}

	private static String schema()
	{
		return modelB.getConnectProperties().getConnectionUsername().toUpperCase(Locale.ENGLISH);
	}

	@CopeName("ItemAB")
	static final class ItemA extends Item
	{
		// toFinal to avoid update counter
		static final DateField hours   = new DateField().toFinal().precisionHour  ();
		static final DateField minutes = new DateField().toFinal().precisionMinute();
		static final DateField seconds = new DateField().toFinal().precisionSecond();

	/**
	 * Creates a new ItemA with all the fields initially needed.
	 * @param hours the initial value for field {@link #hours}.
	 * @param minutes the initial value for field {@link #minutes}.
	 * @param seconds the initial value for field {@link #seconds}.
	 * @throws com.exedio.cope.DatePrecisionViolationException if hours, minutes, seconds violates its precision constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if hours, minutes, seconds is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemA(
				@javax.annotation.Nonnull final java.util.Date hours,
				@javax.annotation.Nonnull final java.util.Date minutes,
				@javax.annotation.Nonnull final java.util.Date seconds)
			throws
				com.exedio.cope.DatePrecisionViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemA.hours.map(hours),
			ItemA.minutes.map(minutes),
			ItemA.seconds.map(seconds),
		});
	}

	/**
	 * Creates a new ItemA and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemA(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #hours}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getHours()
	{
		return ItemA.hours.get(this);
	}

	/**
	 * Returns the value of {@link #minutes}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getMinutes()
	{
		return ItemA.minutes.get(this);
	}

	/**
	 * Returns the value of {@link #seconds}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getSeconds()
	{
		return ItemA.seconds.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemA.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@CopeName("ItemAB")
	static final class ItemB extends Item
	{
		// toFinal to avoid update counter
		static final DateField hours   = new DateField().toFinal();
		static final DateField minutes = new DateField().toFinal();
		static final DateField seconds = new DateField().toFinal();

	/**
	 * Creates a new ItemB with all the fields initially needed.
	 * @param hours the initial value for field {@link #hours}.
	 * @param minutes the initial value for field {@link #minutes}.
	 * @param seconds the initial value for field {@link #seconds}.
	 * @throws com.exedio.cope.MandatoryViolationException if hours, minutes, seconds is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemB(
				@javax.annotation.Nonnull final java.util.Date hours,
				@javax.annotation.Nonnull final java.util.Date minutes,
				@javax.annotation.Nonnull final java.util.Date seconds)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemB.hours.map(hours),
			ItemB.minutes.map(minutes),
			ItemB.seconds.map(seconds),
		});
	}

	/**
	 * Creates a new ItemB and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemB(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #hours}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getHours()
	{
		return ItemB.hours.get(this);
	}

	/**
	 * Returns the value of {@link #minutes}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getMinutes()
	{
		return ItemB.minutes.get(this);
	}

	/**
	 * Returns the value of {@link #seconds}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getSeconds()
	{
		return ItemB.seconds.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemB.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);

}

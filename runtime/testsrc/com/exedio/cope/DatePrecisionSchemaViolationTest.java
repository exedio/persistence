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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.dsmf.SQLRuntimeException;
import java.util.Date;
import java.util.Locale;
import org.junit.Test;

public class DatePrecisionSchemaViolationTest extends SchemaMismatchTest
{
	public DatePrecisionSchemaViolationTest()
	{
		super(modelA, modelB);
	}

	@Test public void testIt()
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

		newItemBad(minutes, ok, ok, "ItemAB_hours_PM", "ItemAB_hours_PR");
		newItemBad(seconds, ok, ok, "ItemAB_hours_PS", "ItemAB_hours_PR");
		newItemBad(millis , ok, ok, "ItemAB_hours_PS", "ItemAB_hours_PR");

		newItemOk (ok, minutes, ok);
		newItemBad(ok, seconds, ok, "ItemAB_minutes_PS", "ItemAB_minutes_PR");
		newItemBad(ok, millis , ok, "ItemAB_minutes_PS", "ItemAB_minutes_PR");

		newItemOk (ok, ok, minutes);
		newItemOk (ok, ok, seconds);
		newItemBad(ok, ok, millis , "ItemAB_seconds_PS", "ItemAB_seconds_PR");
	}

	private static ItemB newItemOk(
			final Date hours,
			final Date minutes,
			final Date seconds)
	{
		try(TransactionTry tx = modelB.startTransactionTry("itemB ok"))
		{
			return tx.commit(
					new ItemB(hours, minutes, seconds));
		}
	}

	private ItemB newItemBad(
			final Date hours,
			final Date minutes,
			final Date seconds,
			final String contraintNameNative,
			final String contraintNameInteger)
	{
		final String contraintName =
				supportsNativeDate(model)
				? contraintNameNative
				: contraintNameInteger;

		final String tableName = getTableName(ItemB.TYPE);

		try(TransactionTry tx = modelB.startTransactionTry("itemB wrong " + contraintName))
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
									"check constraint; " + contraintName + " " +
									"table: " + tableName,
									message);
							break;
						case oracle:
							assertEquals(
									"ORA-02290: " +
									"check constraint (" + schema() + "." + contraintName + ") violated\n",
									message);
							break;
						case postgresql:
							assertTrue(
									message,
									message.startsWith(
									"ERROR: new row for relation \"" + tableName + "\" " +
									"violates check constraint \"" + contraintName + "\"\n"));
							break;

						case mysql: // MySQL does not support check constraints
						default:
							throw new RuntimeException("" + dialect + '/' + message);

					}
				}
				return null;
			}
			else
			{
				return tx.commit(
						new ItemB(hours, minutes, seconds));
			}
		}
	}

	private static String schema()
	{
		return modelB.getConnectProperties().getConnectionUsername().toUpperCase(Locale.ENGLISH);
	}

	@CopeName("ItemAB")
	static final class ItemA extends Item
	{
		// toFinal to avoid update counter
		static final DateField hours   = new DateField().toFinal().hours  ();
		static final DateField minutes = new DateField().toFinal().minutes();
		static final DateField seconds = new DateField().toFinal().seconds();

		/**

	 **
	 * Creates a new ItemA with all the fields initially needed.
	 * @param hours the initial value for field {@link #hours}.
	 * @param minutes the initial value for field {@link #minutes}.
	 * @param seconds the initial value for field {@link #seconds}.
	 * @throws com.exedio.cope.MandatoryViolationException if hours, minutes, seconds is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	ItemA(
				final java.util.Date hours,
				final java.util.Date minutes,
				final java.util.Date seconds)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemA.hours.map(hours),
			ItemA.minutes.map(minutes),
			ItemA.seconds.map(seconds),
		});
	}/**

	 **
	 * Creates a new ItemA and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private ItemA(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #hours}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getHours()
	{
		return ItemA.hours.get(this);
	}/**

	 **
	 * Returns the value of {@link #minutes}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getMinutes()
	{
		return ItemA.minutes.get(this);
	}/**

	 **
	 * Returns the value of {@link #seconds}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getSeconds()
	{
		return ItemA.seconds.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for itemA.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	@CopeName("ItemAB")
	static final class ItemB extends Item
	{
		// toFinal to avoid update counter
		static final DateField hours   = new DateField().toFinal();
		static final DateField minutes = new DateField().toFinal();
		static final DateField seconds = new DateField().toFinal();

		/**

	 **
	 * Creates a new ItemB with all the fields initially needed.
	 * @param hours the initial value for field {@link #hours}.
	 * @param minutes the initial value for field {@link #minutes}.
	 * @param seconds the initial value for field {@link #seconds}.
	 * @throws com.exedio.cope.MandatoryViolationException if hours, minutes, seconds is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	ItemB(
				final java.util.Date hours,
				final java.util.Date minutes,
				final java.util.Date seconds)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemB.hours.map(hours),
			ItemB.minutes.map(minutes),
			ItemB.seconds.map(seconds),
		});
	}/**

	 **
	 * Creates a new ItemB and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private ItemB(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #hours}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getHours()
	{
		return ItemB.hours.get(this);
	}/**

	 **
	 * Returns the value of {@link #minutes}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getMinutes()
	{
		return ItemB.minutes.get(this);
	}/**

	 **
	 * Returns the value of {@link #seconds}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getSeconds()
	{
		return ItemB.seconds.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for itemB.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);

}

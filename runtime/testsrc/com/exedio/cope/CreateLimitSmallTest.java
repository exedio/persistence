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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CreateLimitSmallTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	public CreateLimitSmallTest()
	{
		super(MODEL);
	}

	@Test void testIt() throws NoSuchIDException
	{
		final AnItem i5;
		assertEquals(0, getPrimaryKeyColumnValueL(new AnItem(0)));
		assertEquals(1, getPrimaryKeyColumnValueL(new AnItem(1)));
		assertEquals(2, getPrimaryKeyColumnValueL(new AnItem(2)));
		assertEquals(3, getPrimaryKeyColumnValueL(new AnItem(3)));
		assertEquals(4, getPrimaryKeyColumnValueL(new AnItem(4)));
		assertEquals(5, getPrimaryKeyColumnValueL(i5 = new AnItem(5)));
		try
		{
			new AnItem(6);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"sequence overflow to 6 in AnItem.this limited to 0,5",
					e.getMessage());
		}
		try
		{
			new AnItem(7);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"sequence overflow to 7 in AnItem.this limited to 0,5", // TODO should not increase further
					e.getMessage());
		}

		assertEquals(i5, model.getItem("AnItem-5"));
		assertIDFails("AnItem-6", "must be less or equal 5", true);
	}

	@CopeCreateLimit(5)
	private static final class AnItem extends Item
	{
		static final IntegerField number = new IntegerField();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param number the initial value for field {@link #number}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private AnItem(
				final int number)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.number.map(number),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #number}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getNumber()
	{
		return AnItem.number.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #number}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNumber(final int number)
	{
		AnItem.number.set(this,number);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}

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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Price.valueOf;
import static com.exedio.cope.pattern.SettableFinalPriceTest.AnItem.TYPE;
import static com.exedio.cope.pattern.SettableFinalPriceTest.AnItem.isfinal;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class SettableFinalPriceTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public SettableFinalPriceTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final AnItem item = new AnItem(valueOf(3.3));
		final SetValue<?> setValue = isfinal.map(valueOf(5.5));

		try
		{
			item.set(setValue);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(isfinal, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("final violation on " + item + " for AnItem.isfinal", e.getMessage());
		}
		assertEquals(valueOf(3.3), item.getIsfinal());
	}


	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final PriceField isfinal = new PriceField().toFinal().optional();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param isfinal the initial value for field {@link #isfinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				@javax.annotation.Nullable final com.exedio.cope.pattern.Price isfinal)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.isfinal.map(isfinal),
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
	 * Returns the value of {@link #isfinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Price getIsfinal()
	{
		return AnItem.isfinal.get(this);
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

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
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


	static final class AnItem extends Item
	{
		static final PriceField isfinal = new PriceField().toFinal().optional();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param isfinal the initial value for field {@link #isfinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #isfinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Price getIsfinal()
	{
		return AnItem.isfinal.get(this);
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

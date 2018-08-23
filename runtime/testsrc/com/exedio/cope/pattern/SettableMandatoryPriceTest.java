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
import static com.exedio.cope.pattern.SettableMandatoryPriceTest.AnItem.TYPE;
import static com.exedio.cope.pattern.SettableMandatoryPriceTest.AnItem.mandatory;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class SettableMandatoryPriceTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public SettableMandatoryPriceTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final SetValue<?> setValue = mandatory.map(null);

		try
		{
			new AnItem(setValue);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for AnItem.mandatory", e.getMessage());
		}

		final AnItem item = new AnItem(valueOf(3.3));
		try
		{
			item.set(setValue);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for AnItem.mandatory", e.getMessage());
		}
		assertEquals(valueOf(3.3), item.getMandatory());
	}


	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final PriceField mandatory = new PriceField();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 * @throws com.exedio.cope.MandatoryViolationException if mandatory is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Price mandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.mandatory.map(mandatory),
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
	 * Returns the value of {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final com.exedio.cope.pattern.Price getMandatory()
	{
		return AnItem.mandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setMandatory(@javax.annotation.Nonnull final com.exedio.cope.pattern.Price mandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.mandatory.set(this,mandatory);
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

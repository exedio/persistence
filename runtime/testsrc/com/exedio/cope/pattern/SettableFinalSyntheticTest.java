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

import static com.exedio.cope.pattern.SettableFinalSyntheticTest.AnItem.TYPE;
import static com.exedio.cope.pattern.SettableFinalSyntheticTest.AnItem.isfinal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class SettableFinalSyntheticTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public SettableFinalSyntheticTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final AnItem item = new AnItem(3);
		final SetValue<?> setValue = SetValue.map(isfinal, 5);

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
		assertEquals(3, isfinal.source.getMandatory(item));
	}


	static final class AnItem extends Item
	{
		static final SettableSyntheticField isfinal = new SettableSyntheticField().toFinal();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param isfinal the initial value for field {@link #isfinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				final int isfinal)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.isfinal,isfinal),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
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

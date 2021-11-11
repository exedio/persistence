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

import static com.exedio.cope.pattern.SettableMandatorySyntheticTest.AnItem.TYPE;
import static com.exedio.cope.pattern.SettableMandatorySyntheticTest.AnItem.mandatory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class SettableMandatorySyntheticTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public SettableMandatorySyntheticTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final SetValue<?> setValue = SetValue.map(mandatory, null);

		try
		{
			new AnItem(setValue);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory.source, e.getFeature()); // TODO should be field itself
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for AnItem.mandatory-source", e.getMessage());
		}

		final AnItem item = new AnItem(3);
		try
		{
			item.set(setValue);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory.source, e.getFeature()); // TODO should be field itself
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for AnItem.mandatory-source", e.getMessage());
		}
		assertEquals(3, mandatory.source.getMandatory(item));
	}


	static final class AnItem extends Item
	{
		static final SettableSyntheticField mandatory = new SettableSyntheticField();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				final int mandatory)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.mandatory,mandatory),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

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

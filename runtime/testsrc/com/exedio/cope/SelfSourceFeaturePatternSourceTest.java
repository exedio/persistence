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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.instrument.WrapperType;
import java.io.Serial;
import org.junit.jupiter.api.Test;

public class SelfSourceFeaturePatternSourceTest
{
	@Test void test()
	{
		assertFails(
				() -> TypesBound.newType(MyItem.class, MyItem::new),
				IllegalStateException.class,
				"feature already mounted: MyItem.pattern");
	}

	private static final class MyPattern extends Pattern
	{
		MyPattern()
		{
			addSourceFeature(this, "source"); // This is nonsense !
		}

		@Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final MyPattern pattern = new MyPattern();

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

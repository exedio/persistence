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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ComputedTest
{
	@Test void testIt()
	{
		assertEquals(true,  comp(Super.TYPE));
		assertEquals(true,  comp(Sub.TYPE));
		assertEquals(false, comp(Single.TYPE));
	}

	@Computed
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Super extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Super> TYPE = com.exedio.cope.TypesBound.newType(Super.class,Super::new);

		@com.exedio.cope.instrument.Generated
		protected Super(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Sub extends Super
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Sub> TYPE = com.exedio.cope.TypesBound.newType(Sub.class,Sub::new);

		@com.exedio.cope.instrument.Generated
		protected Sub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Single extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Single> TYPE = com.exedio.cope.TypesBound.newType(Single.class,Single::new);

		@com.exedio.cope.instrument.Generated
		protected Single(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static boolean comp(final Type<?> f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}
}

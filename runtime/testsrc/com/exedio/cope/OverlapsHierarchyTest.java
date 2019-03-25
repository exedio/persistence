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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class OverlapsHierarchyTest
{
	@Test void testIt()
	{
		assertIt(true,  RefItem.fieldA,  RefItem.fieldB1);
		assertIt(true,  RefItem.fieldA,  RefItem.fieldB2);
		assertIt(true,  RefItem.fieldA,  RefItem.fieldC);
		assertIt(false, RefItem.fieldB1, RefItem.fieldB2);
		assertIt(true,  RefItem.fieldB1, RefItem.fieldC);
		assertIt(false, RefItem.fieldB2, RefItem.fieldC);
	}

	private static void assertIt(
			final boolean expected,
			final FunctionField<?> a,
			final FunctionField<?> b)
	{
		assertEquals(expected, a.overlaps(b));
		assertEquals(expected, b.overlaps(a));
		assertEquals(true, a.overlaps(a));
		assertEquals(true, b.overlaps(b));
		assertNotSame(a, b);
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItemA extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemA> TYPE = com.exedio.cope.TypesBound.newType(AnItemA.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItemB1 extends AnItemA
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemB1> TYPE = com.exedio.cope.TypesBound.newType(AnItemB1.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItemB1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItemB2 extends AnItemA
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemB2> TYPE = com.exedio.cope.TypesBound.newType(AnItemB2.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItemB2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItemC extends AnItemB1
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemC> TYPE = com.exedio.cope.TypesBound.newType(AnItemC.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItemC(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class RefItem extends Item
	{
		@WrapperIgnore static final ItemField<AnItemA > fieldA  = ItemField.create(AnItemA .class);
		@WrapperIgnore static final ItemField<AnItemB1> fieldB1 = ItemField.create(AnItemB1.class);
		@WrapperIgnore static final ItemField<AnItemB2> fieldB2 = ItemField.create(AnItemB2.class);
		@WrapperIgnore static final ItemField<AnItemC > fieldC  = ItemField.create(AnItemC .class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<RefItem> TYPE = com.exedio.cope.TypesBound.newType(RefItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private RefItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(AnItemA.TYPE, AnItemB1.TYPE, AnItemB2.TYPE, AnItemC.TYPE, RefItem.TYPE);
}

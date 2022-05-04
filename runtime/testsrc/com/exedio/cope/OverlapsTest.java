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

public class OverlapsTest
{
	@Test void testIt()
	{
		assertIt(false, AnItem1.bool,  AnItem1.item2);
		assertIt(false, AnItem1.bool,  AnItem1.intF);
		assertIt(false, AnItem1.bool,  AnItem1.longF);
		assertIt(false, AnItem1.bool,  AnItem1.date);
		assertIt(false, AnItem1.bool,  AnItem1.date);
		assertIt(false, AnItem1.enum1, AnItem1.enum2);
		assertIt(false, AnItem1.enum1, AnItem2.enum2);
		assertIt(false, AnItem1.item1, AnItem1.item2);
		assertIt(false, AnItem1.item1, AnItem2.item2);

		assertIt(true, AnItem1.bool,  AnItem2.bool);
		assertIt(true, AnItem1.intF,  AnItem2.intF);
		assertIt(true, AnItem1.longF, AnItem2.longF);
		assertIt(true, AnItem1.date,  AnItem2.date);
		assertIt(true, AnItem1.day,   AnItem2.day);
		assertIt(true, AnItem1.enum1, AnItem2.enum1);
		assertIt(true, AnItem1.enum2, AnItem2.enum2);
		assertIt(true, AnItem1.item1, AnItem2.item1);
		assertIt(true, AnItem1.item2, AnItem2.item2);
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
	private static final class AnItem1 extends Item
	{
		@WrapperIgnore static final BooleanField bool  = new BooleanField();
		@WrapperIgnore static final IntegerField intF  = new IntegerField();
		@WrapperIgnore static final LongField    longF = new LongField();
		@WrapperIgnore static final DateField    date  = new DateField();
		@WrapperIgnore static final DayField     day   = new DayField();

		@WrapperIgnore static final EnumField<AnEnum1> enum1 = EnumField.create(AnEnum1.class);
		@WrapperIgnore static final EnumField<AnEnum2> enum2 = EnumField.create(AnEnum2.class);
		@WrapperIgnore static final ItemField<AnItem1> item1 = ItemField.create(AnItem1.class);
		@WrapperIgnore static final ItemField<AnItem2> item2 = ItemField.create(AnItem2.class);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem1> TYPE = com.exedio.cope.TypesBound.newType(AnItem1.class,AnItem1::new);

		@com.exedio.cope.instrument.Generated
		private AnItem1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem2 extends Item
	{
		@WrapperIgnore static final BooleanField bool  = new BooleanField().toFinal().optional();
		@WrapperIgnore static final IntegerField intF  = new IntegerField().toFinal().optional();
		@WrapperIgnore static final LongField    longF = new LongField()   .toFinal().optional();
		@WrapperIgnore static final DateField    date  = new DateField()   .toFinal().optional();
		@WrapperIgnore static final DayField     day   = new DayField()    .toFinal().optional();

		@WrapperIgnore static final EnumField<AnEnum1> enum1 = EnumField.create(AnEnum1.class).toFinal().optional();
		@WrapperIgnore static final EnumField<AnEnum2> enum2 = EnumField.create(AnEnum2.class).toFinal().optional();
		@WrapperIgnore static final ItemField<AnItem1> item1 = ItemField.create(AnItem1.class).toFinal().optional();
		@WrapperIgnore static final ItemField<AnItem2> item2 = ItemField.create(AnItem2.class).toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem2> TYPE = com.exedio.cope.TypesBound.newType(AnItem2.class,AnItem2::new);

		@com.exedio.cope.instrument.Generated
		private AnItem2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum AnEnum1 {one,two}
	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum AnEnum2 {one,two}

	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(AnItem1.TYPE, AnItem2.TYPE);
}

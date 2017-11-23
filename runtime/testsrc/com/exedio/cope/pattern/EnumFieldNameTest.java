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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class EnumFieldNameTest
{
	@Test public void testIt()
	{
		assertEquals("set-normal"   , AnItem.set.getField(AnEnum.normal    ).getName());
		assertEquals("set-underline", AnItem.set.getField(AnEnum.under_line).getName());
		assertEquals("map-normal"   , AnItem.map.getField(AnEnum.normal    ).getName());
		assertEquals("map-underline", AnItem.map.getField(AnEnum.under_line).getName());
	}

	private enum AnEnum
	{
		normal,
		under_line
	}

	@WrapperIgnore
	private static final class AnItem extends Item
	{
		static final EnumSetField<AnEnum        > set = EnumSetField.create(AnEnum.class);
		static final EnumMapField<AnEnum, String> map = EnumMapField.create(AnEnum.class, new StringField());
		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused")
		private static final Type<?> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap){super(ap);}
	}
}

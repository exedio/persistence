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

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.EnumMapField;
import com.exedio.cope.pattern.LimitedListField;
import com.exedio.cope.pattern.ListField;
import com.exedio.cope.pattern.MapField;
import com.exedio.cope.pattern.SetField;
import org.junit.jupiter.api.Test;

public class MysqlExtendedVarcharTest
{
	@Test void testIt()
	{
		assertEquals(false, ext(MyItem.setWith.getParent()));
		assertEquals(false, ext(MyItem.setWith.getOrder()));
		assertEquals(true,  ext(MyItem.setWith.getElement()));
		assertEquals(false, ext(MyItem.setWithout.getParent()));
		assertEquals(false, ext(MyItem.setWithout.getOrder()));
		assertEquals(false, ext(MyItem.setWithout.getElement()));

		assertEquals(false, ext(MyItem.listWith.getParent()));
		assertEquals(false, ext(MyItem.listWith.getOrder()));
		assertEquals(true,  ext(MyItem.listWith.getElement()));
		assertEquals(false, ext(MyItem.listWithout.getParent()));
		assertEquals(false, ext(MyItem.listWithout.getOrder()));
		assertEquals(false, ext(MyItem.listWithout.getElement()));

		assertEquals(false, ext(MyItem.mapWith.getParent()));
		assertEquals(true,  ext(MyItem.mapWith.getKey()));
		assertEquals(true,  ext(MyItem.mapWith.getValue()));
		assertEquals(false, ext(MyItem.mapWithout.getParent()));
		assertEquals(false, ext(MyItem.mapWithout.getKey()));
		assertEquals(false, ext(MyItem.mapWithout.getValue()));

		assertEquals(false, ext(MyItem.enumSetWith   .getField(MyEnum.A))); // TODO bug: should be true
		assertEquals(false, ext(MyItem.enumSetWith   .getField(MyEnum.B))); // TODO bug: should be true
		assertEquals(false, ext(MyItem.enumSetWithout.getField(MyEnum.A)));
		assertEquals(false, ext(MyItem.enumSetWithout.getField(MyEnum.B)));

		assertEquals(false, ext(MyItem.limitedListWith   .getListSources().get(0))); // TODO bug: should be true
		assertEquals(false, ext(MyItem.limitedListWith   .getListSources().get(1))); // TODO bug: should be true
		assertEquals(false, ext(MyItem.limitedListWithout.getListSources().get(0)));
		assertEquals(false, ext(MyItem.limitedListWithout.getListSources().get(1)));
		assertEquals(false, ext(MyItem.limitedListWith   .getLengthIfExists()));
		assertEquals(false, ext(MyItem.limitedListWithout.getLengthIfExists()));
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		@MysqlExtendedVarchar
		@WrapperIgnore static final SetField<String> setWith    = SetField.create(new StringField()).ordered();
		@WrapperIgnore static final SetField<String> setWithout = SetField.create(new StringField()).ordered();
		@MysqlExtendedVarchar
		@WrapperIgnore static final ListField<String> listWith    = ListField.create(new StringField());
		@WrapperIgnore static final ListField<String> listWithout = ListField.create(new StringField());
		@MysqlExtendedVarchar
		@WrapperIgnore static final MapField<String, String> mapWith    = MapField.create(new StringField(), new StringField());
		@WrapperIgnore static final MapField<String, String> mapWithout = MapField.create(new StringField(), new StringField());
		@MysqlExtendedVarchar
		@WrapperIgnore static final EnumMapField<MyEnum,String> enumSetWith    = EnumMapField.create(MyEnum.class, new StringField());
		@WrapperIgnore static final EnumMapField<MyEnum,String> enumSetWithout = EnumMapField.create(MyEnum.class, new StringField());
		@MysqlExtendedVarchar
		@WrapperIgnore static final LimitedListField<String> limitedListWith    = LimitedListField.create(new StringField(), 2);
		@WrapperIgnore static final LimitedListField<String> limitedListWithout = LimitedListField.create(new StringField(), 2);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private enum MyEnum {A, B}

	private static boolean ext(final Field<?> f)
	{
		final boolean result = f.isAnnotationPresent(MysqlExtendedVarchar.class);
		assertEquals(result, f.getAnnotation(MysqlExtendedVarchar.class)!=null);
		return result;
	}
}

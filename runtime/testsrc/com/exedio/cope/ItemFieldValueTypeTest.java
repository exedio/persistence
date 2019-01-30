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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockField;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.CompositeField;
import com.exedio.cope.pattern.SetField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Convert2MethodRef")
@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
public class ItemFieldValueTypeTest
{
	@Test void testModelTypes()
	{
		assertEquals(asList(
				MyItem.TYPE,
				MyItem.set.getRelationType(),
				MyItem.blok.of(MyBlok.set).getRelationType()),
				MODEL.getTypes());
	}

	@Test void testValueClass()
	{
		assertSame(MyItem.class, MyItem.field.getValueClass());
		assertSame(MyItem.class, MyItem.set.getParent().getValueClass());
		assertSame(MyItem.class, MyItem.set.getElement().getValueClass());
		assertSame(MyItem.class, MyItem.comp.of(MyComp.field).getValueClass());
		assertSame(MyItem.class, MyItem.blok.of(MyBlok.field).getValueClass());
		assertSame(MyItem.class, MyItem.blok.of(MyBlok.set).getParent().getValueClass());
		assertSame(MyItem.class, MyItem.blok.of(MyBlok.set).getElement().getValueClass());
		assertSame(MyItem.class, MyComp.field.getValueClass());
		assertSame(MyItem.class, MyBlok.field.getValueClass());
		assertFails(() ->        MyBlok.set.getParent(), IllegalStateException.class, "feature not mounted");
		assertSame(MyItem.class, MyBlok.set.getElement().getValueClass());
	}

	@Test void testValueType()
	{
		assertSame(MyItem.TYPE, MyItem.field.getValueType());
		assertSame(MyItem.TYPE, MyItem.set.getParent().getValueType());
		assertSame(MyItem.TYPE, MyItem.set.getElement().getValueType());
		assertSame(MyItem.TYPE, MyItem.comp.of(MyComp.field).getValueType());
		assertSame(MyItem.TYPE, MyItem.blok.of(MyBlok.field).getValueType());
		assertSame(MyItem.TYPE, MyItem.blok.of(MyBlok.set).getParent().getValueType());
		assertSame(MyItem.TYPE, MyItem.blok.of(MyBlok.set).getElement().getValueType());
		assertSame(MyItem.TYPE, MyComp.field.getValueType());
		assertSame(MyItem.TYPE, MyBlok.field.getValueType());
		assertSame(MyItem.TYPE, MyBlok.set.getElement().getValueType());
	}

	@Test void testValueTypeModel()
	{
		assertSame(MyItem.TYPE, MyItem.field.getValueType(MODEL));
		assertSame(MyItem.TYPE, MyItem.set.getParent().getValueType(MODEL));
		assertSame(MyItem.TYPE, ((ItemField)MyItem.set.getElement()).getValueType(MODEL));
		assertSame(MyItem.TYPE, MyItem.comp.of(MyComp.field).getValueType(MODEL));
		assertSame(MyItem.TYPE, MyItem.blok.of(MyBlok.field).getValueType(MODEL));
		assertSame(MyItem.TYPE, MyItem.blok.of(MyBlok.set).getParent().getValueType(MODEL));
		assertSame(MyItem.TYPE, ((ItemField)MyItem.blok.of(MyBlok.set).getElement()).getValueType(MODEL));
		assertSame(MyItem.TYPE, MyComp.field.getValueType(MODEL));
		assertSame(MyItem.TYPE, MyBlok.field.getValueType(MODEL));
		assertSame(MyItem.TYPE, ((ItemField)MyBlok.set.getElement()).getValueType(MODEL));
	}

	@Test void testValueTypeModelNull()
	{
		assertFails(
				() -> MyItem.field.getValueType(null),
				NullPointerException.class,
				"model");
	}

	@Test void testReferences()
	{
		assertEquals(asList(
				MyItem.field, MyItem.comp.of(MyComp.field), MyItem.blok.of(MyBlok.field),
				MyItem.set.getParent(), MyItem.set.getElement(),
				MyItem.blok.of(MyBlok.set).getParent(), MyItem.blok.of(MyBlok.set).getElement()),
				MyItem.TYPE.getDeclaredReferences());
		assertEquals(asList(
				MyItem.field, MyItem.comp.of(MyComp.field), MyItem.blok.of(MyBlok.field),
				MyItem.set.getParent(), MyItem.set.getElement(),
				MyItem.blok.of(MyBlok.set).getParent(), MyItem.blok.of(MyBlok.set).getElement()),
				MyItem.TYPE.getReferences());
	}


	@WrapperType(constructor=Visibility.NONE, indent=2, comments=false)
	static final class MyComp extends Composite
	{
		@WrapperIgnore
		static final ItemField<MyItem> field = ItemField.create(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyComp(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(indent=2, comments=false)
	static final class MyBlok extends Block
	{
		@WrapperIgnore
		static final ItemField<MyItem> field = ItemField.create(MyItem.class);

		@WrapperIgnore
		static final SetField<MyItem> set = SetField.create(ItemField.create(MyItem.class));

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.pattern.BlockType<MyBlok> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlok.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyBlok(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=Visibility.NONE, genericConstructor=Visibility.NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperIgnore
		static final ItemField<MyItem> field = ItemField.create(MyItem.class);

		@WrapperIgnore
		static final SetField<MyItem> set = SetField.create(ItemField.create(MyItem.class));

		@WrapperIgnore
		static final CompositeField<MyComp> comp = CompositeField.create(MyComp.class);

		@WrapperIgnore
		static final BlockField<MyBlok> blok = BlockField.create(MyBlok.TYPE);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}

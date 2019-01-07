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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypeFuture;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Convert2MethodRef")
public class TypeFutureInconsistentTest
{
	@Test void testItem()
	{
		assertFails(
				() -> FeatureItem.field.getValueType(),
				IllegalStateException.class,
				"item field FeatureItem.field (TypeFuture(FeatureItem.field)) " +
				"does not belong to any model");

		assertFails(
				() -> new Model(TypeItem.TYPE, ValueClassItem.TYPE, FeatureItem.TYPE),
				IllegalArgumentException.class,
				"ItemField FeatureItem.field: " +
				"valueClass " + ValueClassItem.class.getName() + " " +
				"must be equal to " +
				"javaClass " + TypeItem.class.getName() + " " +
				"of valueType TypeItem provided by TypeFuture TypeFuture(FeatureItem.field).");

		// make sure there is still not value type set
		assertFails(
				() -> FeatureItem.field.getValueType(),
				IllegalStateException.class,
				"item field FeatureItem.field (TypeFuture(FeatureItem.field)) " +
				"does not belong to any model");
	}

	@Test void testComposite()
	{
		assertFails(
				() -> FeatureComposite.field.getValueType(),
				IllegalStateException.class,
				"item field " + FeatureComposite.field + " (TypeFuture(FeatureComposite.field)) " +
				"does not belong to any model");

		CompositeField.create(FeatureComposite.class);

		// make sure there is still not value type set
		assertFails(
				() -> FeatureComposite.field.getValueType(),
				IllegalStateException.class,
				"item field " + FeatureComposite.field + " (TypeFuture(FeatureComposite.field)) " +
				"does not belong to any model");
	}

	@Test void testBlock()
	{
		assertFails(
				() -> FeatureBlock.field.getValueType(),
				IllegalStateException.class,
				"item field " + FeatureBlock.field + " (TypeFuture(FeatureBlock.field)) " +
				"does not belong to any model");

		BlockField.create(FeatureBlock.TYPE);

		// make sure there is still not value type set
		assertFails(
				() -> FeatureBlock.field.getValueType(),
				IllegalStateException.class,
				"item field " + FeatureBlock.field + " (TypeFuture(FeatureBlock.field)) " +
				"does not belong to any model");
	}

	@Test void testUnmounted()
	{
		final ItemField<ValueClassItem> field = inconsistentField("Unmounted");
		assertFails(
				() -> field.getValueType(),
				IllegalStateException.class,
				"item field " + field + " (TypeFuture(Unmounted.field)) " +
				"does not belong to any model");
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ValueClassItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final com.exedio.cope.Type<ValueClassItem> TYPE = com.exedio.cope.TypesBound.newType(ValueClassItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private ValueClassItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class TypeItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final com.exedio.cope.Type<TypeItem> TYPE = com.exedio.cope.TypesBound.newType(TypeItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private TypeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class FeatureItem extends Item
	{
		@WrapperIgnore
		static final ItemField<ValueClassItem> field = inconsistentField("FeatureItem");

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final com.exedio.cope.Type<FeatureItem> TYPE = com.exedio.cope.TypesBound.newType(FeatureItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private FeatureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class FeatureComposite extends Composite
	{
		@WrapperIgnore
		static final ItemField<ValueClassItem> field = inconsistentField("FeatureComposite");

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private FeatureComposite(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(indent=2, comments=false)
	private static final class FeatureBlock extends Block
	{
		@WrapperIgnore
		static final ItemField<ValueClassItem> field = inconsistentField("FeatureBlock");

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final com.exedio.cope.pattern.BlockType<FeatureBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(FeatureBlock.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private FeatureBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	private static ItemField<ValueClassItem> inconsistentField(final String clazz)
	{
		return ItemField.create(ValueClassItem.class, new TypeFuture<ValueClassItem>()
		{
			@Override
			@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
			public Type<ValueClassItem> get()
			{
				return (Type)TypeItem.TYPE;
			}

			@Override
			public String toString()
			{
				return "TypeFuture(" + clazz + ".field)";
			}

		}, DeletePolicy.FORBID);
	}
}

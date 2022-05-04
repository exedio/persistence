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

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockField;
import com.exedio.cope.pattern.BlockType;
import org.junit.jupiter.api.Test;

/**
 * Tests item/block activation using reflection.
 * This is deprecated functionality.
 */
public class TypeActivatorReflectionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(MyAbstractItem.TYPE, MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(TypeActivatorReflectionTest.class, "MODEL");
	}

	TypeActivatorReflectionTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		final MyItem item = new MyItem(
				SetValue.map(MyItem.block.of(MyBlock.field), "fieldValue"));
		assertEquals(MyItem.TYPE, item.getCopeType());
		assertEquals("fieldValue", item.block().getField());

		final MyPattern.SourceType patternItem =
				MyItem.pattern.sourceType.newItem(SetValue.map(MyItem.pattern.sourceTypeField, "sourceFieldValue"));
		assertEquals("sourceFieldValue", MyItem.pattern.sourceTypeField.get(patternItem));
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	abstract static class MyAbstractItem extends Item
	{
		// Written manually to use reflection activator.
		@WrapInterim
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		static final Type<MyAbstractItem> TYPE = TypesBound.newType(MyAbstractItem.class);

		@com.exedio.cope.instrument.Generated
		protected MyAbstractItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		protected MyAbstractItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	static final class MyItem extends MyAbstractItem
	{
		static final BlockField<MyBlock> block = BlockField.create(MyBlock.TYPE);

		static final MyPattern pattern = new MyPattern();

		// Written manually to use reflection activator.
		@WrapInterim
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		static final Type<MyItem> TYPE = TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MyBlock block()
		{
			return MyItem.block.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final class MyPattern extends Pattern
	{
		private static final long serialVersionUID = 1l;

		final StringField sourceTypeField = new StringField();
		Type<SourceType> sourceType = null;

		@Override
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			features.put("field", sourceTypeField);
			@SuppressWarnings("deprecation") // OK: testing deprecated API
			final Type<SourceType> sourceType = newSourceType(SourceType.class, features);
			this.sourceType = sourceType;
		}

		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class SourceType extends Item
		{
			@com.exedio.cope.instrument.Generated
			@java.io.Serial
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private SourceType(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyBlock extends Block
	{
		static final StringField field = new StringField();

		// Written manually to use reflection activator.
		@WrapInterim
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		static final BlockType<MyBlock> TYPE = BlockType.newType(MyBlock.class);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return field().of(MyBlock.field).get(item());
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			field().of(MyBlock.field).set(item(),field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}
}

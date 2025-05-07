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

import com.exedio.cope.BooleanField;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.CheckViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Conditions;
import org.junit.jupiter.api.Test;

class EnumMapFieldInBlockTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(MyItem.TYPE);

	EnumMapFieldInBlockTest()
	{
		super(MODEL);
	}

	@Test
	void test()
	{
		final MyItem item = new MyItem();
		assertFails(
				() -> item.blockField().setNeedsValueForOne(true),
				CheckViolationException.class,
				"check violation on " + item + " for " + MyItem.blockField.of(MyBlock.check)
		);
	}

	private enum MyEnum
	{
		one, two
	}

	@WrapperType(comments=false, indent=2)
	private static final class MyBlock extends Block
	{
		@Wrapper(wrap = "get", visibility = NONE)
		private static final BooleanField needsValueForOne = new BooleanField().defaultTo(false);

		@Wrapper(wrap = "*", visibility = NONE)
		private static final EnumMapField<MyEnum,Integer> myMap = EnumMapField.create(MyEnum.class, new IntegerField().optional());

		private static final CheckConstraint check = new CheckConstraint(Conditions.implies(
				needsValueForOne.isTrue(),
				myMap.getField(MyEnum.one).isNotNull()
		));

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setNeedsValueForOne(final boolean needsValueForOne)
		{
			field().of(MyBlock.needsValueForOne).set(item(),needsValueForOne);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.pattern.BlockType<MyBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlock.class,MyBlock::new);

		@com.exedio.cope.instrument.Generated
		private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static class MyItem extends Item
	{
		private static final BlockField<MyBlock> blockField = BlockField.create(MyBlock.TYPE);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		private MyBlock blockField()
		{
			return MyItem.blockField.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

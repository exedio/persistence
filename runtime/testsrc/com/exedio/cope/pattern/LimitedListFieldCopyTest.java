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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class LimitedListFieldCopyTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(LimitedListCopyItem.TYPE);

	public LimitedListFieldCopyTest()
	{
		super(MODEL);
	}

	@Test void testSet()
	{
		final CopyConstraint copy0 = (CopyConstraint)LimitedListCopyItem.TYPE.getFeature("valueCopyFromlist-0");
		final CopyConstraint copy1 = (CopyConstraint)LimitedListCopyItem.TYPE.getFeature("valueCopyFromlist-1");
		final CopyConstraint copy2 = (CopyConstraint)LimitedListCopyItem.TYPE.getFeature("valueCopyFromlist-2");
		final CopyConstraint copy3 = (CopyConstraint)LimitedListCopyItem.TYPE.getFeature("valueCopyFromlist-3");
		assertEquals(asList(copy0, copy1, copy2, copy3), LimitedListCopyItem.TYPE.getDeclaredCopyConstraints());
		final LimitedListCopyItem i7 = new LimitedListCopyItem(7);
		final LimitedListCopyItem i8 = new LimitedListCopyItem(8);
		i7.setList(singletonList(i7));
		try
		{
			i7.setList(singletonList(i8));
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on LimitedListCopyItem-0 for LimitedListCopyItem.valueCopyFromlist-0, expected '8' from target LimitedListCopyItem-1, but was '7'",
				e.getMessage()
			);
		}
		assertEquals(singletonList(i7), i7.getList());
		try
		{
			i7.setList(asList(i7, i8));
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on LimitedListCopyItem-0 for LimitedListCopyItem.valueCopyFromlist-1, expected '8' from target "+i8+", but was '7'",
				e.getMessage()
			);
		}
	}

	@WrapperType(comments=false, indent=2)
	private static class LimitedListCopyItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField value = new IntegerField().toFinal();

		private static final LimitedListField<LimitedListCopyItem> list = LimitedListField.create(
			ItemField.create(LimitedListCopyItem.class).optional().copyTo(value),
			4
		);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private LimitedListCopyItem(
					final int value)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				LimitedListCopyItem.value.map(value),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected LimitedListCopyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		private java.util.List<LimitedListCopyItem> getList()
		{
			return LimitedListCopyItem.list.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setList(@javax.annotation.Nonnull final java.util.Collection<? extends LimitedListCopyItem> list)
				throws
					java.lang.ClassCastException,
					com.exedio.cope.pattern.ListSizeViolationException
		{
			LimitedListCopyItem.list.set(this,list);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<LimitedListCopyItem> TYPE = com.exedio.cope.TypesBound.newType(LimitedListCopyItem.class);

		@com.exedio.cope.instrument.Generated
		protected LimitedListCopyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

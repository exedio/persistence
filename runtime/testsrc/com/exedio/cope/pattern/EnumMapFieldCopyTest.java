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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class EnumMapFieldCopyTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(EnumMapFieldCopyItem.TYPE, EnumMapFieldCopyValue.TYPE);

	public EnumMapFieldCopyTest()
	{
		super(MODEL);
	}

	@Test public void test()
	{
		final CopyConstraint copyRed = (CopyConstraint)EnumMapFieldCopyItem.TYPE.getFeature("fieldCopyFrommap-red");
		final CopyConstraint copyGreen = (CopyConstraint)EnumMapFieldCopyItem.TYPE.getFeature("fieldCopyFrommap-green");
		final CopyConstraint copyBlue = (CopyConstraint)EnumMapFieldCopyItem.TYPE.getFeature("fieldCopyFrommap-blue");
		assertEquals(asList(copyRed, copyGreen, copyBlue), EnumMapFieldCopyItem.TYPE.getDeclaredCopyConstraints());
		final EnumMapFieldCopyItem item = new EnumMapFieldCopyItem("x");
		final EnumMapFieldCopyValue valueX = new EnumMapFieldCopyValue("x");
		item.setMap(Color.red, valueX);
		final EnumMapFieldCopyValue valueY = new EnumMapFieldCopyValue("y");
		try
		{
			item.setMap(Color.red, valueY);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on "+copyRed+", expected 'y' from target "+valueY+", but was 'x'",
				e.getMessage()
			);
		}
		assertEquals(valueX, item.getMap(Color.red));
	}

	enum Color { red, green, blue }

	@WrapperType(comments=false, indent=2)
	private static class EnumMapFieldCopyItem extends Item
	{
		@WrapperInitial
		@Wrapper(wrap="*", visibility=NONE)
		private static final StringField field = new StringField().toFinal();

		@Wrapper(wrap="getMap", visibility=NONE)
		@Wrapper(wrap="setMap", visibility=NONE)
		private static final EnumMapField<Color,EnumMapFieldCopyValue> map = EnumMapField.create(Color.class, ItemField.create(EnumMapFieldCopyValue.class).copyTo(field).optional() );

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private EnumMapFieldCopyItem(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				EnumMapFieldCopyItem.field.map(field),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected EnumMapFieldCopyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		private final EnumMapFieldCopyValue getMap(@javax.annotation.Nonnull final Color k)
		{
			return EnumMapFieldCopyItem.map.get(this,k);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private final void setMap(@javax.annotation.Nonnull final Color k,@javax.annotation.Nullable final EnumMapFieldCopyValue map)
		{
			EnumMapFieldCopyItem.map.set(this,k,map);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final com.exedio.cope.Type<EnumMapFieldCopyItem> TYPE = com.exedio.cope.TypesBound.newType(EnumMapFieldCopyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected EnumMapFieldCopyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static class EnumMapFieldCopyValue extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final StringField field = new StringField().toFinal();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private EnumMapFieldCopyValue(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				EnumMapFieldCopyValue.field.map(field),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected EnumMapFieldCopyValue(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final com.exedio.cope.Type<EnumMapFieldCopyValue> TYPE = com.exedio.cope.TypesBound.newType(EnumMapFieldCopyValue.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected EnumMapFieldCopyValue(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

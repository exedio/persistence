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
import static org.junit.Assert.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class CopyRedundantTest
{
	@Test public void testIsRedundant()
	{
		assertEquals(false, AnItem.targetMan.isRedundantByCopyConstraint());
		assertEquals(false, AnItem.targetOpt.isRedundantByCopyConstraint());

		assertEquals(false, AnItem.copyNone .isRedundantByCopyConstraint());
		assertEquals(true,  AnItem.copyMan  .isRedundantByCopyConstraint());
		assertEquals(false, AnItem.copyOpt  .isRedundantByCopyConstraint());
		assertEquals(true,  AnItem.copyBoth .isRedundantByCopyConstraint());
	}

	@WrapperType(type=NONE, indent=2)
	static final class AnItem extends Item
	{
		static final ItemField<AnItem> targetMan = ItemField.create(AnItem.class).toFinal();
		static final ItemField<AnItem> targetOpt = ItemField.create(AnItem.class).toFinal().optional();

		static final StringField copyNone = new StringField().toFinal();
		static final StringField copyMan  = new StringField().toFinal().copyFrom(targetMan);
		static final StringField copyOpt  = new StringField().toFinal().copyFrom(targetOpt);
		static final StringField copyBoth = new StringField().toFinal().copyFrom(targetMan).copyFrom(targetOpt);


		/**
		 * Creates a new AnItem with all the fields initially needed.
		 * @param targetMan the initial value for field {@link #targetMan}.
		 * @param targetOpt the initial value for field {@link #targetOpt}.
		 * @param copyNone the initial value for field {@link #copyNone}.
		 * @param copyOpt the initial value for field {@link #copyOpt}.
		 * @throws com.exedio.cope.MandatoryViolationException if targetMan, copyNone, copyOpt is null.
		 * @throws com.exedio.cope.StringLengthViolationException if copyNone, copyOpt violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		AnItem(
					@javax.annotation.Nonnull final AnItem targetMan,
					@javax.annotation.Nullable final AnItem targetOpt,
					@javax.annotation.Nonnull final java.lang.String copyNone,
					@javax.annotation.Nonnull final java.lang.String copyOpt)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.targetMan.map(targetMan),
				AnItem.targetOpt.map(targetOpt),
				AnItem.copyNone.map(copyNone),
				AnItem.copyOpt.map(copyOpt),
			});
		}

		/**
		 * Creates a new AnItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private AnItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #targetMan}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final AnItem getTargetMan()
		{
			return AnItem.targetMan.get(this);
		}

		/**
		 * Returns the value of {@link #targetOpt}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final AnItem getTargetOpt()
		{
			return AnItem.targetOpt.get(this);
		}

		/**
		 * Returns the value of {@link #copyNone}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final java.lang.String getCopyNone()
		{
			return AnItem.copyNone.get(this);
		}

		/**
		 * Returns the value of {@link #copyMan}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final java.lang.String getCopyMan()
		{
			return AnItem.copyMan.get(this);
		}

		/**
		 * Returns the value of {@link #copyOpt}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final java.lang.String getCopyOpt()
		{
			return AnItem.copyOpt.get(this);
		}

		/**
		 * Returns the value of {@link #copyBoth}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final java.lang.String getCopyBoth()
		{
			return AnItem.copyBoth.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

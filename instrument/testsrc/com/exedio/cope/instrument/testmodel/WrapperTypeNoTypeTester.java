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

package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.CopeNoType;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;

@SuppressWarnings({"EmptyClass", "unused"}) // OK: just for testing instrumentor
public final class WrapperTypeNoTypeTester
{
	@CopeNoType
	@WrapperType(indent=2)
	public static final class WithDefault extends Item
	{
		/**
		 * Creates a new WithDefault and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private WithDefault(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private WithDefault(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeNoType
	@WrapperType(type=PUBLIC, constructor=PUBLIC, genericConstructor=PUBLIC, activationConstructor=PUBLIC, indent=2)
	@SuppressWarnings("PublicConstructorInNonPublicClass") // OK: just for testing instrumentor
	static final class WithPublic extends Item
	{
		/**
		 * Creates a new WithPublic with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		public WithPublic()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		/**
		 * Creates a new WithPublic and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		public WithPublic(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for withPublic.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<WithPublic> TYPE = com.exedio.cope.TypesBound.newType(WithPublic.class,WithPublic::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		public WithPublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

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

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.instrument.Visibility.PROTECTED;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;

@SuppressWarnings({"EmptyClass", "unused"}) // OK: just for testing instrumentor
public final class WrapperTypeActivationConstructorTester
{
	@WrapperType(activationConstructor=DEFAULT,
			type=NONE, constructor=NONE, genericConstructor=NONE, indent=2)
	public static class DefaultNonFinal extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		protected DefaultNonFinal(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(activationConstructor=DEFAULT,
			type=NONE, constructor=NONE, genericConstructor=NONE, indent=2)
	public static final class DefaultFinal extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultFinal(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(activationConstructor=PUBLIC,
			type=NONE, constructor=NONE, genericConstructor=NONE, indent=2)
	@SuppressWarnings("PublicConstructorInNonPublicClass")
	static final class SetPublic extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		public SetPublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(activationConstructor=PROTECTED,
			type=NONE, constructor=NONE, genericConstructor=NONE, indent=2)
	@SuppressWarnings("ProtectedMemberInFinalClass")
	static final class SetProtected extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		protected SetProtected(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(activationConstructor=PACKAGE,
			type=NONE, constructor=NONE, genericConstructor=NONE, indent=2)
	public static final class SetPackage extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		SetPackage(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(activationConstructor=PRIVATE,
			type=NONE, constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetPrivate extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPrivate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

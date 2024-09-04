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
public final class WrapperTypeTypeTester
{
	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	public static final class DefaultPublic extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultPublic.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<DefaultPublic> TYPE = com.exedio.cope.TypesBound.newType(DefaultPublic.class,DefaultPublic::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultPublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	@SuppressWarnings("ProtectedMemberInFinalClass")
	protected static final class DefaultProtected extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultProtected.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		protected static final com.exedio.cope.Type<DefaultProtected> TYPE = com.exedio.cope.TypesBound.newType(DefaultProtected.class,DefaultProtected::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultProtected(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class DefaultPackage extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultPackage.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<DefaultPackage> TYPE = com.exedio.cope.TypesBound.newType(DefaultPackage.class,DefaultPackage::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultPackage(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	private static final class DefaultPrivate extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultPrivate.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<DefaultPrivate> TYPE = com.exedio.cope.TypesBound.newType(DefaultPrivate.class,DefaultPrivate::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultPrivate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(type=PUBLIC,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetPublic extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setPublic.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<SetPublic> TYPE = com.exedio.cope.TypesBound.newType(SetPublic.class,SetPublic::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=PROTECTED,
			constructor=NONE, genericConstructor=NONE, indent=2)
	@SuppressWarnings("ProtectedMemberInFinalClass")
	static final class SetProtected extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setProtected.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		protected static final com.exedio.cope.Type<SetProtected> TYPE = com.exedio.cope.TypesBound.newType(SetProtected.class,SetProtected::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetProtected(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=PACKAGE,
			constructor=NONE, genericConstructor=NONE, indent=2)
	public static final class SetPackage extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setPackage.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<SetPackage> TYPE = com.exedio.cope.TypesBound.newType(SetPackage.class,SetPackage::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPackage(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=PRIVATE,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetPrivate extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setPrivate.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<SetPrivate> TYPE = com.exedio.cope.TypesBound.newType(SetPrivate.class,SetPrivate::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPrivate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

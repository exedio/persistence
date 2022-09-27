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
import com.exedio.cope.ItemWildcardCast;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperType;

@SuppressWarnings({"EmptyClass", "unused"}) // OK: just for testing instrumentor
public final class WrapperTypeWildcardClassTester
{
	@WrapperType(wildcardClass=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	public static final class DefaultPublic<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use DefaultPublic.classWildcard.value instead of DefaultPublic.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		public static final class classWildcard { public static final java.lang.Class<DefaultPublic<?>> value = com.exedio.cope.ItemWildcardCast.cast(DefaultPublic.class); private classWildcard(){} }

		/**
		 * The persistent type information for defaultPublic.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<DefaultPublic<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,DefaultPublic::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultPublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("ProtectedMemberInFinalClass")
	@WrapperType(wildcardClass=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	protected static final class DefaultProtected<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use DefaultProtected.classWildcard.value instead of DefaultProtected.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		protected static final class classWildcard { public static final java.lang.Class<DefaultProtected<?>> value = com.exedio.cope.ItemWildcardCast.cast(DefaultProtected.class); private classWildcard(){} }

		/**
		 * The persistent type information for defaultProtected.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		protected static final com.exedio.cope.Type<DefaultProtected<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,DefaultProtected::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultProtected(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(wildcardClass=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class DefaultPackage<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use DefaultPackage.classWildcard.value instead of DefaultPackage.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		static final class classWildcard { public static final java.lang.Class<DefaultPackage<?>> value = com.exedio.cope.ItemWildcardCast.cast(DefaultPackage.class); private classWildcard(){} }

		/**
		 * The persistent type information for defaultPackage.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<DefaultPackage<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,DefaultPackage::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultPackage(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(wildcardClass=DEFAULT,
			constructor=NONE, genericConstructor=NONE, indent=2)
	private static final class DefaultPrivate<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use DefaultPrivate.classWildcard.value instead of DefaultPrivate.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		private static final class classWildcard { public static final java.lang.Class<DefaultPrivate<?>> value = com.exedio.cope.ItemWildcardCast.cast(DefaultPrivate.class); }

		/**
		 * The persistent type information for defaultPrivate.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<DefaultPrivate<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,DefaultPrivate::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DefaultPrivate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@WrapperType(wildcardClass=PUBLIC,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetPublic<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use SetPublic.classWildcard.value instead of SetPublic.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		public static final class classWildcard { public static final java.lang.Class<SetPublic<?>> value = com.exedio.cope.ItemWildcardCast.cast(SetPublic.class); private classWildcard(){} }

		/**
		 * The persistent type information for setPublic.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<SetPublic<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,SetPublic::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(wildcardClass=PROTECTED,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetProtected<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use SetProtected.classWildcard.value instead of SetProtected.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		protected static final class classWildcard { public static final java.lang.Class<SetProtected<?>> value = com.exedio.cope.ItemWildcardCast.cast(SetProtected.class); private classWildcard(){} }

		/**
		 * The persistent type information for setProtected.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<SetProtected<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,SetProtected::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetProtected(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(wildcardClass=PACKAGE,
			constructor=NONE, genericConstructor=NONE, indent=2)
	public static final class SetPackage<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use SetPackage.classWildcard.value instead of SetPackage.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		static final class classWildcard { public static final java.lang.Class<SetPackage<?>> value = com.exedio.cope.ItemWildcardCast.cast(SetPackage.class); private classWildcard(){} }

		/**
		 * The persistent type information for setPackage.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<SetPackage<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,SetPackage::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPackage(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(wildcardClass=PRIVATE,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetPrivate<E> extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * Use SetPrivate.classWildcard.value instead of SetPrivate.class to avoid rawtypes warnings.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
		private static final class classWildcard { public static final java.lang.Class<SetPrivate<?>> value = com.exedio.cope.ItemWildcardCast.cast(SetPrivate.class); }

		/**
		 * The persistent type information for setPrivate.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<SetPrivate<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,SetPrivate::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetPrivate(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(wildcardClass=NONE,
			constructor=NONE, genericConstructor=NONE, indent=2)
	static final class SetNone<E> extends Item
	{
		@WrapInterim
		private static final class classWildcard
		{
			public static final Class<SetNone<?>> value = ItemWildcardCast.cast(SetNone.class);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setNone.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<SetNone<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,SetNone::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private SetNone(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

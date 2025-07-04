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
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.WrapperType;

@SuppressWarnings({"AbstractClassNeverImplemented", "EmptyClass", "unused"}) // OK: just for testing instrumentor
public final class WrapperTypeConstructorTester
{
	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	public static final class DefaultPublic extends Item
	{
		private DefaultPublic(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultPublic with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		public DefaultPublic()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	public abstract static class DefaultPublicAbstract extends Item
	{
		private DefaultPublicAbstract(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultPublicAbstract with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		protected DefaultPublicAbstract()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;
	}

	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	protected static final class DefaultProtected extends Item
	{
		private DefaultProtected(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultProtected with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		protected DefaultProtected()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	protected abstract static class DefaultProtectedAbstract extends Item
	{
		private DefaultProtectedAbstract(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultProtectedAbstract with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		protected DefaultProtectedAbstract()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;
	}

	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	static final class DefaultPackage extends Item
	{
		private DefaultPackage(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultPackage with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		DefaultPackage()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	private static final class DefaultPrivate extends Item
	{
		private DefaultPrivate(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultPrivate with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private DefaultPrivate()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=DEFAULT,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	@SuppressWarnings("ClassWithOnlyPrivateConstructors")
	private abstract static class DefaultPrivateAbstract extends Item
	{
		private DefaultPrivateAbstract(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new DefaultPrivateAbstract with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private DefaultPrivateAbstract()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;
	}


	@WrapperType(constructor=PUBLIC,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	@SuppressWarnings("PublicConstructorInNonPublicClass")
	static final class SetPublic extends Item
	{
		private SetPublic(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new SetPublic with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		public SetPublic()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=PUBLIC,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	@SuppressWarnings({"PublicConstructorInNonPublicClass","ConstructorNotProtectedInAbstractClass"})
	abstract static class SetPublicAbstract extends Item
	{
		private SetPublicAbstract(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new SetPublicAbstract with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		public SetPublicAbstract()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;
	}

	@WrapperType(constructor=PROTECTED,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	@SuppressWarnings("ProtectedMemberInFinalClass")
	static final class SetProtected extends Item
	{
		private SetProtected(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new SetProtected with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		protected SetProtected()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=PACKAGE,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	public static final class SetPackage extends Item
	{
		private SetPackage(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new SetPackage with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		SetPackage()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=PRIVATE,
			type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	static final class SetPrivate extends Item
	{
		private SetPrivate(final SetValue<?>[] sv) { super(sv); }

		/**
		 * Creates a new SetPrivate with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private SetPrivate()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}
}

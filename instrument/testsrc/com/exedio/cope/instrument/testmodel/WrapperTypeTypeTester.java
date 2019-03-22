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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({"EmptyClass", "unused"}) // OK: just for testing instrumentor
public final class WrapperTypeTypeTester
{
	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	public static final class DefaultPublic extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultPublic.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<DefaultPublic> TYPE = com.exedio.cope.TypesBound.newType(DefaultPublic.class);
	}

	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	@SuppressFBWarnings("CI_CONFUSED_INHERITANCE")
	protected static final class DefaultProtected extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultProtected.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		protected static final com.exedio.cope.Type<DefaultProtected> TYPE = com.exedio.cope.TypesBound.newType(DefaultProtected.class);
	}

	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	static final class DefaultPackage extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultPackage.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<DefaultPackage> TYPE = com.exedio.cope.TypesBound.newType(DefaultPackage.class);
	}

	@WrapperType(type=DEFAULT,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	private static final class DefaultPrivate extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for defaultPrivate.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<DefaultPrivate> TYPE = com.exedio.cope.TypesBound.newType(DefaultPrivate.class);
	}


	@WrapperType(type=PUBLIC,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	static final class SetPublic extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setPublic.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		public static final com.exedio.cope.Type<SetPublic> TYPE = com.exedio.cope.TypesBound.newType(SetPublic.class);
	}

	@WrapperType(type=PROTECTED,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	@SuppressFBWarnings("CI_CONFUSED_INHERITANCE")
	static final class SetProtected extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setProtected.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		protected static final com.exedio.cope.Type<SetProtected> TYPE = com.exedio.cope.TypesBound.newType(SetProtected.class);
	}

	@WrapperType(type=PACKAGE,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	public static final class SetPackage extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setPackage.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<SetPackage> TYPE = com.exedio.cope.TypesBound.newType(SetPackage.class);
	}

	@WrapperType(type=PRIVATE,
			constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2)
	static final class SetPrivate extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for setPrivate.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<SetPrivate> TYPE = com.exedio.cope.TypesBound.newType(SetPrivate.class);
	}
}

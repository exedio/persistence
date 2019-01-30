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

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(indent=3)
final class Indent extends Item
{
	static final StringField one = new StringField().optional();
	static final StringField two = new StringField();
	static final UniqueConstraint unq = new UniqueConstraint(one, two);

			/**
			 * Creates a new Indent with all the fields initially needed.
			 * @param two the initial value for field {@link #two}.
			 * @throws com.exedio.cope.MandatoryViolationException if two is null.
			 * @throws com.exedio.cope.StringLengthViolationException if two violates its length constraint.
			 * @throws com.exedio.cope.UniqueViolationException if two is not unique.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
			Indent(
						@javax.annotation.Nonnull final java.lang.String two)
					throws
						com.exedio.cope.MandatoryViolationException,
						com.exedio.cope.StringLengthViolationException,
						com.exedio.cope.UniqueViolationException
			{
				this(new com.exedio.cope.SetValue<?>[]{
					Indent.two.map(two),
				});
			}

			/**
			 * Creates a new Indent and sets the given fields initially.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
			private Indent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

			/**
			 * Returns the value of {@link #one}.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
			@javax.annotation.Nullable
			java.lang.String getOne()
			{
				return Indent.one.get(this);
			}

			/**
			 * Sets a new value for {@link #one}.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
			void setOne(@javax.annotation.Nullable final java.lang.String one)
					throws
						com.exedio.cope.UniqueViolationException,
						com.exedio.cope.StringLengthViolationException
			{
				Indent.one.set(this,one);
			}

			/**
			 * Returns the value of {@link #two}.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
			@javax.annotation.Nonnull
			java.lang.String getTwo()
			{
				return Indent.two.get(this);
			}

			/**
			 * Sets a new value for {@link #two}.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
			void setTwo(@javax.annotation.Nonnull final java.lang.String two)
					throws
						com.exedio.cope.MandatoryViolationException,
						com.exedio.cope.UniqueViolationException,
						com.exedio.cope.StringLengthViolationException
			{
				Indent.two.set(this,two);
			}

			/**
			 * Finds a indent by it's unique fields.
			 * @param one shall be equal to field {@link #one}.
			 * @param two shall be equal to field {@link #two}.
			 * @return null if there is no matching item.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
			@javax.annotation.Nullable
			static Indent forUnq(@javax.annotation.Nonnull final java.lang.String one,@javax.annotation.Nonnull final java.lang.String two)
			{
				return Indent.unq.search(Indent.class,one,two);
			}

			/**
			 * Finds a indent by its unique fields.
			 * @param one shall be equal to field {@link #one}.
			 * @param two shall be equal to field {@link #two}.
			 * @throws java.lang.IllegalArgumentException if there is no matching item.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
			@javax.annotation.Nonnull
			static Indent forUnqStrict(@javax.annotation.Nonnull final java.lang.String one,@javax.annotation.Nonnull final java.lang.String two)
					throws
						java.lang.IllegalArgumentException
			{
				return Indent.unq.searchStrict(Indent.class,one,two);
			}

			@javax.annotation.Generated("com.exedio.cope.instrument")
			private static final long serialVersionUID = 1l;

			/**
			 * The persistent type information for indent.
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
			static final com.exedio.cope.Type<Indent> TYPE = com.exedio.cope.TypesBound.newType(Indent.class);

			/**
			 * Activation constructor. Used for internal purposes only.
			 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
			 */
			@javax.annotation.Generated("com.exedio.cope.instrument")
			@SuppressWarnings("unused") private Indent(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

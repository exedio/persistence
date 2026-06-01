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

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.opentest4j.AssertionFailedError;

@SuppressWarnings({"EmptyClass","unused"})
public class StringFieldValidateInstrumentTest
{
	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final StringField one = new StringField().validate(new MyValidator(List.of(
				IllegalArgumentException.class)));

		@WrapperInitial
		static final StringField two = new StringField().validate(new MyValidator(List.of(
				IntegerRangeViolationException.class,
				DoubleRangeViolationException.class))).
				optional();

		@WrapperInitial
		static final StringField twoReverse = new StringField().validate(new MyValidator(List.of(
				DoubleRangeViolationException.class,
				IntegerRangeViolationException.class))).
				optional();

		@WrapperInitial
		static final StringField twoAnd = new StringField().validate(
				new MyValidator(List.of(IntegerRangeViolationException.class)).and(
				new MyValidator(List.of(DoubleRangeViolationException.class)))).
				optional();

		static final StringField none = new StringField().validate(new MyValidator(List.of()));

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final java.lang.String one,
					@javax.annotation.Nullable final java.lang.String two,
					@javax.annotation.Nullable final java.lang.String twoReverse,
					@javax.annotation.Nullable final java.lang.String twoAnd,
					@javax.annotation.Nonnull final java.lang.String none)
				throws
					com.exedio.cope.DoubleRangeViolationException,
					com.exedio.cope.IntegerRangeViolationException,
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.IllegalArgumentException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.one,one),
				com.exedio.cope.SetValue.map(MyItem.two,two),
				com.exedio.cope.SetValue.map(MyItem.twoReverse,twoReverse),
				com.exedio.cope.SetValue.map(MyItem.twoAnd,twoAnd),
				com.exedio.cope.SetValue.map(MyItem.none,none),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getOne()
		{
			return MyItem.one.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setOne(@javax.annotation.Nonnull final java.lang.String one)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.IllegalArgumentException
		{
			MyItem.one.set(this,one);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getTwo()
		{
			return MyItem.two.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTwo(@javax.annotation.Nullable final java.lang.String two)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.IntegerRangeViolationException,
					com.exedio.cope.DoubleRangeViolationException
		{
			MyItem.two.set(this,two);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getTwoReverse()
		{
			return MyItem.twoReverse.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTwoReverse(@javax.annotation.Nullable final java.lang.String twoReverse)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.DoubleRangeViolationException,
					com.exedio.cope.IntegerRangeViolationException
		{
			MyItem.twoReverse.set(this,twoReverse);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getTwoAnd()
		{
			return MyItem.twoAnd.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTwoAnd(@javax.annotation.Nullable final java.lang.String twoAnd)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.IntegerRangeViolationException,
					com.exedio.cope.DoubleRangeViolationException
		{
			MyItem.twoAnd.set(this,twoAnd);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getNone()
		{
			return MyItem.none.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setNone(@javax.annotation.Nonnull final java.lang.String none)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.none.set(this,none);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapInterim
	@SuppressWarnings("ClassCanBeRecord")
	private static class MyValidator implements FieldValidator<String>
	{
		@WrapInterim
		private final List<Class<? extends Throwable>> exceptions;

		@WrapInterim
		MyValidator(final List<Class<? extends Throwable>> exceptions)
		{
			this.exceptions = exceptions;
		}

		@Override
		@WrapInterim
		public @Nullable ConstraintViolationException validate(
				@Nonnull final String value,
				@Nullable final Item exceptionItem,
				@NonNull final Field<String> field)
		{
			throw new AssertionFailedError();
		}

		@Override
		@WrapInterim
		public Set<Class<? extends Throwable>> getValidateExceptions()
		{
			return new LinkedHashSet<>(exceptions);
		}
	}
}

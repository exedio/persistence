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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.io.Serial;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class StringFieldValidateTest extends TestWithEnvironment
{
	@Test void test()
	{
		assertSame(VALIDATOR, MyItem.field.getValidator());

		final String valueOk = "value OK";
		final String valueFail = "value FAIL";
		final String valueFailTooLong = valueFail + "0123456789".repeat(10);

		MyItem.field.check(valueOk);
		assertFails(
				() -> MyItem.field.check(valueFail),
				MyException.class,
				valueFail+"-null-MyItem.field");
		assertFails(
				() -> MyItem.field.check(valueFailTooLong),
				StringLengthViolationException.class,
				"length violation, '" + valueFailTooLong + "' is too long for MyItem.field, " +
				"must be at most 80 characters, but was 110");
		MyItem.field.check(null);

		final MyItem item = new MyItem(valueOk);
		assertEquals(valueOk, item.getField());
		assertFails(
				() -> new MyItem(valueFail),
				MyException.class,
				valueFail+"-null-MyItem.field");
		assertFails(
				() -> new MyItem(valueFailTooLong),
				StringLengthViolationException.class,
				"length violation, '" + valueFailTooLong + "' is too long for MyItem.field, " +
				"must be at most 80 characters, but was 110");
		new MyItem((String)null);

		assertFails(
				() -> item.setField(valueFail),
				MyException.class,
				valueFail+"-"+item+"-MyItem.field");
		assertEquals(valueOk, item.getField());
		assertFails(
				() -> item.setField(valueFailTooLong),
				StringLengthViolationException.class,
				"length violation on " + item + ", '" + valueFailTooLong + "' is too long for MyItem.field, " +
				"must be at most 80 characters, but was 110");
		assertEquals(valueOk, item.getField());

		item.setField(null);
		assertEquals(null, item.getField());
	}

	@WrapInterim
	private static final FieldValidator<String> VALIDATOR = new FieldValidator<>()
	{
		@Override
		public @Nullable ConstraintViolationException validate(
				@Nonnull final String value,
				@Nullable final Item exceptionItem,
				@NonNull final Field<String> field)
		{
			assertNotNull(value);
			return value.contains("FAIL")
					? new MyException(value, exceptionItem, field)
					: null;
		}
		@Override
		public Set<Class<? extends Throwable>> getValidateExceptions()
		{
			return Set.of(MyException.class);
		}
	};

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@WrapperInitial
		@Wrapper(wrap="set", suppressWarnings="UnnecessarilyQualifiedInnerClassAccess")
		static final StringField field = new StringField().optional().validate(VALIDATOR);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringFieldValidateTest.MyException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.StringFieldValidateTest.MyException
		{
			MyItem.field.set(this,field);
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
	private static final class MyException extends ConstraintViolationException
	{
		@WrapInterim
		private final StringField field;
		@WrapInterim
		private final String value;

		@WrapInterim
		MyException(final String value, final Item item, final Field<String> field)
		{
			super(item, null);
			this.value = value;
			this.field = StringField.cast(field);
		}

		@Override
		public StringField getFeature()
		{
			return field;
		}

		@Override
		protected String getMessage(final boolean withFeature)
		{
			return value + '-' + getItem() + '-' + field;
		}

		@Serial private static final long serialVersionUID = 1L;
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	public StringFieldValidateTest()
	{
		super(MODEL);
	}
}

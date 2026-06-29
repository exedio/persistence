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
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Serial;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class FieldValidatorTest
{
	@Test void testValidateBothNull()
	{
		assertEquals(null, validateOk.and(validateOk).validate(null, null, null));
	}

	@Test void testValidateFirstFails()
	{
		assertEquals("A", validateFailsA.and(validateOk).validate(null, null, null).getMessage());
	}

	@Test void testValidateSecondFails()
	{
		assertEquals("B", validateOk.and(validateFailsB).validate(null, null, null).getMessage());
	}

	@Test void testValidateBothFail()
	{
		assertEquals("A", validateFailsA.and(validateFailsB).validate(null, null, null).getMessage());
	}

	private static final FieldValidator<String> validateOk = (v, i, f) -> null;
	private static final FieldValidator<String> validateFailsA = (v, i, f) -> new MyException("A");
	private static final FieldValidator<String> validateFailsB = (v, i, f) -> new MyException("B");

	private static final class MyException extends ConstraintViolationException
	{
		private final String message;

		MyException(final String message)
		{
			super(null, null);
			this.message = message;
		}

		@Override
		public Feature getFeature()
		{
			return null;
		}

		@Override
		protected String getMessage(final boolean withFeature)
		{
			return message;
		}

		@Serial
		private static final long serialVersionUID = 1L;
	}

	@Test void testExceptionsValidator()
	{
		final var one = new ExceptionsValidator(Set.of(IllegalArgumentException.class));
		final var two = new ExceptionsValidator(Set.of(IllegalStateException.class));
		final FieldValidator<String> and = one.and(two);
		assertEquals(
				Set.of(IllegalArgumentException.class, IllegalStateException.class),
				and.getValidateExceptions());
	}

	@Test void testExceptionsValidatorOrderAndDuplicates()
	{
		final var one = new ExceptionsValidator(new LinkedHashSet<>(asList(
				NullPointerException.class,
				IllegalArgumentException.class,
				IOException.class
		)));
		final var two = new ExceptionsValidator(new LinkedHashSet<>(asList(
				IOException.class,
				IllegalStateException.class
		)));
		final FieldValidator<String> and = one.and(two);
		assertEquals(List.of(
				NullPointerException.class,
				IllegalArgumentException.class,
				IOException.class,
				IllegalStateException.class),
				asList(and.getValidateExceptions().toArray()));
	}

	private static final class ExceptionsValidator extends AssertionFailedValidator
	{
		final Set<Class<? extends Throwable>> exceptions;

		private ExceptionsValidator(final Set<Class<? extends Throwable>> exceptions)
		{
			this.exceptions = exceptions;
		}

		@Override
		public Set<Class<? extends Throwable>> getValidateExceptions()
		{
			return unmodifiableSet(exceptions);
		}
	}

	@Test void testToString()
	{
		final var one = new ToStringValidator("one");
		final var two = new ToStringValidator("two");
		final FieldValidator<String> and = one.and(two);
		assertEquals("one/two", and.toString());
	}

	private static final class ToStringValidator extends AssertionFailedValidator
	{
		final String string;

		private ToStringValidator(final String string)
		{
			this.string = string;
		}

		@Override
		public String toString()
		{
			return string;
		}
	}

	@Test void testNull()
	{
		final var some = new ToStringValidator("some");
		assertFails(
				() -> some.and(null),
				NullPointerException.class,
				"other");
	}

	private static class AssertionFailedValidator implements FieldValidator<String>
	{
		@Override
		public @Nullable ConstraintViolationException validate(
				@NonNull final String value,
				@Nullable final Item exceptionItem,
				@NonNull final Field<String> field)
		{
			throw new AssertionFailedError();
		}
	}
}

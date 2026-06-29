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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.WrapImplementsInterim;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
@WrapImplementsInterim
public interface FieldValidator<E>
{
	/**
	 * @param value is never null, even if {@code field} is {@link Field#optional() optional}.
	 */
	@Nullable ConstraintViolationException validate(
			@NonNull E value,
			@Nullable Item exceptionItem,
			@NonNull Field<E> field);

	/**
	 * Returns the exceptions possibly returned by
	 * {@link #validate(Object, Item, Field)}.
	 * @see Settable#getInitialExceptions()
	 */
	default Set<Class<? extends Throwable>> getValidateExceptions()
	{
		return Set.of();
	}

	default FieldValidator<E> and(@NonNull final FieldValidator<E> other)
	{
		requireNonNull(other, "other");

		return new FieldValidator<>()
		{
			@Override
			public @Nullable ConstraintViolationException validate(
					@NonNull final E value,
					@Nullable final Item exceptionItem,
					@NonNull final Field<E> field)
			{
				final ConstraintViolationException thisResult =
						FieldValidator.this.validate(value, exceptionItem, field);
				if(thisResult!=null)
					return thisResult;

				return other.validate(value, exceptionItem, field);
			}

			@Override
			public Set<Class<? extends Throwable>> getValidateExceptions()
			{
				final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
				result.addAll(FieldValidator.this.getValidateExceptions());
				result.addAll(other.getValidateExceptions());
				return result;
			}

			@Override
			public String toString()
			{
				return FieldValidator.this.toString() + '/' + other;
			}
		};
	}
}

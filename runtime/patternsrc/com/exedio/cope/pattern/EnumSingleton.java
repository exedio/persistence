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

package com.exedio.cope.pattern;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.CopeSchemaNameElement;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class EnumSingleton<E extends Enum<E>> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1l;

	private final EnumField<E> once;

	private EnumSingleton(final Class<E> valueClass)
	{
		once = addSourceFeature(EnumField.create(valueClass).toFinal().unique(), "once", CustomAnnotatedElement.create(CopeSchemaNameElement.getEmpty()));
	}

	public static <E extends Enum<E>> EnumSingleton<E> create(final Class<E> valueClass)
	{
		return new EnumSingleton<>(valueClass);
	}

	public EnumField<E> getOnce()
	{
		return once;
	}

	@Override
	public boolean isInitial()
	{
		return true;
	}

	@Override
	public boolean isFinal()
	{
		return true;
	}

	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public Class<E> getInitialType()
	{
		return once.getValueClass();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return once.getInitialExceptions();
	}

	@Wrap(order=10, doc=Wrap.GET_DOC)
	@Nonnull
	public E get(final Item item)
	{
		return once.get(item);
	}

	@Wrap(order=20,
			name="instance",
			doc="Gets the instance of {2} for the given value.",
			docReturn="never returns null.",
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc="if no such instance exists"))
	@Nonnull
	public <P extends Item> P instance(
			@Nonnull final Class<P> typeClass,
			@Nonnull final E value)
	{
		requireNonNull(value, "value");

		final Type<P> type = getType().as(typeClass);
		final P found = type.searchSingleton(once.equal(value));
		if(found!=null)
			return found;
		else
			// TODO: this behaviour is inconsistent with Singleton#instance:
			throw new IllegalArgumentException(value.name());
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue<?>[] {
			SetValue.map(once, value)
		};
	}
}

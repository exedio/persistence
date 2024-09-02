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

import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Stores a java object by serialization - use with care!
 * <p>
 * Stores serializable objects into a backing
 * {@link DataField}.
 * BEWARE:
 * Generally this is not a good idea.
 * In contrast to normal fields, such as
 * {@link StringField}, {@link DateField},
 * etc. there is no searching, order by or caching.
 * The main purpose is to maintain database compatibility
 * to legacy systems.
 */
@WrapFeature
public final class Serializer<E> extends Pattern implements Settable<E>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Class<E> valueClass;
	private final DataField source;

	private Serializer(final Class<E> valueClass, final DataField source)
	{
		this.valueClass = requireNonNull(valueClass, "valueClass");
		this.source = addSourceFeature(source, "data", ComputedElement.get());
	}

	/**
	 * @deprecated external sources are discouraged and will be removed in the future
	 */
	@Deprecated
	public static <E> Serializer<E> create(final Class<E> valueClass, final DataField source)
	{
		return new Serializer<>(valueClass, source);
	}

	public static <E> Serializer<E> create(final Class<E> valueClass)
	{
		return new Serializer<>(valueClass, new DataField());
	}

	public Serializer<E> optional()
	{
		return new Serializer<>(valueClass, source.optional());
	}

	// TODO allow setting of length of DataField

	public DataField getSource()
	{
		return source;
	}

	@Override
	public boolean isInitial()
	{
		return source.isInitial();
	}

	@Override
	public boolean isFinal()
	{
		return source.isFinal();
	}

	@Override
	public boolean isMandatory()
	{
		return source.isMandatory();
	}

	@Override
	public Class<?> getInitialType()
	{
		return valueClass;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return source.getInitialExceptions();
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public E get(@Nonnull final Item item)
	{
		final byte[] buf = source.getArray(item);

		if(buf==null)
			return null;

		final E result;
		final ByteArrayInputStream bis = new ByteArrayInputStream(buf);
		try(ObjectInputStream ois = new ObjectInputStream(bis))
		{
			result = valueClass.cast(ois.readObject());
		}
		catch(final IOException | ClassNotFoundException e)
		{
			throw new RuntimeException(toString(), e);
		}

		return result;
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final E value)
	{
		FinalViolationException.check(this, item);

		source.set(item, serialize(value));
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ source.map(serialize(value)) };
	}

	private byte[] serialize(final E value)
	{
		if(value==null)
			return null;

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try(ObjectOutputStream oos = new ObjectOutputStream(bos))
		{
			oos.writeObject(value);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(toString(), e);
		}
		return bos.toByteArray();
	}
}

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

import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;

import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 * A {@code view} represents a value computed from the
 * fields of a {@link Type}.
 * The computation is available both in Java and SQL,
 * so you can use views in search conditions.
 *
 * @author Ralf Wiebicke
 */
@WrapFeature
public abstract class View<E> extends Feature
	implements Function<E>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Function<?>[] sources;
	private final List<Function<?>> sourceList;
	private final String name;
	final Class<E> valueClass;
	final Type<?> sourceType;

	protected View(final Function<?>[] sources, final String name, final Class<E> valueClass)
	{
		this.sources = requireNonEmptyAndCopy(sources, "sources");
		this.sourceList = List.of(this.sources);
		this.name = name;
		this.valueClass = valueClass;

		if(sources[0] instanceof final Feature f)
		{
			this.sourceType = f.isMountedToType() ? f.getType() : null;
		}
		else
		{
			this.sourceType = null;
		}
	}

	public final List<Function<?>> getSources()
	{
		return sourceList;
	}

	protected abstract E mapJava(Object[] sourceValues);

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void check(@SuppressWarnings("ClassEscapesDefinedScope") final TC tc, final Join join)
	{
		for(final Function<?> source : sources)
			source.check(tc, join);
	}

	@Override
	public final void forEachFieldCovered(final Consumer<Field<?>> action)
	{
		for(final Function<?> source : sources)
			source.forEachFieldCovered(action);
	}

	@Override
	public final void requireSupportForGet() throws UnsupportedGetException
	{
		for(final Function<?> source : sources)
			source.requireSupportForGet();
	}

	@Wrap(order=10, name="get{0}", doc=Wrap.GET_DOC) // TODO box into primitives
	public final E getSupported(@Nonnull final Item item)
	{
		try
		{
			return get(item);
		}
		catch(final UnsupportedGetException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public final E get(@Nonnull final Item item) throws UnsupportedGetException
	{
		return get(new FieldValues(item));
	}

	@Override
	public final E get(final FieldValues item) throws UnsupportedGetException
	{
		final Object[] values = new Object[sources.length];
		int pos = 0;
		for(final Function<?> source : sources)
			values[pos++] = source.get(item);

		return mapJava(values);
	}

	@Override
	public final Class<E> getValueClass()
	{
		return valueClass;
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append(name);
		bf.append('(');
		for(int i = 0; i<sources.length; i++)
		{
			if(i>0)
				bf.append(',');
			sources[i].toString(bf, defaultType);
		}
		bf.append(')');
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof final View<?> o))
			return false;

		if(!name.equals(o.name) || sources.length!=o.sources.length)
			return false;

		for(int i = 0; i<sources.length; i++)
		{
			if(!sources[i].equals(o.sources[i]))
				return false;
		}

		return true;
	}

	@Override
	public final int hashCode()
	{
		int result = name.hashCode();

		for(final Function<?> source : sources)
			result = (31*result) + source.hashCode(); // may not be commutative

		return result;
	}


	// second initialization phase ---------------------------------------------------

	@Override
	final void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		if(sourceType!=null && type!=sourceType)
			throw new RuntimeException();

		try
		{
			for(final Function<?> source : sources)
				source.requireSupportForGet();
		}
		catch(final UnsupportedGetException e)
		{
			throw new IllegalArgumentException(
					"view contains unsupported function: " + e.function);
		}

		super.mount(type, name, annotationSource);
	}

	@Override
	public final Type<?> getType()
	{
		return (sourceType!=null) ? sourceType : super.getType();
	}
}

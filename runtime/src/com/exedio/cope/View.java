/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.search.ExtremumAggregate;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A <tt>view</tt> represents a value computed from the
 * fields of a {@link Type}.
 * The computation is available both in Java and SQL,
 * so you can use views in search conditions.
 *
 * @author Ralf Wiebicke
 */
public abstract class View<E> extends Feature
	implements Function<E>
{
	private static final long serialVersionUID = 1l;

	private final Function<?>[] sources;
	private final List<Function<?>> sourceList;
	private final String name;
	final Class<E> valueClass;
	final Type<? extends Item> sourceType;

	public View(final Function<?>[] sources, final String name, final Class<E> valueClass)
	{
		if(sources==null)
			throw new NullPointerException("sources");
		if(sources.length==0)
			throw new IllegalArgumentException("sources must not be empty");
		for(int i = 0; i<sources.length; i++)
			if(sources[i]==null)
				throw new NullPointerException("sources" + '[' + i + ']');

		this.sources = com.exedio.cope.misc.Arrays.copyOf(sources);
		this.sourceList = Collections.unmodifiableList(Arrays.asList(this.sources));
		this.name = name;
		this.valueClass = valueClass;

		if(sources[0] instanceof Feature)
		{
			final Feature f = (Feature)sources[0];
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
	public final void check(final TC tc, final Join join)
	{
		for(final Function<?> source : sources)
			source.check(tc, join);
	}

	@Override
	@Wrap(order=10, doc="Returns the value of {0}.") // TODO box into primitives
	public final E get(final Item item)
	{
		final Object[] values = new Object[sources.length];
		int pos = 0;
		for(final Function<?> source : sources)
			values[pos++] = source.get(item);

		return mapJava(values);
	}

	/**
	 * @deprecated use {@link #get(Item)} instead.
	 */
	@Deprecated
	public final Object getObject(final Item item)
	{
		return get(item);
	}

	@Override
	public final Class<E> getValueClass()
	{
		return valueClass;
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void appendSelect(final Statement bf, final Join join)
	{
		append(bf, join);
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
		if(!(other instanceof View<?>))
			return false;

		final View<?> o = (View<?>)other;

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
	final void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		if(sourceType!=null && type!=sourceType)
			throw new RuntimeException();

		super.mount(type, name, annotationSource);
	}

	@Override
	public final Type<? extends Item> getType()
	{
		return (sourceType!=null) ? sourceType : super.getType();
	}

	// convenience methods for conditions and views ---------------------------------

	@Override
	public final IsNullCondition<E> isNull()
	{
		return new IsNullCondition<E>(this, false);
	}

	@Override
	public final IsNullCondition<E> isNotNull()
	{
		return new IsNullCondition<E>(this, true);
	}

	@Override
	public final Condition equal(final E value)
	{
		return Cope.equal(this, value);
	}

	@Override
	public final Condition equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}

	@Override
	public final Condition in(final E... values)
	{
		return CompositeCondition.in(this, values);
	}

	@Override
	public final Condition in(final Collection<? extends E> values)
	{
		return CompositeCondition.in(this, values);
	}

	@Override
	public final Condition notEqual(final E value)
	{
		return Cope.notEqual(this, value);
	}

	@Override
	public final CompareCondition<E> less(final E value)
	{
		return new CompareCondition<E>(Operator.Less, this, value);
	}

	@Override
	public final CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.LessEqual, this, value);
	}

	@Override
	public final CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<E>(Operator.Greater, this, value);
	}

	@Override
	public final CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.GreaterEqual, this, value);
	}

	@Override
	public Condition between(final E lowerBound, final E upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	@Override
	public final CompareFunctionCondition<E> equal(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Equal, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> notEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.NotEqual, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> less(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Less, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> lessOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.LessEqual, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> greater(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Greater, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> greaterOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.GreaterEqual, this, right);
	}

	@Override
	public final ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<E>(this, true);
	}

	@Override
	public final ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<E>(this, false);
	}

	@Override
	public BindFunction<E> bind(final Join join)
	{
		return new BindFunction<E>(this, join);
	}
}

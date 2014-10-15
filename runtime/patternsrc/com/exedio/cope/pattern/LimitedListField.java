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

package com.exedio.cope.pattern;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.ReflectionTypes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class LimitedListField<E> extends AbstractListField<E> implements Settable<Collection<E>>
{
	private static final long serialVersionUID = 1l;

	private final IntegerField length;
	private final FunctionField<E>[] sources;
	private final boolean initial;
	private final boolean isFinal;
	private final CheckConstraint unison;

	private LimitedListField(final FunctionField<E>[] sources)
	{
		this.length = new IntegerField().range(0, sources.length).defaultTo(0);
		addSource(this.length, "Len", ComputedElement.get());

		this.sources = sources;

		boolean initial = false;
		boolean isFinal = false;
		int i = 0;
		for(final FunctionField<E> source : sources)
		{
			addSource(source, String.valueOf(i++), ComputedElement.get());
			initial = initial || source.isInitial();
			isFinal = isFinal || source.isFinal();
		}
		this.initial = initial;
		this.isFinal = isFinal;

		final Condition[] unisonConditions = new Condition[sources.length];
		for(int a = 0; a<sources.length; a++)
			unisonConditions[a] = length.greater(a).or(sources[a].isNull());
		this.unison = new CheckConstraint(Cope.and(unisonConditions));
		addSource(unison, "unison");
	}

	private LimitedListField(final FunctionField<E> source1, final FunctionField<E> source2)
	{
		this(LimitedListField.<E>cast(new FunctionField<?>[]{source1, source2}));
	}

	private LimitedListField(final FunctionField<E> source1, final FunctionField<E> source2, final FunctionField<E> source3)
	{
		this(LimitedListField.<E>cast(new FunctionField<?>[]{source1, source2, source3}));
	}

	private LimitedListField(final FunctionField<E> template, final int maximumSize)
	{
		this(template2Sources(template, maximumSize));
	}

	public static final <E> LimitedListField<E> create(final FunctionField<E> source1, final FunctionField<E> source2)
	{
		return new LimitedListField<>(source1, source2);
	}

	public static final <E> LimitedListField<E> create(final FunctionField<E> source1, final FunctionField<E> source2, final FunctionField<E> source3)
	{
		return new LimitedListField<>(source1, source2, source3);
	}

	public static final <E> LimitedListField<E> create(final FunctionField<E> template, final int maximumSize)
	{
		return new LimitedListField<>(template, maximumSize);
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic array creation
	private static final <X> FunctionField<X>[] cast(final FunctionField[] o)
	{
		return o;
	}

	private static final <Y> FunctionField<Y>[] template2Sources(final FunctionField<Y> template, final int maximumSize)
	{
		if(maximumSize<=1)
			throw new IllegalArgumentException("maximumSize must be greater 1, but was " + maximumSize);

		final FunctionField<Y>[] result = cast(new FunctionField<?>[maximumSize]);

		for(int i = 0; i<maximumSize; i++)
			result[i] = template.copy();

		return result;
	}


	public IntegerField getLength()
	{
		return length;
	}

	public List<FunctionField<E>> getListSources()
	{
		return Collections.unmodifiableList(Arrays.asList(sources));
	}

	public CheckConstraint getUnison()
	{
		return unison;
	}

	@Override
	public FunctionField<E> getElement()
	{
		return sources[0];
	}

	@Override
	public int getMaximumSize()
	{
		return sources.length;
	}

	public boolean isInitial()
	{
		return initial;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public boolean isMandatory()
	{
		return true; // list can be empty but not null;
	}

	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(List.class, sources[0].getValueClass());
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = sources[0].getInitialExceptions();
		for(int i = 1; i<sources.length; i++)
			result.addAll(sources[i].getInitialExceptions());
		return result;
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	@Override
	public List<E> get(final Item item)
	{
		final int length = this.length.getMandatory(item);
		final ArrayList<E> result = new ArrayList<>(length);

		for(int i = 0; i<length; i++)
		{
			result.add(sources[i].get(item));
		}
		return Collections.unmodifiableList(result);
	}

	private void assertValue(final Collection<?> value, final Item exceptionItem)
	{
		if(value.size()>sources.length)
			throw new ListSizeViolationException(this, exceptionItem, value.size(), sources.length);
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=Thrown.class)
	@Override
	public void set(final Item item, final Collection<? extends E> value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			StringLengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		assertValue(value, item);
		int i = 0;
		final SetValue<?>[] setValues = new SetValue<?>[sources.length+1];

		for(final Iterator<? extends E> it = value.iterator(); it.hasNext(); i++)
			setValues[i] = sources[i].map(it.next());

		final int length = i;

		for(; i<sources.length; i++)
			setValues[i] = sources[i].map(null);

		setValues[i] = this.length.map(length);

		item.set(setValues);
	}

	private static final class Thrown implements ThrownGetter<LimitedListField<?>>
	{
		public Set<Class<? extends Throwable>> get(final LimitedListField<?> feature)
		{
			final Set<Class<? extends Throwable>> result = feature.getInitialExceptions();
			result.add(ClassCastException.class);
			result.add(ListSizeViolationException.class);
			return result;
		}
	}

	public SetValue<Collection<E>> map(final Collection<E> value)
	{
		return SetValue.map(this, value);
	}

	public SetValue<?>[] execute(final Collection<E> value, final Item exceptionItem)
	{
		assertValue(value, exceptionItem);
		int i = 0;
		final SetValue<?>[] result = new SetValue<?>[sources.length+1];

		for(final Object v : value)
			result[i] = Cope.mapAndCast(sources[i++], v);

		final int length = i;

		for(; i<sources.length; i++)
			result[i] = Cope.mapAndCast(sources[i], null);

		result[i] = this.length.map(length);

		return result;
	}

	public Condition equal(final Collection<E> value)
	{
		int i = 0;
		final Condition[] conditions = new Condition[sources.length];

		for(final Iterator<E> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = sources[i].equal(it.next());

		for(; i<sources.length; i++)
			conditions[i] = sources[i].equal((E)null);

		return Cope.and(conditions);
	}

	public Condition notEqual(final Collection<E> value)
	{
		int i = 0;
		final Condition[] conditions = new Condition[sources.length];

		for(final E v : value)
		{
			conditions[i] = sources[i].notEqual(v).or(sources[i].isNull());
			i++;
		}

		for(; i<sources.length; i++)
			conditions[i] = sources[i].isNotNull();

		return Cope.or(conditions);
	}

	public Condition contains(final E value)
	{
		final Condition[] conditions = new Condition[sources.length];

		for(int i = 0; i<sources.length; i++)
			conditions[i] = sources[i].equal(value);

		return Cope.or(conditions);
	}

	// todo remove duplicate code
	public final Condition contains(final Join join, E value)
	{
		final Condition[] conditions = new Condition[sources.length];

		for(int i = 0; i<sources.length; i++)
			conditions[i] = sources[i].bind(join).equal(value);

		return Cope.or(conditions);
	}

	public Condition containsAny(final Collection<E> set)
	{
		final Condition[] conditions = new Condition[set.size()];
		int i = 0;
		for(final E item : set)
			conditions[i++] = contains(item);

		return Cope.or(conditions);
	}

	public final Condition containsAny(final Join join, final Collection<E> set)
	{
		final Condition[] conditions = new Condition[set.size()];
		int i = 0;
		for(final E item : set)
			conditions[i++] = contains(join, item);

		return Cope.or(conditions);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #create(FunctionField,FunctionField)} instead
	 */
	@Deprecated
	public static final <E> LimitedListField<E> newList(final FunctionField<E> source1, final FunctionField<E> source2)
	{
		return create(source1, source2);
	}

	/**
	 * @deprecated Use {@link #create(FunctionField,FunctionField,FunctionField)} instead
	 */
	@Deprecated
	public static final <E> LimitedListField<E> newList(final FunctionField<E> source1, final FunctionField<E> source2, final FunctionField<E> source3)
	{
		return create(source1, source2, source3);
	}

	/**
	 * @deprecated Use {@link #create(FunctionField,int)} instead
	 */
	@Deprecated
	public static final <E> LimitedListField<E> newList(final FunctionField<E> template, final int maximumSize)
	{
		return create(template, maximumSize);
	}
}

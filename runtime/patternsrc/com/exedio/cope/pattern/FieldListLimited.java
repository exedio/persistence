/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.Wrapper;

public final class FieldListLimited<E> extends Pattern implements Settable<Collection<E>>
{
	private final FunctionField<E>[] sources;
	private final boolean initial;
	private final boolean isFinal;

	private FieldListLimited(final FunctionField<E>[] sources)
	{
		this.sources = sources;

		boolean initial = false;
		boolean isFinal = false;
		for(FunctionField<E> source : sources)
		{
			registerSource(source);
			initial = initial || source.isInitial();
			isFinal = isFinal || source.isFinal();
		}
		this.initial = initial;
		this.isFinal = isFinal;
	}
	
	private FieldListLimited(final FunctionField<E> source1, final FunctionField<E> source2)
	{
		this(FieldListLimited.<E>cast(new FunctionField[]{source1, source2}));
	}
	
	private FieldListLimited(final FunctionField<E> source1, final FunctionField<E> source2, final FunctionField<E> source3)
	{
		this(FieldListLimited.<E>cast(new FunctionField[]{source1, source2, source3}));
	}
	
	private FieldListLimited(final FunctionField<E> template, final int maximumSize)
	{
		this(template2Sources(template, maximumSize));
	}
	
	public static final <E> FieldListLimited<E> newList(final FunctionField<E> source1, final FunctionField<E> source2)
	{
		return new FieldListLimited<E>(source1, source2);
	}
	
	public static final <E> FieldListLimited<E> newList(final FunctionField<E> source1, final FunctionField<E> source2, final FunctionField<E> source3)
	{
		return new FieldListLimited<E>(source1, source2, source3);
	}
	
	public static final <E> FieldListLimited<E> newList(final FunctionField<E> template, final int maximumSize)
	{
		return new FieldListLimited<E>(template, maximumSize);
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private final static <X> FunctionField<X>[] cast(final FunctionField[] o)
	{
		return o;
	}
	
	private final static <Y> FunctionField<Y>[] template2Sources(final FunctionField<Y> template, final int maximumSize)
	{
		final FunctionField<Y>[] result = cast(new FunctionField[maximumSize]);
		
		for(int i = 0; i<maximumSize; i++)
			result[i] = template.copy();

		return result;
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		
		for(int i = 0; i<sources.length; i++)
		{
			final FunctionField<E> source = sources[i];
			if(!source.isInitialized())
				initialize(source, name+(i+1/*TODO: make this '1' customizable*/));
		}
	}
	
	public List<FunctionField<E>> getSources()
	{
		return Collections.unmodifiableList(Arrays.asList(sources));
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(new Wrapper(
			ParameterizedTypeImpl.make(List.class, new java.lang.reflect.Type[]{Wrapper.TypeVariable0.class}, null),
			"get",
			"Returns the contents of the field list {0}.",
			null, null));
		
		return Collections.unmodifiableList(result);
	}
	
	public boolean isInitial()
	{
		return initial;
	}
	
	public boolean isFinal()
	{
		return isFinal;
	}
	
	public Set<Class> getSetterExceptions()
	{
		final Set<Class> result = sources[0].getSetterExceptions();
		for(int i = 1; i<sources.length; i++)
			result.addAll(sources[i].getSetterExceptions());
		return result;
	}
	
	public List<E> get(final Item item)
	{
		final ArrayList<E> result = new ArrayList<E>(sources.length);

		for(int i = 0; i<sources.length; i++)
		{
			final E value = sources[i].get(item);
			if(value!=null)
				result.add(value);
		}
		return result;
	}
	
	private void assertValue(final Collection<?> value)
	{
		if(value.size()>sources.length)
			throw new IllegalArgumentException("value exceeds limit " + sources.length + " for " + toString() + ": " + value);
	}
	
	public void set(final Item item, final Collection<? extends E> value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		assertValue(value);
		int i = 0;
		final SetValue[] setValues = new SetValue[sources.length];

		for(Iterator<? extends E> it = value.iterator(); it.hasNext(); i++)
			setValues[i] = sources[i].map(it.next());

		for(; i<sources.length; i++)
			setValues[i] = sources[i].map(null);
		
		try
		{
			item.set(setValues);
		}
		catch(CustomAttributeException e)
		{
			// cannot happen, since FunctionField only are allowed for source
			throw new RuntimeException(e);
		}
	}
	
	public SetValue<Collection<E>> map(final Collection<E> value)
	{
		return new SetValue<Collection<E>>(this, value);
	}
	
	public SetValue[] execute(final Collection value, final Item exceptionItem)
	{
		assertValue(value);
		int i = 0;
		final SetValue[] result = new SetValue[sources.length];

		for(final Object v : value)
			result[i] = Cope.mapAndCast(sources[i++], v);

		for(; i<sources.length; i++)
			result[i] = Cope.mapAndCast(sources[i], null);
		
		return result;
	}
	
	public Condition equal(final Collection<E> value)
	{
		int i = 0;
		final Condition[] conditions = new Condition[sources.length];
		
		for(Iterator<E> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = sources[i].equal(it.next());

		for(; i<sources.length; i++)
			conditions[i] = sources[i].equal((E)null);

		return Cope.and(conditions);
	}
	
	public Condition notEqual(final Collection<E> value)
	{
		int i = 0;
		final Condition[] conditions = new Condition[sources.length];
		
		for(E v : value)
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
}

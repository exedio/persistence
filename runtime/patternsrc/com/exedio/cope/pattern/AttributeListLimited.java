/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.SortedSet;

import com.exedio.cope.CompositeCondition;
import com.exedio.cope.Cope;
import com.exedio.cope.EqualCondition;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.NotEqualCondition;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.UniqueViolationException;

public final class AttributeListLimited<T> extends Pattern implements Settable<Collection<T>>
{
	private final FunctionAttribute<T>[] sources;
	private final boolean initial;
	private final boolean isFinal;

	public AttributeListLimited(final FunctionAttribute<T>[] sources)
	{
		this.sources = sources;

		boolean initial = false;
		boolean isFinal = false;
		for(FunctionAttribute<T> source : sources)
		{
			registerSource(source);
			initial = initial || source.isInitial();
			isFinal = isFinal || source.isFinal();
		}
		this.initial = initial;
		this.isFinal = isFinal;
	}
	
	public AttributeListLimited(final FunctionAttribute<T> source1, final FunctionAttribute<T> source2)
	{
		this(AttributeListLimited.<T>cast(new FunctionAttribute[]{source1, source2}));
	}
	
	public AttributeListLimited(final FunctionAttribute<T> source1, final FunctionAttribute<T> source2, final FunctionAttribute<T> source3)
	{
		this(AttributeListLimited.<T>cast(new FunctionAttribute[]{source1, source2, source3}));
	}
	
	public AttributeListLimited(final FunctionAttribute<T> template, final int maximumSize)
	{
		this(template2Sources(template, maximumSize));
	}
	
	public static final <T> AttributeListLimited<T> newVector(final FunctionAttribute<T> source1, final FunctionAttribute<T> source2)
	{
		return new AttributeListLimited<T>(source1, source2);
	}
	
	public static final <T> AttributeListLimited<T> newVector(final FunctionAttribute<T> source1, final FunctionAttribute<T> source2, final FunctionAttribute<T> source3)
	{
		return new AttributeListLimited<T>(source1, source2, source3);
	}
	
	public static final <T> AttributeListLimited<T> newVector(final FunctionAttribute<T> template, final int maximumSize)
	{
		return new AttributeListLimited<T>(template, maximumSize);
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private final static <X> FunctionAttribute<X>[] cast(final FunctionAttribute[] o)
	{
		return (FunctionAttribute<X>[])o;
	}
	
	private final static <Y> FunctionAttribute<Y>[] template2Sources(final FunctionAttribute<Y> template, final int maximumSize)
	{
		final FunctionAttribute<Y>[] result = cast(new FunctionAttribute[maximumSize]);
		
		for(int i = 0; i<maximumSize; i++)
			result[i] = template.copyFunctionAttribute();

		return result;
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		
		for(int i = 0; i<sources.length; i++)
		{
			final FunctionAttribute<T> source = sources[i];
			if(!source.isInitialized())
				initialize(source, name+(i+1/*TODO: make this '1' customizable*/));
		}
	}
	
	public List<FunctionAttribute<T>> getSources()
	{
		return Collections.unmodifiableList(Arrays.asList(sources));
	}
	
	public boolean isInitial()
	{
		return initial;
	}
	
	public boolean isFinal()
	{
		return isFinal;
	}
	
	public SortedSet<Class> getSetterExceptions()
	{
		final SortedSet<Class> result = sources[0].getSetterExceptions();
		for(int i = 1; i<sources.length; i++)
			result.addAll(sources[i].getSetterExceptions());
		return result;
	}
	
	public List<T> get(final Item item)
	{
		final ArrayList<T> result = new ArrayList<T>(sources.length);

		for(int i = 0; i<sources.length; i++)
		{
			final T value = sources[i].get(item);
			if(value!=null)
				result.add(value);
		}
		return result;
	}
	
	public void set(final Item item, final Collection<? extends T> value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		int i = 0;
		final SetValue[] setValues = new SetValue[sources.length];

		for(Iterator<? extends T> it = value.iterator(); it.hasNext(); i++)
			setValues[i] = sources[i].map(it.next());

		for(; i<sources.length; i++)
			setValues[i] = sources[i].map(null);
		
		try
		{
			item.set(setValues);
		}
		catch(CustomAttributeException e)
		{
			// cannot happen, since FunctionAttribute only are allowed for source
			throw new RuntimeException(e);
		}
	}
	
	public SetValue<Collection<T>> map(final Collection<T> value)
	{
		return new SetValue<Collection<T>>(this, value);
	}
	
	public SetValue[] execute(final Collection value, final Item exceptionItem)
	{
		int i = 0;
		final SetValue[] result = new SetValue[sources.length];

		for(final Object v : value)
			result[i] = Cope.mapAndCast(sources[i++], v);

		for(; i<sources.length; i++)
			result[i] = Cope.mapAndCast(sources[i], null);
		
		return result;
	}
	
	public CompositeCondition equal(final Collection<T> value)
	{
		int i = 0;
		final EqualCondition[] conditions = new EqualCondition[sources.length];
		
		for(Iterator<T> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = sources[i].equal(it.next());

		for(; i<sources.length; i++)
			conditions[i] = sources[i].equal((T)null);

		return Cope.and(conditions);
	}
	
	public CompositeCondition notEqual(final Collection<T> value)
	{
		int i = 0;
		final NotEqualCondition[] conditions = new NotEqualCondition[sources.length];
		
		for(Iterator<T> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = sources[i].notEqual(it.next());

		for(; i<sources.length; i++)
			conditions[i] = sources[i].notEqual(null);

		return Cope.or(conditions);
	}

	public CompositeCondition contains(final T value)
	{
		final EqualCondition[] conditions = new EqualCondition[sources.length];
		
		for(int i = 0; i<sources.length; i++)
			conditions[i] = sources[i].equal(value);

		return Cope.or(conditions);
	}
	
}

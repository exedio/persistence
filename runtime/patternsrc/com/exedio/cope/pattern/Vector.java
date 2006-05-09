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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

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
import com.exedio.cope.search.AndCondition;
import com.exedio.cope.search.OrCondition;

public final class Vector<T> extends Pattern implements Settable<Collection<T>>
{
	private final FunctionAttribute<T>[] sources;
	private final boolean initial;
	private final boolean isFinal;

	public Vector(final FunctionAttribute<T>[] sources)
	{
		this.sources = sources;

		// TODO SOON
		boolean initial = false;
		boolean isFinal = false;
		for(int i = 0; i<sources.length; i++)
		{
			registerSource(sources[i]);
			initial = initial || sources[i].isInitial();
			isFinal = isFinal || sources[i].isFinal();
		}
		this.initial = initial;
		this.isFinal = isFinal;
	}
	
	public Vector(final FunctionAttribute<T> source1)
	{
		this(Vector.<T>cast(new FunctionAttribute[]{source1}));
	}
	
	public Vector(final FunctionAttribute<T> source1, final FunctionAttribute<T> source2)
	{
		this(Vector.<T>cast(new FunctionAttribute[]{source1, source2}));
	}
	
	public Vector(final FunctionAttribute<T> source1, final FunctionAttribute<T> source2, final FunctionAttribute<T> source3)
	{
		this(Vector.<T>cast(new FunctionAttribute[]{source1, source2, source3}));
	}
	
	public Vector(final FunctionAttribute<T> template, final int length)
	{
		this(template2Sources(template, length));
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private final static <X> FunctionAttribute<X>[] cast(final FunctionAttribute[] o)
	{
		return (FunctionAttribute<X>[])o;
	}
	
	private final static <Y> FunctionAttribute<Y>[] template2Sources(final FunctionAttribute<Y> template, final int length)
	{
		final FunctionAttribute<Y>[] result = cast(new FunctionAttribute[length]);
		
		for(int i = 0; i<length; i++)
			result[i] = template.copyFunctionAttribute();

		return result;
	}
	
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
	
	public List<Object> get(final Item item)
	{
		final ArrayList<Object> result = new ArrayList<Object>(sources.length);

		for(int i = 0; i<sources.length; i++)
		{
			final Object value = sources[i].get(item);
			if(value!=null)
				result.add(value);
		}
		return result;
	}
	
	public void set(final Item item, final Collection value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		int i = 0;
		final SetValue[] setValues = new SetValue[sources.length];

		for(Iterator it = value.iterator(); it.hasNext(); i++)
			setValues[i] = new SetValue(sources[i], it.next());

		for(; i<sources.length; i++)
			setValues[i] = new SetValue(sources[i], null);
		
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
	
	public SetValue map(final Collection value)
	{
		return new SetValue(this, value);
	}
	
	public Map<? extends FunctionAttribute, ? extends Object> execute(final Collection value, final Item exceptionItem)
	{
		int i = 0;
		final HashMap<FunctionAttribute, Object> result = new HashMap<FunctionAttribute, Object>();

		for(final Object v : value)
			result.put(sources[i++], v);

		for(; i<sources.length; i++)
			result.put(sources[i], null);
		
		return result;
	}
	
	public AndCondition equal(final Collection<T> value)
	{
		int i = 0;
		final EqualCondition[] conditions = new EqualCondition[sources.length];
		
		for(Iterator<T> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = new EqualCondition<T>(sources[i], it.next());

		for(; i<sources.length; i++)
			conditions[i] = new EqualCondition<T>(sources[i], null);

		return new AndCondition(conditions);
	}
	
	public OrCondition notEqual(final Collection<T> value)
	{
		int i = 0;
		final NotEqualCondition[] conditions = new NotEqualCondition[sources.length];
		
		for(Iterator<T> it = value.iterator(); it.hasNext(); i++)
			conditions[i] = new NotEqualCondition<T>(sources[i], it.next());

		for(; i<sources.length; i++)
			conditions[i] = new NotEqualCondition<T>(sources[i], null);

		return new OrCondition(conditions);
	}

	public OrCondition contains(final T value)
	{
		final EqualCondition[] conditions = new EqualCondition[sources.length];
		
		for(int i = 0; i<sources.length; i++)
			conditions[i] = new EqualCondition<T>(sources[i], value);

		return new OrCondition(conditions);
	}
	
}

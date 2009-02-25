/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Condition;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Range<E> extends Pattern implements Settable<Range.Value<E>>
{
	private final FunctionField<E> from;
	private final FunctionField<E> to;
	
	private Range(final FunctionField<E> borderTemplate)
	{
		addSource(from = borderTemplate.copy(), "From");
		addSource(to   = borderTemplate.copy(), "To");
	}
	
	public static final <E> Range<E> newRange(final FunctionField<E> borderTemplate)
	{
		if(!borderTemplate.isMandatory())
			throw new IllegalArgumentException("optional borderTemplate not yet implemented");
		if(borderTemplate.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("unique borderTemplate is not supported");
		
		return new Range<E>(borderTemplate);
	}
	
	public FunctionField<E> getFrom()
	{
		return from;
	}
	
	public FunctionField<E> getTo()
	{
		return to;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("get").
			setReturn(Wrapper.generic(Value.class, from.getValueClass())));
		
		result.add(
			new Wrapper("set").
			addParameter(Wrapper.genericExtends(Value.class, from.getValueClass())));
			
		result.add(
			new Wrapper("getFrom").
			setReturn(Wrapper.TypeVariable0.class));
		
		result.add(
			new Wrapper("getTo").
			setReturn(Wrapper.TypeVariable0.class));
		
		result.add(
			new Wrapper("setFrom").
			addParameter(Wrapper.TypeVariable0.class));
		
		result.add(
			new Wrapper("setTo").
			addParameter(Wrapper.TypeVariable0.class));
		
		return Collections.unmodifiableList(result);
	}
	
	public Value<E> get(final Item item)
	{
		return new Value<E>(from.get(item), to.get(item));
	}
	
	public void set(final Item item, final Value<? extends E> value)
	{
		item.set(
				this.from.map(value.from),
				this.to  .map(value.to  ));
	}
	
	public E getFrom(final Item item)
	{
		return from.get(item);
	}
	
	public E getTo(final Item item)
	{
		return to.get(item);
	}
	
	public void setFrom(final Item item, final E from)
	{
		this.from.set(item, from);
	}
	
	public void setTo(final Item item, final E to)
	{
		this.to.set(item, to);
	}
	
	public Condition contains(final E value)
	{
		return from.isMandatory() 
			? from.lessOrEqual(value).and(to.greaterOrEqual(value))
			: from.isNull().or(from.lessOrEqual(value)).and(to.isNull().or(to.greaterOrEqual(value)));
	}
	
	public static final class Value<E>
	{
		final E from;
		final E to;

		public Value(E from, E to)
		{
			if(from==null)
				throw new NullPointerException("optional from not yet implemented");
			if(to==null)
				throw new NullPointerException("optional to not yet implemented");
			
			this.from = from;
			this.to = to;
		}
		
		@Override
		public boolean equals(final Object other)
		{
			if(!(other instanceof Range.Value))
				return false;
			
			final Range.Value o = (Range.Value)other;
			return from.equals(o.from) && to.equals(o.to);
		}
		
		@Override
		public int hashCode()
		{
			return from.hashCode() ^ (to.hashCode() << 2);
		}
	}
	
	public SetValue map(final Value<E> value)
	{
		return new SetValue<Value<E>>(this, value);
	}
	
	public SetValue[] execute(Value<E> value, Item exceptionItem)
	{
		//TODO test valid days
		return new SetValue[]{from.map(value.from), to.map(value.to)};
	}
	
	public boolean isFinal()
	{
		return from.isFinal();
	}
	
	public boolean isInitial()
	{
		return from.isInitial();
	}
	
	public Class getInitialType()
	{
		return Range.Value.class;
	}
	
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return from.getInitialExceptions();
	}
}

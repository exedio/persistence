/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrapper;

public final class PriceField extends Pattern implements Settable<Price>
{
	private final IntegerField integer;
	
	public PriceField()
	{
		this(new IntegerField());
	}
	
	private PriceField(final IntegerField integer)
	{
		this.integer = integer;
		addSource(integer, "Int");
	}
	
	public PriceField toFinal()
	{
		return new PriceField(integer.toFinal());
	}
	
	public PriceField optional()
	{
		return new PriceField(integer.optional());
	}
	
	public PriceField min(final int minimum)
	{
		return new PriceField(integer.min(minimum));
	}
	
	public IntegerField getInt()
	{
		return integer;
	}
	
	public boolean isInitial()
	{
		return integer.isInitial();
	}
	
	public boolean isFinal()
	{
		return integer.isFinal();
	}
	
	public Class getInitialType()
	{
		return Price.class;
	}
	
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return integer.getInitialExceptions();
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("get").
			addComment("Returns the value of {0}.").
			setReturn(Price.class));
		
		if(!isFinal())
		{
			result.add(
				new Wrapper("set").
				addComment("Sets a new value for {0}.").
				addThrows(getInitialExceptions()).
				addParameter(Price.class));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	public Price get(final Item item)
	{
		return Price.storeOf(integer.get(item));
	}
	
	public void set(final Item item, final Price value)
	{
		integer.set(item, value!=null ? value.store : null);
	}
	
	public SetValue<Price> map(final Price value)
	{
		return new SetValue<Price>(this, value);
	}
	
	public SetValue[] execute(final Price value, final Item exceptionItem)
	{
		return new SetValue[]{ integer.map(value!=null ? value.store : null) };
	}
}

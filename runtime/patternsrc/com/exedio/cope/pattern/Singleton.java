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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.ComputedInstance;

public final class Singleton extends Pattern
{
	private static final long serialVersionUID = 1l;
	
	private static final int THE_ONE = 42;
	private static final Integer THE_ONE_OBJECT = Integer.valueOf(THE_ONE);
	
	private final IntegerField source =
		new IntegerField().toFinal().unique().
				defaultTo(THE_ONE_OBJECT).range(THE_ONE, THE_ONE+1);
	
	public Singleton()
	{
		addSource(source, "Once", ComputedInstance.get());
	}
	
	public IntegerField getSource()
	{
		return source;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("instance").
			addComment("Gets the single instance of {2}.").
			addComment("Creates an instance, if none exists.").
			setMethodWrapperPattern("instance").
			setStatic().
			setReturn(Wrapper.ClassVariable.class, "never returns null."));
		
		return Collections.unmodifiableList(result);
	}
	
	public final <P extends Item> P instance(final Class<P> typeClass)
	{
		final Type<P> type = getType().as(typeClass);
		final P found = type.searchSingleton(source.equal(THE_ONE_OBJECT));
		if(found!=null)
			return found;
		else
			return type.newItem();
	}
}

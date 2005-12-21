/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.AttributeValue;
import com.exedio.cope.EqualCondition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.NotEqualCondition;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.search.AndCondition;
import com.exedio.cope.search.OrCondition;

public final class Vector extends Pattern
{
	private final ObjectAttribute[] sources;

	public Vector(final ObjectAttribute[] sources)
	{
		this.sources = sources;

		for(int i = 0; i<sources.length; i++)
			registerSource(sources[i]);
	}
	
	public Vector(final ObjectAttribute template, final int length)
	{
		this(template2Sources(template, length));
	}
	
	private static final ObjectAttribute[] template2Sources(final ObjectAttribute template, final int length)
	{
		final ObjectAttribute[] result = new ObjectAttribute[length];
		
		for(int i = 0; i<length; i++)
			result[i] = template.copyAsTemplate();

		return result;
	}
	
	public void initialize()
	{
		final String name = getName();
		
		for(int i = 0; i<sources.length; i++)
		{
			final ObjectAttribute source = sources[i];
			if(!source.isInitialized())
				initialize(source, name+(i+1/*TODO: make this '1' customizable*/));
		}
	}
	
	public final List getSources()
	{
		return Collections.unmodifiableList(Arrays.asList(sources));
	}
	
	public List get(final Item item)
	{
		final ArrayList result = new ArrayList(sources.length);

		for(int i = 0; i<sources.length; i++)
		{
			final Object value = item.get(sources[i]);
			if(value!=null)
				result.add(value);
		}
		return result;
	}
	
	public void set(final Item item, final Collection values)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			ReadOnlyViolationException,
			ClassCastException
	{
		int i = 0;
		final AttributeValue[] attributeValues = new AttributeValue[sources.length];

		for(Iterator it = values.iterator(); it.hasNext(); i++)
			attributeValues[i] = new AttributeValue(sources[i], it.next());

		for(; i<sources.length; i++)
			attributeValues[i] = new AttributeValue(sources[i], null);
		
		item.set(attributeValues);
	}
	
	public final AndCondition equal(final Collection values)
	{
		int i = 0;
		final EqualCondition[] conditions = new EqualCondition[sources.length];
		
		for(Iterator it = values.iterator(); it.hasNext(); i++)
			conditions[i] = new EqualCondition((Join)null, sources[i], it.next());

		for(; i<sources.length; i++)
			conditions[i] = new EqualCondition((Join)null, sources[i], null);

		return new AndCondition(conditions);
	}
	
	public final OrCondition notEqual(final Collection values)
	{
		int i = 0;
		final NotEqualCondition[] conditions = new NotEqualCondition[sources.length];
		
		for(Iterator it = values.iterator(); it.hasNext(); i++)
			conditions[i] = new NotEqualCondition(sources[i], it.next());

		for(; i<sources.length; i++)
			conditions[i] = new NotEqualCondition(sources[i], null);

		return new OrCondition(conditions);
	}

	public final OrCondition contains(final Object value)
	{
		final EqualCondition[] conditions = new EqualCondition[sources.length];
		
		for(int i = 0; i<sources.length; i++)
			conditions[i] = new EqualCondition((Join)null, sources[i], value);

		return new OrCondition(conditions);
	}
	
}

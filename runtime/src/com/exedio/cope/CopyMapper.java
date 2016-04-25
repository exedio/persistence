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

import java.util.HashMap;

public final class CopyMapper
{
	private final HashMap<Feature, Feature> map = new HashMap<>();

	public <F extends Feature> F get(final F template)
	{
		final Feature mapped = map.get(template);
		if(mapped==null)
			throw new IllegalArgumentException("not mapped " + template);
		@SuppressWarnings("unchecked")
		final F casted = (F)mapped;
		return casted;
	}

	public FunctionField<?>[] get(final FunctionField<?>[] templates)
	{
		final FunctionField<?>[] result = new FunctionField<?>[templates.length];
		for(int i = 0; i<templates.length; i++)
			result[i] = get(templates[i]);
		return result;
	}

	public <F extends Function<?>> F getF(final F template)
	{
		if(!(template instanceof FunctionField))
				throw new RuntimeException("not yet implemented: " + template);

		return (F)get((FunctionField<?>)template);
	}

	public <F extends FunctionField<?>> F copy(final F template)
	{
		@SuppressWarnings("unchecked")
		final F component = (F)template.copy();
		return put(template, component);
	}

	public <F extends Feature> F put(final F template, final F component)
	{
		final Feature mapped = map.put(template, component);
		if(mapped!=null && mapped!=component)
			throw new IllegalArgumentException("already mapped " + template);
		return component;
	}
}

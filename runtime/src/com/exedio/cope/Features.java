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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Features
{
	private final LinkedHashMap<String, Feature> map;
	private final IdentityHashMap<Feature, AnnotatedElement> annotationSources;

	public Features()
	{
		map = new LinkedHashMap<>();
		annotationSources = new IdentityHashMap<>();
	}

	public void put(final String name, final Feature feature, final AnnotatedElement annotationSource)
	{
		requireNonNull(name, "name");
		if(!"BEANSHELL_HACK_ATTRIBUTE".equals(name)) // TODO
		{
			final int i = Feature.NAME_CHAR_SET.indexOfNotContains(name);
			if(i>=0)
				throw new IllegalArgumentException("name >" + name + "< contains illegal character >" + name.charAt(i) + "< at position " + i);
		}
		requireNonNull(feature, "feature");
		if(map.containsKey(name))
			throw new IllegalArgumentException("already contains the name >" + name + '<');
		if(annotationSources.containsKey(feature))
			throw new IllegalArgumentException("already contains the feature >" + feature + '<');

		map.put(name, feature);
		if(annotationSources.put(feature, annotationSource)!=null)
			throw new RuntimeException();
	}

	public void put(final String name, final Feature feature)
	{
		put(name, feature, null);
	}

	public void clear()
	{
		map.clear();
		annotationSources.clear();
	}

	int size()
	{
		return map.size();
	}

	void mount(final Type<?> type)
	{
		for(final Map.Entry<String, Feature> entry : map.entrySet())
		{
			final Feature feature = entry.getValue();
			feature.mount(type, entry.getKey(), annotationSources.get(feature));
		}
	}

	List<Feature> mountPattern(
			final Pattern pattern,
			final Type<?> type,
			final String name)
	{
		final ArrayList<Feature> result = new ArrayList<>();

		for(final Map.Entry<String, Feature> entry : map.entrySet())
		{
			final Feature source = entry.getValue();
			final String postfix = entry.getKey();
			if(!source.isMountedToType())
				source.mount(type, name + '-' + postfix, annotationSources.get(source));
			final Type<?> sourceType = source.getType();
			//System.out.println("----------check"+source);
			if(!sourceType.equals(type))
				throw new RuntimeException(
						"Source " + source + " of pattern " + pattern + " must be declared on the same type, " +
						"expected " + type + ", but was " + sourceType + '.');
			result.add(source);
		}

		return List.copyOf(result);
	}
}

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

package com.exedio.cope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Features
{
	private final LinkedHashMap<String, Feature> map;
	private final HashSet<Feature> set;
	private HashMap<Feature, java.lang.reflect.Field> annotationSources = null;
	
	public Features()
	{
		map = new LinkedHashMap<String, Feature>();
		set = new HashSet<Feature>();
	}
	
	@Deprecated
	Features(final LinkedHashMap<String, Feature> map)
	{
		this.map = map;
		this.set = new HashSet<Feature>(map.values());
		if(map.size()!=set.size())
			throw new IllegalArgumentException("map contains duplicate features: " + map.toString());
	}
	
	public void put(final String name, final Feature feature, final java.lang.reflect.Field annotationSource)
	{
		if(name==null)
			throw new NullPointerException("name");
		if(feature==null)
			throw new NullPointerException("feature");
		if(map.containsKey(name))
			throw new IllegalArgumentException("already contains the name >" + name + '<');
		if(set.contains(feature))
			throw new IllegalArgumentException("already contains the feature >" + feature.toString() + '<');
		
		map.put(name, feature);
		set.add(feature);
		if(annotationSource!=null)
		{
			if(annotationSources==null)
				annotationSources = new HashMap<Feature, java.lang.reflect.Field>();
			if(annotationSources.put(feature, annotationSource)!=null)
				throw new RuntimeException();
		}
	}
	
	public void put(final String name, final Feature feature)
	{
		put(name, feature, null);
	}
	
	public void clear()
	{
		map.clear();
		set.clear();
		if(annotationSources!=null)
			annotationSources.clear();
	}
	
	int size()
	{
		return map.size();
	}
	
	private java.lang.reflect.Field getAnnotationSource(final Feature feature)
	{
		if(annotationSources==null)
			return null;
		
		return annotationSources.get(feature);
	}
	
	void mount(final Type<?> type)
	{
		for(final Map.Entry<String, Feature> entry : map.entrySet())
			entry.getValue().mount(type, entry.getKey(), getAnnotationSource(entry.getValue()));
	}
}

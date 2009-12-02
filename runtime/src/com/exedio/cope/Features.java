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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class Features
{
	private final LinkedHashMap<String, Feature> map;
	
	public Features()
	{
		map = new LinkedHashMap<String, Feature>();
	}
	
	@Deprecated
	Features(final LinkedHashMap<String, Feature> map)
	{
		this.map = map;
	}
	
	public void put(final String name, final Feature feature)
	{
		if(name==null)
			throw new NullPointerException("name");
		if(feature==null)
			throw new NullPointerException("feature");
		if(map.containsKey(name))
			throw new IllegalArgumentException("already contains the name " + name + '<');
		
		map.put(name, feature);
	}
	
	public void clear()
	{
		map.clear();
	}
	
	int size()
	{
		return map.size();
	}
	
	Set<Map.Entry<String, Feature>> entrySet()
	{
		return Collections.unmodifiableSet(map.entrySet());
	}
}

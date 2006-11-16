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

package com.exedio.cope;

import java.util.ArrayList;
import java.util.HashSet;

final class TC
{
	private final Query query;
	private final HashSet<Type> queryTypes = new HashSet<Type>();
	private final HashSet<Type> distinctThisTypes = new HashSet<Type>();
	private HashSet<Type> ambiguousThisTypes = null;
	private final HashSet<Type> distinctAllTypes = new HashSet<Type>();
	private HashSet<Type> ambiguousAllTypes = null;
	private boolean frozen = false;
	
	TC(final Query<?> query)
	{
		this.query = query;

		for(Type t = query.type; t!=null; t=t.supertype)
			queryTypes.add(t);

		putType(query.type);

		final ArrayList<Join> joins = query.joins;
		if(joins!=null)
			for(final Join join : joins)
				putType(join.type);
		
		frozen = true;
	}
	
	private void putType(final Type type)
	{
		assert !frozen;

		boolean isThis = true;
		for(Type t = type; t!=null; t=t.supertype)
		{
			ambiguousAllTypes = putType(t, distinctAllTypes, ambiguousAllTypes);
			
			if(isThis)
			{
				ambiguousThisTypes = putType(t, distinctThisTypes, ambiguousThisTypes);
				isThis = false;
			}
		}
	}

	private static HashSet<Type> putType(final Type type, final HashSet<Type> distinctTypes, HashSet<Type> ambiguousTypes)
	{
		if(ambiguousTypes!=null && ambiguousTypes.contains(type))
			return ambiguousTypes;
		
		if(!distinctTypes.add(type))
		{
			distinctTypes.remove(type);
			if(ambiguousTypes==null)
				ambiguousTypes = new HashSet<Type>();
			ambiguousTypes.add(type);
		}
		return ambiguousTypes;
	}
	
	void check(final FunctionField select, final Join join)
	{
		check(select, join, distinctAllTypes, ambiguousAllTypes);
	}

	void check(final Type.This select, final Join join)
	{
		check(select, join, distinctThisTypes, ambiguousThisTypes);
	}

	private void check(final Selectable select, final Join join, final HashSet<Type> distinctTypes, final HashSet<Type> ambiguousTypes)
	{
		assert frozen;
		
		final Type selectType = select.getType();
		
		if(join==null)
		{
			if(queryTypes.contains(selectType))
				return;
			
			if(distinctTypes.contains(selectType))
				return;
	
			if(ambiguousTypes!=null && ambiguousTypes.contains(selectType))
				throw new IllegalArgumentException(
						select.toString() + " is ambiguous, use Function#bind in query: " + query.toString()
						/*+ "---" + distinctTypes + "---" + ambiguousTypes*/);
		}
		else
		{
			if(distinctTypes.contains(selectType))
				return;
	
			if(ambiguousTypes!=null && ambiguousTypes.contains(selectType))
				return;
		}

		throw new IllegalArgumentException(select.toString() + " does not belong to a type of the query: " + query.toString());
	}
}

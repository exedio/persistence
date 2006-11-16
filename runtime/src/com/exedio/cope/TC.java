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
import java.util.HashMap;
import java.util.HashSet;

final class TC
{
	private final Query query;
	private final HashSet<Type> queryTypes = new HashSet<Type>();
	private final HashMap<Type, Join> distinctThisTypes = new HashMap<Type, Join>();
	private HashSet<Type> ambiguousThisTypes = null;
	private final HashMap<Type, Join> distinctAllTypes = new HashMap<Type, Join>();
	private HashSet<Type> ambiguousAllTypes = null;
	private boolean frozen = false;
	private final HashMap<Join, HashSet<Table>> tables = new HashMap<Join, HashSet<Table>>();
	
	TC(final Query<?> query)
	{
		this.query = query;

		for(Type t = query.type; t!=null; t=t.supertype)
			queryTypes.add(t);

		putType(query.type, null);

		final ArrayList<Join> joins = query.joins;
		if(joins!=null)
			for(final Join join : joins)
				putType(join.type, join);
		
		frozen = true;
	}
	
	private void putType(final Type type, final Join join)
	{
		assert !frozen;

		boolean isThis = true;
		for(Type t = type; t!=null; t=t.supertype)
		{
			ambiguousAllTypes = putType(t, join, distinctAllTypes, ambiguousAllTypes);
			
			if(isThis)
			{
				ambiguousThisTypes = putType(t, join, distinctThisTypes, ambiguousThisTypes);
				isThis = false;
			}
		}
		
		tables.put(join, new HashSet<Table>());
	}

	private static HashSet<Type> putType(final Type type, final Join join, final HashMap<Type, Join> distinctTypes, HashSet<Type> ambiguousTypes)
	{
		if(ambiguousTypes!=null && ambiguousTypes.contains(type))
			return ambiguousTypes;
		
		if(distinctTypes.containsKey(type))
		{
			distinctTypes.remove(type);
			if(ambiguousTypes==null)
				ambiguousTypes = new HashSet<Type>();
			ambiguousTypes.add(type);
		}
		else
			distinctTypes.put(type, join);
		
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

	private void check(final Selectable select, final Join join, final HashMap<Type, Join> distinctTypes, final HashSet<Type> ambiguousTypes)
	{
		assert frozen;
		
		final Type selectType = select.getType();
		
		if(join==null)
		{
			if(queryTypes.contains(selectType))
			{
				register(select, null);
				return;
			}
			
			if(distinctTypes.containsKey(selectType))
			{
				register(select, distinctTypes.get(selectType));
				return;
			}
	
			if(ambiguousTypes!=null && ambiguousTypes.contains(selectType))
				throw new IllegalArgumentException(
						select.toString() + " is ambiguous, use Function#bind in query: " + query.toString()
						/*+ "---" + distinctTypes + "---" + ambiguousTypes*/);
		}
		else
		{
			if(distinctTypes.containsKey(selectType))
			{
				register(select, join);
				return;
			}
	
			if(ambiguousTypes!=null && ambiguousTypes.contains(selectType))
			{
				register(select, join);
				return;
			}
		}

		throw new IllegalArgumentException(select.toString() + " does not belong to a type of the query: " + query.toString());
	}

	private void register(final Selectable select, final Join join)
	{
		final Table table;
		if(select instanceof FunctionField)
			table = ((FunctionField)select).getColumn().table;
		else if(select instanceof Type.This)
			table = ((Type.This)select).getType().getTable();
		else
			throw new RuntimeException(select.toString());

		tables.get(join).add(table);
	}
	
	boolean containsTable(final Join join, final Table table)
	{
		return tables.get(join).contains(table);
	}
}

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

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.Cast;

final class Importer<E extends Object> extends Pattern
{
	private static final long serialVersionUID = 1l;
	
	private final FunctionField<E> key;
	
	private Importer(final FunctionField<E> key)
	{
		if(key==null)
			throw new NullPointerException("key");
		if(!key.isFinal())
			throw new IllegalArgumentException();
		if(!key.isMandatory())
			throw new IllegalArgumentException();
		if(key.getImplicitUniqueConstraint()==null)
			throw new IllegalArgumentException();
		
		this.key = key;
	}
	
	public static final <E> Importer<E> newImporter(final FunctionField<E> key)
	{
		return new Importer<E>(key);
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("doImport").
			setMethodWrapperPattern("import{0}").
			addComment("Import {0}.").
			setReturn(Wrapper.ClassVariable.class, "the imported item").
			addParameter(key.getInitialType(), "keyValue").
			addParameter(SetValue[].class, "setValues").
			setStatic());
		
		return Collections.unmodifiableList(result);
	}
	
	public <P extends Item> P doImport(
			final Class<P> parentClass,
			final E keyValue,
			final SetValue... setValues)
	{
		if(keyValue==null)
			throw new NullPointerException("keyValue");
		if(setValues==null)
			throw new NullPointerException("setValues");
		
		final P existent = Cast.verboseCast(parentClass, key.searchUnique(keyValue));
		if(existent!=null)
		{
			existent.set(setValues);
			return existent;
		}
		else
		{
			final SetValue[] setValuesNew = new SetValue[setValues.length + 1];
			setValuesNew[0] = key.map(keyValue);
			System.arraycopy(setValues, 0, setValuesNew, 1, setValues.length);
			return getType().as(parentClass).newItem(setValuesNew);
		}
	}
}

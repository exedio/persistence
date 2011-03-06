/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.SetValueUtil;
import com.exedio.cope.util.Cast;

public final class Importer<K extends Object> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<K> key;
	private boolean hintInitial = false;

	private Importer(final FunctionField<K> key)
	{
		if(key==null)
			throw new NullPointerException("key");
		if(!key.isFinal())
			throw new IllegalArgumentException("key must be final");
		if(!key.isMandatory())
			throw new IllegalArgumentException("key must be mandatory");
		if(key.getImplicitUniqueConstraint()==null)
			throw new IllegalArgumentException("key must be unique");

		this.key = key;
	}

	public static final <K> Importer<K> newImporter(final FunctionField<K> key)
	{
		return new Importer<K>(key);
	}

	public FunctionField<K> getKey()
	{
		return key;
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
			addParameterVararg(SetValue[].class, "setValues").
			setStatic());
		result.add(
			new Wrapper("doImport").
			setMethodWrapperPattern("import{0}").
			addComment("Import {0}.").
			setReturn(Wrapper.ClassVariable.class, "the imported item").
			addParameter(key.getInitialType(), "keyValue").
			addParameter(Wrapper.genericExtends(List.class, SetValue.class), "setValues").
			setStatic());

		return Collections.unmodifiableList(result);
	}

	public <P extends Item> P doImport(
			final Class<P> parentClass,
			final K keyValue,
			final List<? extends SetValue> setValues)
	{
		return doImport(parentClass, keyValue, SetValueUtil.toArray(setValues));
	}

	public <P extends Item> P doImport(
			final Class<P> parentClass,
			final K keyValue,
			final SetValue... setValues)
	{
		if(keyValue==null)
			throw new NullPointerException("keyValue");
		if(setValues==null)
			throw new NullPointerException("setValues");

		if(hintInitial)
			return doImportInitial(parentClass, keyValue, setValues);

		final P existent = Cast.verboseCast(parentClass, key.searchUnique(keyValue));
		if(existent!=null)
		{
			existent.set(setValues);
			return existent;
		}
		else
		{
			return getType().as(parentClass).newItem(prepend(key.map(keyValue), setValues));
		}
	}

	private <P extends Item> P doImportInitial(
			final Class<P> parentClass,
			final K keyValue,
			final SetValue... setValues)
	{
		final SetValue[] setValuesNew = prepend(key.map(keyValue), setValues);
		final Type<P> type = getType().as(parentClass);

		try
		{
			return type.newItem(setValuesNew);
		}
		catch(final UniqueViolationException e)
		{
			final P existent = Cast.verboseCast(parentClass, key.searchUnique(keyValue));
			existent.set(setValues);
			return existent;
		}
	}

	/**
	 * When setting to true,
	 * method {@link #doImport(Class, Object, SetValue...)}
	 * becomes more efficient when item do not yet exist
	 * and less efficient when items already do exist.
	 */
	public void setHintInitialExerimental(final boolean hintInitial)
	{
		this.hintInitial = hintInitial;
	}

	private static SetValue[] prepend(final SetValue head, final SetValue[] tail)
	{
		final SetValue[] result = new SetValue[tail.length + 1];
		result[0] = head;
		System.arraycopy(tail, 0, result, 1, tail.length);
		return result;
	}
}

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

package com.exedio.cope.pattern;

import static com.exedio.cope.SetValue.map;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.SetValueUtil;
import java.util.List;
import javax.annotation.Nonnull;

@WrapFeature
public final class Importer<K> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<K> key;
	private boolean hintInitial = false;

	private Importer(final FunctionField<K> key)
	{
		this.key = requireNonNull(key, "key");
		if(!key.isFinal())
			throw new IllegalArgumentException("key must be final");
		if(!key.isMandatory())
			throw new IllegalArgumentException("key must be mandatory");
		if(key.getImplicitUniqueConstraint()==null)
			throw new IllegalArgumentException("key must be unique");
	}

	public static <K> Importer<K> create(final FunctionField<K> key)
	{
		return new Importer<>(key);
	}

	public FunctionField<K> getKey()
	{
		return key;
	}

	@Wrap(order=20, name="import{0}", doc="Import {0}.", docReturn="the imported item")
	@Nonnull
	public <P extends Item> P doImport(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("keyValue") final K keyValue,
			@Nonnull @Parameter("setValues") final List<SetValue<?>> setValues)
	{
		return doImport(parentClass, keyValue, SetValueUtil.toArray(setValues));
	}

	@Wrap(order=10, name="import{0}", doc="Import {0}.", docReturn="the imported item")
	@Nonnull
	public <P extends Item> P doImport(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("keyValue") final K keyValue,
			@Nonnull @Parameter("setValues") final SetValue<?>... setValues)
	{
		final Type<P> type =
				requireParentClass(parentClass, "parentClass");
		requireNonNull(keyValue, "keyValue");
		requireNonNull(setValues, "setValues");

		if(hintInitial)
			return doImportInitial(parentClass, type, keyValue, setValues);

		final P existent = key.searchUnique(parentClass, keyValue);
		if(existent!=null)
		{
			existent.set(setValues);
			return existent;
		}
		else
		{
			return type.newItem(prepend(map(key, keyValue), setValues));
		}
	}

	private <P extends Item> P doImportInitial(
			final Class<P> parentClass,
			final Type<P> type,
			final K keyValue,
			final SetValue<?>... setValues)
	{
		final SetValue<?>[] setValuesNew = prepend(map(key, keyValue), setValues);
		try
		{
			return type.newItem(setValuesNew);
		}
		catch(final UniqueViolationException e)
		{
			assert key.getImplicitUniqueConstraint()==e.getFeature();
			final P existent = key.searchUniqueStrict(parentClass, keyValue);
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

	private static SetValue<?>[] prepend(final SetValue<?> head, final SetValue<?>[] tail)
	{
		final SetValue<?>[] result = new SetValue<?>[tail.length + 1];
		result[0] = head;
		System.arraycopy(tail, 0, result, 1, tail.length);
		return result;
	}
}

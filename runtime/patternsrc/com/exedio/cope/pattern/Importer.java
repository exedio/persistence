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
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.FeaturesGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Arrays;
import com.exedio.cope.misc.SetValueUtil;
import java.util.List;
import javax.annotation.Nonnull;

@WrapFeature
public final class Importer<K> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final Class<K> keyClass;
	private final UniqueConstraint constraint;
	private boolean hintInitial = false;

	private Importer(final Class<K> keyClass, final UniqueConstraint constraint)
	{
		this.keyClass = keyClass;
		this.constraint = requireNonNull(constraint, "constraint");
		int index = 0;
		for(final FunctionField<?> key : constraint.getFields())
		{
			if(! key.isFinal())
				throw new IllegalArgumentException("key "+index+" must be final");
			if(! key.isMandatory())
				throw new IllegalArgumentException("key "+index+" must be mandatory");
			index++;
		}
	}

	public static <K> Importer<K> create(final FunctionField<K> key)
	{
		requireNonNull(key, "key");
		if(key.getImplicitUniqueConstraint()==null)
			throw new IllegalArgumentException("key must be unique");
		return new Importer<>(key.getValueClass(), key.getImplicitUniqueConstraint());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Importer<?> create(final UniqueConstraint constraint)
	{
		return (Importer<?>) new Importer(List.class, constraint);
	}

	@SuppressWarnings("unchecked")
	public FunctionField<K> getKey()
	{
		final List<FunctionField<?>> fields = constraint.getFields();
		if (fields.size()!=1)
			throw new IllegalStateException("not supported for Importer on compound unique index "+constraint);
		if (!keyClass.isAssignableFrom(fields.get(0).getValueClass()))
			throw new RuntimeException(fields.get(0)+" does not match "+keyClass);
		return (FunctionField<K>) fields.get(0);
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return constraint;
	}

	@Wrap(order=20, name="import{0}", doc="Import {0}.", docReturn="the imported item", hide=MoreThanOneKey.class)
	@Nonnull
	public <P extends Item> P doImport(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("keyValue") final K keyValue,
			@Nonnull @Parameter("setValues") final List<SetValue<?>> setValues)
	{
		return doImport(parentClass, keyValue, SetValueUtil.toArray(setValues));
	}

	@Wrap(order=10, name="import{0}", doc="Import {0}.", docReturn="the imported item", hide=MoreThanOneKey.class)
	@Nonnull
	public <P extends Item> P doImport(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("keyValue") final K keyValue,
			@Nonnull @Parameter("setValues") final SetValue<?>... setValues)
	{
		requireNonNull(keyValue, "keyValue");
		requireNonNull(setValues, "setValues");

		return doImportInternal(parentClass, List.of(keyValue), setValues);
	}

	@Nonnull
	private <P extends Item> P doImportInternal(
			@Nonnull final Class<P> parentClass,
			@Nonnull final List<?> keys,
			@Nonnull final SetValue<?>[] setValues)
	{
		final Type<P> type = requireParentClass(parentClass, "parentClass");
		if (constraint.getFields().size()!=keys.size())
			throw new RuntimeException(String.valueOf(constraint.getFields().size())+'-'+keys.size());
		if(hintInitial)
			return doImportInitial(parentClass, type, keys, setValues);

		final P existent = constraint.search(parentClass, keys.toArray());
		if(existent!=null)
		{
			existent.set(setValues);
			return existent;
		}
		else
		{
			return type.newItem(prependKeys(keys, setValues));
		}
	}

	@Wrap(order=30, name="import{0}", doc="Import {0}.", docReturn="the imported item", varargsFeatures=ImportMulti.class, hide=OnlyOneKey.class)
	@Nonnull
	public <P extends Item> P doImportMultipleKeys(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("setValues") final List<SetValue<?>> setValues,
			@Nonnull final Object... values)
	{
		return doImportInternal(parentClass, asList(values), SetValueUtil.toArray(setValues));
	}

	private <P extends Item> P doImportInitial(
			final Class<P> parentClass,
			final Type<P> type,
			final List<?> keys,
			final SetValue<?>... setValues)
	{
		try
		{
			return type.newItem(prependKeys(keys, setValues));
		}
		catch(final UniqueViolationException e)
		{
			assert constraint==e.getFeature();
			final P existent = constraint.searchStrict(parentClass, keys.toArray());
			existent.set(setValues);
			return existent;
		}
	}

	private SetValue<?>[] prependKeys(final List<?> keys, final SetValue<?>[] setValues)
	{
		SetValue<?>[] setValuesNew = setValues;
		for (int i = 0; i < keys.size(); i++)
		{
			setValuesNew = Arrays.prepend(mapChecked(constraint.getFields().get(i), keys.get(i)), setValuesNew);
		}
		return setValuesNew;
	}

	private static <K> SetValue<?> mapChecked(final FunctionField<K> field, final Object value)
	{
		return map(field, field.getValueClass().cast(value));
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

	private static class ImportMulti implements FeaturesGetter<Importer<?>>
	{
		@Override
		public List<?> get(final Importer<?> feature)
		{
			return feature.constraint.getFields();
		}
	}

	private static class MoreThanOneKey implements BooleanGetter<Importer<?>>
	{
		@Override
		public boolean get(final Importer<?> feature)
		{
			return feature.constraint.getFields().size()>1;
		}
	}

	private static class OnlyOneKey implements BooleanGetter<Importer<?>>
	{
		@Override
		public boolean get(final Importer<?> feature)
		{
			return feature.constraint.getFields().size()==1;
		}
	}
}

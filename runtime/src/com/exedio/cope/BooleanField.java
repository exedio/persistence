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

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.io.Serial;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BooleanField extends FunctionField<Boolean>
{
	@Serial
	private static final long serialVersionUID = 1l;

	static final int[] ALLOWED_VALUES = {0, 1};

	private BooleanField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<Boolean> defaultS)
	{
		super(isfinal, optional, Boolean.class, unique, copyFrom, defaultS);
		mountDefault();
	}

	public BooleanField()
	{
		this(false, false, false, null, null);
	}

	@Override
	public BooleanField copy()
	{
		return new BooleanField(isfinal, optional, unique, copyFrom, defaultS);
	}

	@Override
	public BooleanField toFinal()
	{
		return new BooleanField(true, optional, unique, copyFrom, defaultS);
	}

	@Override
	public BooleanField optional()
	{
		return new BooleanField(isfinal, true, unique, copyFrom, defaultS);
	}

	@Override
	public BooleanField mandatory()
	{
		return new BooleanField(isfinal, false, unique, copyFrom, defaultS);
	}

	@Override
	public BooleanField unique()
	{
		return new BooleanField(isfinal, optional, true, copyFrom, defaultS);
	}

	@Override
	public BooleanField nonUnique()
	{
		return new BooleanField(isfinal, optional, false, copyFrom, defaultS);
	}

	@Override
	public BooleanField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.RESOLVE_TEMPLATE));
	}

	@Override
	public BooleanField copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<Boolean>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public BooleanField copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private BooleanField copyFrom(final CopyFrom copyFrom)
	{
		return new BooleanField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS);
	}

	@Override
	public BooleanField noCopyFrom()
	{
		return new BooleanField(isfinal, optional, unique, null, defaultS);
	}

	@Override
	public BooleanField noDefault()
	{
		return new BooleanField(isfinal, optional, unique, copyFrom, null);
	}

	@Override
	public BooleanField defaultTo(final Boolean defaultConstant)
	{
		return new BooleanField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant));
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Boolean> getValueType()
	{
		return SimpleSelectType.BOOLEAN;
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		return new IntegerColumn(table, name, optional, ALLOWED_VALUES);
	}

	@Override
	Boolean get(final Row row)
	{
		final Object cell = row.get(getColumn());
		if(cell==null)
			return null;
		else
		{
			switch((Integer)cell)
			{
				case 0:
					return Boolean.FALSE;
				case 1:
					return Boolean.TRUE;
				default:
					throw new RuntimeException("cacheToSurface:"+cell);
			}
		}
	}

	static final Integer FALSE = 0;
	static final Integer TRUE  = 1;

	@Override
	void set(final Row row, final Boolean surface)
	{
		row.put(getColumn(), surface==null ? null : surface ? TRUE : FALSE);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc=Wrap.GET_DOC, hide=OptionalGetter.class)
	public boolean getMandatory(@Nonnull final Item item)
	{
		return getMandatoryObject(item);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			hide={FinalSettableGetter.class, OptionalGetter.class, RedundantByCopyConstraintGetter.class},
			thrownGetter=InitialThrown.class)
	public void set(@Nonnull final Item item, final boolean value)
	{
		set(item, Boolean.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC,
			docReturn=Wrap.FOR_RETURN,
			hide={OptionalGetter.class, NonUniqueGetter.class})
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) final boolean value)
	{
		return searchUnique(typeClass, Boolean.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=110, name=Wrap.FOR_STRICT_NAME,
			doc=Wrap.FOR_DOC,
			hide={OptionalGetter.class, NonUniqueGetter.class},
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc="if there is no matching item."))
	@Nonnull
	public <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final boolean value)
			throws IllegalArgumentException
	{
		return searchUniqueStrict(typeClass, Boolean.valueOf(value));
	}
}

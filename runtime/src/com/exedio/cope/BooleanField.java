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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BooleanField extends FunctionField<Boolean>
{
	private static final long serialVersionUID = 1l;

	static final int[] ALLOWED_VALUES = {0, 1};

	private BooleanField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
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
	public BooleanField copyFrom(final ItemField<?> copyFrom)
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
	public SelectType<Boolean> getValueType()
	{
		return SimpleSelectType.BOOLEAN;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, optional, ALLOWED_VALUES);
	}

	@SuppressFBWarnings("NP_BOOLEAN_RETURN_NULL") // Method with Boolean return type returns explicit null
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
	@Wrap(order=10, name="get{0}", doc="Returns the value of {0}.", hide=OptionalGetter.class)
	public boolean getMandatory(@Nonnull final Item item)
	{
		return getMandatoryObject(item);
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			hide={FinalSettableGetter.class, OptionalGetter.class, RedundantByCopyConstraintGetter.class},
			thrownGetter=InitialThrown.class)
	public void set(@Nonnull final Item item, final boolean value)
	{
		set(item, Boolean.valueOf(value));
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name="for{0}",
			doc="Finds a {2} by it''s {0}.",
			docReturn="null if there is no matching item.",
			hide={OptionalGetter.class, NonUniqueGetter.class})
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final boolean value)
	{
		return searchUnique(typeClass, Boolean.valueOf(value));
	}
}

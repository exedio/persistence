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

import java.io.Serial;
import java.util.List;
import java.util.function.Supplier;

public final class EnumField<E extends Enum<E>> extends FunctionField<E>
{
	@Serial
	private static final long serialVersionUID = 1l;

	final EnumFieldType<E> valueType;

	private EnumField(
			final boolean isfinal,
			final boolean optional,
			final Class<E> valueClass,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<E> defaultS)
	{
		super(isfinal, optional, valueClass, unique, copyFrom, defaultS);

		this.valueType = EnumFieldType.get(valueClass);

		checkValueClass(Enum.class);
		mountDefault();
	}

	public static <E extends Enum<E>> EnumField<E> create(final Class<E> valueClass)
	{
		final EnumField<E> result = createEvenIfRedundant(valueClass);
		if(result.valueType.isSingle())
			throw new IllegalArgumentException(
					"Redundant field on a valueClass with one enum constant only " +
					"(" + valueClass.getName() + ") is probably a mistake. " +
					"You may call method createEvenIfRedundant if you are sure this is ok.");
		return result;
	}

	public static <E extends Enum<E>> EnumField<E> createEvenIfRedundant(final Class<E> valueClass)
	{
		return new EnumField<>(false, false, valueClass, false, null, null);
	}

	@Override
	public EnumField<E> copy()
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, copyFrom, defaultS);
	}

	@Override
	public EnumField<E> toFinal()
	{
		return new EnumField<>(true, optional, valueClass, unique, copyFrom, defaultS);
	}

	@Override
	public EnumField<E> optional()
	{
		return new EnumField<>(isfinal, true, valueClass, unique, copyFrom, defaultS);
	}

	@Override
	public EnumField<E> mandatory()
	{
		return new EnumField<>(isfinal, false, valueClass, unique, copyFrom, defaultS);
	}

	@Override
	public EnumField<E> unique()
	{
		return new EnumField<>(isfinal, optional, valueClass, true, copyFrom, defaultS);
	}

	@Override
	public EnumField<E> nonUnique()
	{
		return new EnumField<>(isfinal, optional, valueClass, false, copyFrom, defaultS);
	}

	@Override
	public EnumField<E> copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.RESOLVE_TEMPLATE));
	}

	@Override
	public EnumField<E> copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<E>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public EnumField<E> copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private EnumField<E> copyFrom(final CopyFrom copyFrom)
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, addCopyFrom(copyFrom), defaultS);
	}

	@Override
	public EnumField<E> noCopyFrom()
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, null, defaultS);
	}

	@Override
	public EnumField<E> noDefault()
	{
		return defaultTo(null);
	}

	@Override
	public EnumField<E> defaultTo(final E defaultConstant)
	{
		assert valueType.isValid(defaultConstant);
		return new EnumField<>(isfinal, optional, valueClass, unique, copyFrom, defaultConstant(defaultConstant));
	}

	@Override
	boolean overlaps(final FunctionField<?> other)
	{
		return
				super.overlaps(other) &&
				valueType==((EnumField<?>)other).valueType;
	}

	public List<E> getValues()
	{
		return valueType.values;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<E> getValueType()
	{
		return valueType;
	}

	/**
	 * @see Enum#valueOf(Class, String)
	 */
	public E getValue(final String code)
	{
		//System.out.println("EnumerationValue#getValue("+code+") from "+codesToValues);
		return Enum.valueOf(valueClass, code);
	}

	/**
	 * @see ItemField#as(Class)
	 * @see Class#asSubclass(Class)
	 */
	public <X extends Enum<X>> EnumField<X> as(final Class<X> clazz)
	{
		if(!valueClass.equals(clazz))
		{
			final String n = EnumField.class.getName();
			// exception message consistent with Cope.verboseCast(Class, Object)
			throw new ClassCastException(
					"expected a " + n + '<' + clazz.getName() +
					">, but was a " + n + '<' + valueClass.getName() + '>');
		}

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final EnumField<X> result = (EnumField<X>)this;
		return result;
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		return new IntegerColumn(table, name, optional, valueType.getNumbers());
	}

	@Override
	E get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return
			cell==null ?
				null :
				valueType.getValueByNumber((Integer)cell);
	}

	@Override
	void set(final Row row, final E surface)
	{
		assert valueType.isValid(surface);
		row.put(getColumn(), surface==null ? null : valueType.getNumber(surface));
	}
}

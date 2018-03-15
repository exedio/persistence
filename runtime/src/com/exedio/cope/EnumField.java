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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

public final class EnumField<E extends Enum<E>> extends FunctionField<E>
{
	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final EnumFieldType<E> valueType;

	private EnumField(
			final boolean isfinal,
			final boolean optional,
			final Class<E> valueClass,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<E> defaultSource)
	{
		super(isfinal, optional, valueClass, unique, copyFrom, defaultSource);

		this.valueType = EnumFieldType.get(valueClass);

		checkValueClass(Enum.class);
		mountDefaultSource();
	}

	public static <E extends Enum<E>> EnumField<E> create(final Class<E> valueClass)
	{
		return new EnumField<>(false, false, valueClass, false, null, null);
	}

	@Override
	public EnumField<E> copy()
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, copyFrom, defaultSource);
	}

	@Override
	public EnumField<E> toFinal()
	{
		return new EnumField<>(true, optional, valueClass, unique, copyFrom, defaultSource);
	}

	@Override
	public EnumField<E> optional()
	{
		return new EnumField<>(isfinal, true, valueClass, unique, copyFrom, defaultSource);
	}

	@Override
	public EnumField<E> unique()
	{
		return new EnumField<>(isfinal, optional, valueClass, true, copyFrom, defaultSource);
	}

	@Override
	public EnumField<E> nonUnique()
	{
		return new EnumField<>(isfinal, optional, valueClass, false, copyFrom, defaultSource);
	}

	@Override
	public EnumField<E> copyFrom(final ItemField<?> copyFrom)
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, addCopyFrom(copyFrom), defaultSource);
	}

	@Override
	public EnumField<E> noCopyFrom()
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, null, defaultSource);
	}

	@Override
	public EnumField<E> noDefault()
	{
		return new EnumField<>(isfinal, optional, valueClass, unique, copyFrom, null);
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
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		if(!optional && valueType.isSingle())
			throw new IllegalArgumentException(
					"mandatory enum field is not allowed on valueClass with one enum value only: "
					+ type.getID() + '.' + name +
					" on " + getValueClass().getName());

		super.mount(type, name, annotationSource);
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
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

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #as(Class)} instead
	 */
	@Deprecated
	public <X extends Enum<X>> EnumField<X> cast(final Class<X> clazz)
	{
		return as(clazz);
	}

	/**
	 * @deprecated Use {@link SchemaInfo#getColumnValue(EnumField,Enum)} instead
	 */
	@Deprecated
	public int getColumnValue(final E value)
	{
		return SchemaInfo.getColumnValue(this, value);
	}
}

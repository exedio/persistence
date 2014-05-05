/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final Class<E> valueClass,
			final DefaultSource<E> defaultSource)
	{
		super(isfinal, optional, unique, copyFrom, valueClass, defaultSource);

		this.valueType = EnumFieldType.get(valueClass);

		checkValueClass(Enum.class);
		mountDefaultSource();
	}

	public static final <E extends Enum<E>> EnumField<E> create(final Class<E> valueClass)
	{
		return new EnumField<>(false, false, false, null, valueClass, null);
	}

	@Override
	public EnumField<E> copy()
	{
		return new EnumField<>(isfinal, optional, unique, copyFrom, valueClass, defaultSource);
	}

	@Override
	public EnumField<E> toFinal()
	{
		return new EnumField<>(true, optional, unique, copyFrom, valueClass, defaultSource);
	}

	@Override
	public EnumField<E> optional()
	{
		return new EnumField<>(isfinal, true, unique, copyFrom, valueClass, defaultSource);
	}

	@Override
	public EnumField<E> unique()
	{
		return new EnumField<>(isfinal, optional, true, copyFrom, valueClass, defaultSource);
	}

	@Override
	public EnumField<E> nonUnique()
	{
		return new EnumField<>(isfinal, optional, false, copyFrom, valueClass, defaultSource);
	}

	@Override
	public EnumField<E> copyFrom(final ItemField<?> copyFrom)
	{
		return new EnumField<>(isfinal, optional, unique, addCopyFrom(copyFrom), valueClass, defaultSource);
	}

	@Override
	public EnumField<E> noDefault()
	{
		return new EnumField<>(isfinal, optional, unique, copyFrom, valueClass, null);
	}

	@Override
	public EnumField<E> defaultTo(final E defaultConstant)
	{
		assert valueType.isValid(defaultConstant);
		return new EnumField<>(isfinal, optional, unique, copyFrom, valueClass, defaultConstant(defaultConstant));
	}

	public List<E> getValues()
	{
		return valueType.values;
	}

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
	final void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		if(!this.optional && valueType.isSingle())
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
				valueType.getValueByNumber(((Integer)cell).intValue());
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
	public final int getColumnValue(final E value)
	{
		return SchemaInfo.getColumnValue(this, value);
	}
}

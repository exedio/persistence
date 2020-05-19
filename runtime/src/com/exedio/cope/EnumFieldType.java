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

import com.exedio.cope.misc.EnumAnnotatedElement;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gnu.trove.TIntObjectHashMap;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

final class EnumFieldType<E extends Enum<E>> implements SelectType<E>
{
	private final Class<E> valueClass;
	final List<E> values;
	private final TIntObjectHashMap<E> numbersToValues;
	private final int[] ordinalsToNumbers;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Marshaller<E> marshaller;

	private EnumFieldType(final Class<E> valueClass, final E[] enumConstants)
	{
		this.valueClass = valueClass;
		assert valueClass.isEnum() : valueClass;
		assert enumConstants.length>0 : valueClass;

		final TIntObjectHashMap<E> numbersToValues = new TIntObjectHashMap<>();
		final int[] ordinalsToNumbers = new int[enumConstants.length];
		int schemaValue = 0;
		boolean first = true;
		for(final E e : enumConstants)
		{
			final CopeSchemaValue annotation = schemaValue(e);
			final int previousSchemaValue = schemaValue;
			schemaValue = annotation!=null ? annotation.value() : roundUpTo10(schemaValue+1);
			if(!first && previousSchemaValue>=schemaValue)
				throw new IllegalArgumentException(
						valueClass.getName() + ": @" + CopeSchemaValue.class.getSimpleName() +
						" for " + e.name() + " must be greater than " + previousSchemaValue +
						", but was " + schemaValue + '.');

			first = false;
			numbersToValues.put(schemaValue, e);
			ordinalsToNumbers[e.ordinal()] = schemaValue;
		}
		numbersToValues.trimToSize();
		this.values = Collections.unmodifiableList(Arrays.asList(enumConstants));
		this.numbersToValues = numbersToValues;
		this.ordinalsToNumbers = ordinalsToNumbers;
		this.marshaller = new Marshaller<E>(1)
		{
			@Override
			E unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				final int cell = row.getInt(columnIndex);
				if(row.wasNull())
					return null;

				return getValueByNumber(cell);
			}

			@Override
			String marshalLiteral(final E value)
			{
				return String.valueOf(getNumber(value)); // TODO precompute strings
			}

			@Override
			Object marshalPrepared(final E value)
			{
				return getNumber(value);
			}
		};
	}

	static int roundUpTo10(final int n)
	{
		final int mod = n % 10;
		if(mod==0)
			return n;

		return
			n>=0
			? n - mod + 10
			: n - mod;
	}

	private static CopeSchemaValue schemaValue(final Enum<?> e)
	{
		return EnumAnnotatedElement.get(e).getAnnotation(CopeSchemaValue.class);
	}

	@Override
	public Class<E> getJavaClass()
	{
		return valueClass;
	}

	boolean isValid(final E value)
	{
		if(value==null)
			return true;

		final Class<?> actualValueClass = value.getClass();
      return actualValueClass == valueClass || actualValueClass.getSuperclass() == valueClass;
	}

	int getNumber(final E value)
	{
		if(!isValid(value))
			throw new IllegalArgumentException(
					"expected " + valueClass.getName() +
					", but was a " + value.getClass().getName());
		return ordinalsToNumbers[value.ordinal()];
	}

	E getValueByNumber(final int number)
	{
		final E result = numbersToValues.get(number);
		if(result==null)
			throw new RuntimeException(valueClass.getName() + '/' + number);
		return result;
	}

	boolean isSingle()
	{
		return ordinalsToNumbers.length==1;
	}

	int[] getNumbers()
	{
		return com.exedio.cope.misc.Arrays.copyOf(ordinalsToNumbers);
	}

	@Override
	public String toString()
	{
		return valueClass.getName();
	}

	// static registry

	private static final HashMap<Class<?>, EnumFieldType<?>> types = new HashMap<>();

	static <E extends Enum<E>> EnumFieldType<E> get(final Class<E> valueClass)
	{
		if(!valueClass.isEnum())
			throw new IllegalArgumentException("not an enum: " + valueClass);
		final E[] enumConstants = valueClass.getEnumConstants();
		if(enumConstants.length==0)
			throw new IllegalArgumentException("must have at least one enum value: " + valueClass);

		synchronized(types)
		{
			@SuppressWarnings({"unchecked","rawtypes"})
			EnumFieldType<E> result = (EnumFieldType)types.get(valueClass);
			if(result==null)
			{
				result = new EnumFieldType<>(valueClass, enumConstants);
				types.put(valueClass, result);
			}
			return result;
		}
	}

	// serialization -------------

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
	 */
	private Object writeReplace()
	{
		return new Serialized<>(valueClass);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	private Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	private static final class Serialized<E extends Enum<E>> implements Serializable
	{
		private static final long serialVersionUID = 1l;
		private final Class<E> valueClass;

		private Serialized(final Class<E> valueClass)
		{
			this.valueClass = valueClass;
		}

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve()
		{
			return get(valueClass);
		}
	}
}

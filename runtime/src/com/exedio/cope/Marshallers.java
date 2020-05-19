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

import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings("AnonymousInnerClassMayBeStatic")
final class Marshallers
{
	private final HashMap<Class<?>, Marshaller<?>> marshallers = new HashMap<>();

	Marshallers(
			final Dialect dialect,
			final boolean supportsNativeDate)
	{
		put(SimpleSelectType.STRING, new Marshaller<String>(1) {
			@Override
			String unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				return row.getString(columnIndex);
			}
			@Override
			String marshalLiteral(final String value)
			{
				return StringColumn.cacheToDatabaseStatic(value);
			}
			@Override
			Object marshalPrepared(final String value)
			{
				return value;
			}
		});
		put(SimpleSelectType.BOOLEAN, new Marshaller<Boolean>(1) {
			@SuppressFBWarnings("NP_BOOLEAN_RETURN_NULL") // Method with Boolean return type returns explicit null
			@Override
			Boolean unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				final int cell = row.getInt(columnIndex);
				if(row.wasNull())
					return null;

				switch(cell)
				{
					case 0:
						return Boolean.FALSE;
					case 1:
						return Boolean.TRUE;
					default:
						throw new RuntimeException("" + cell);
				}
			}
			@Override
			String marshalLiteral(final Boolean value)
			{
				return value ? "1" : "0";
			}
			@Override
			Object marshalPrepared(final Boolean value)
			{
				return value ? BooleanField.TRUE : BooleanField.FALSE;
			}
		});
		put(SimpleSelectType.INTEGER, new Marshaller<Integer>(1) {
			@Override
			Integer unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				final int cell = row.getInt(columnIndex);
				if(row.wasNull())
					return null;

				return cell;
			}
			@Override
			String marshalLiteral(final Integer value)
			{
				//noinspection CallToNumericToString
				return value.toString();
			}
			@Override
			Object marshalPrepared(final Integer value)
			{
				return value;
			}
		});
		put(SimpleSelectType.LONG, new Marshaller<Long>(1) {
			@Override
			Long unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				final long cell = row.getLong(columnIndex);
				return !row.wasNull() ? cell : null;
			}
			@Override
			String marshalLiteral(final Long value)
			{
				//noinspection CallToNumericToString
				return value.toString();
			}
			@Override
			Object marshalPrepared(final Long value)
			{
				return value;
			}
		});
		put(SimpleSelectType.DOUBLE, new Marshaller<Double>(1) {
			@Override
			Double unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				final double cell = row.getDouble(columnIndex);
				//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
				return !row.wasNull() ? cell : null;
			}
			@Override
			String marshalLiteral(final Double value)
			{
				//noinspection CallToNumericToString
				return value.toString();
			}
			@Override
			Object marshalPrepared(final Double value)
			{
				return value;
			}
		});

		put(SimpleSelectType.DATE, supportsNativeDate
			? new Marshaller<Date>(1) {
				@Override
				Date unmarshal(final ResultSet row, final int columnIndex) throws SQLException
				{
					final Timestamp cell = row.getTimestamp(columnIndex, TimestampColumn.newGMTCalendar());
					return (cell!=null) ? new Date(cell.getTime()) : null;
				}

				@Override
				String marshalLiteral(final Date value)
				{
					return dialect.toLiteral(value);
				}

				@Override
				Object marshalPrepared(final Date value)
				{
					return new Timestamp(value.getTime());
				}
			}
			: new Marshaller<Date>(1) {
				@Override
				Date unmarshal(final ResultSet row, final int columnIndex) throws SQLException
				{
					final long cell = row.getLong(columnIndex);
					return !row.wasNull() ? new Date(cell) : null;
				}
				@Override
				String marshalLiteral(final Date value)
				{
					return String.valueOf(value.getTime());
				}
				@Override
				Object marshalPrepared(final Date value)
				{
					return value.getTime();
				}
			});

		put(SimpleSelectType.DAY, new Marshaller<Day>(1) {
			@Override
			Day unmarshal(final ResultSet row, final int columnIndex) throws SQLException
			{
				final String cell = row.getString(columnIndex);
				return (cell!=null) ? dialect.unmarshalDay(cell) : null;
			}
			@Override
			String marshalLiteral(final Day value)
			{
				// Don't use a static instance,
				// since then access must be synchronized
				final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMinimumIntegerDigits(2);
				return "{d '"+value.getYear()+'-'+nf.format(value.getMonthValue())+'-'+nf.format(value.getDayOfMonth())+"'}";
			}
			@Override
			Object marshalPrepared(final Day value)
			{
				return dialect.marshalDay(value);
			}
		});
	}

	private <E> void put(final SimpleSelectType<E> selectType, final Marshaller<E> marshaller)
	{
		if(marshallers.putIfAbsent(selectType.javaClass, marshaller)!=null)
			throw new RuntimeException(selectType.javaClass.getName());
	}

	Marshaller<?> get(final Selectable<?> select)
	{
		final SelectType<?> valueType = select.getValueType();

		if(valueType instanceof SimpleSelectType<?>)
			return get(valueType.getJavaClass());
		else if(valueType instanceof Type<?>)
			return ((Type<?>)valueType).getMarshaller();
		else if(valueType instanceof EnumFieldType<?>)
			return ((EnumFieldType<?>)valueType).marshaller;
		else
			throw new RuntimeException(valueType.toString());
	}

	Marshaller<?> getByValue(final Object value)
	{
		if(value instanceof Item)
		{
			return ((Item)value).getCopeType().getMarshaller();
		}
		else if(value instanceof Enum)
		{
			@SuppressWarnings("rawtypes")
			final Enum enumValue = (Enum)value;
			@SuppressWarnings({"unchecked", "rawtypes"})
			final	Marshaller marshaller = get(enumValue);
			return marshaller;
		}
		else
		{
			return get(value.getClass());
		}
	}

	private static <E extends Enum<E>> Marshaller<E> get(final E value)
	{
		return EnumFieldType.get(value.getDeclaringClass()).marshaller;
	}

	private <E> Marshaller<E> get(final Class<E> javaClass)
	{
		@SuppressWarnings({"unchecked","rawtypes"})
		final Marshaller<E> result = (Marshaller)marshallers.get(javaClass);
		if(result==null)
			throw new NullPointerException(javaClass.getName());
		return result;
	}
}

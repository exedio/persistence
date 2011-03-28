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

package com.exedio.cope;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.exedio.cope.util.Day;

final class Marshallers
{
	private final HashMap<Class, Marshaller> marshallers = new HashMap<Class, Marshaller>();

	Marshallers(final boolean supportsNativeDate)
	{
		put(SimpleSelectType.String, new Marshaller<String>() {
			@Override
			public String unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
			{
				return row.getString(columnIndex.value++);
			}
			@Override
			public String marshal(final String value)
			{
				return StringColumn.cacheToDatabaseStatic(value);
			}
			@Override
			public Object marshalPrepared(final String value)
			{
				return value;
			}
		});
		put(SimpleSelectType.Boolean, new Marshaller<Boolean>() {
			@Override
			public Boolean unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
			{
				final Object cell = row.getObject(columnIndex.value++);
				if(cell==null)
					return null;

				switch(((Number)cell).intValue())
				{
					case 0:
						return Boolean.FALSE;
					case 1:
						return Boolean.TRUE;
					default:
						throw new RuntimeException(cell.toString());
				}
			}
			@Override
			public String marshal(final Boolean value)
			{
				return value.booleanValue() ? "1" : "0";
			}
			@Override
			public Object marshalPrepared(final Boolean value)
			{
				return value.booleanValue() ? BooleanField.TRUE : BooleanField.FALSE;
			}
		});
		put(SimpleSelectType.Integer, new Marshaller<Integer>() {
			@Override
			public Integer unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
			{
				final Object cell = row.getObject(columnIndex.value++);
				return
					(cell==null)
					? null
					: (cell instanceof Integer)
						? (Integer)cell
						: Integer.valueOf(((Number)cell).intValue());
			}
			@Override
			public String marshal(final Integer value)
			{
				return value.toString();
			}
			@Override
			public Object marshalPrepared(final Integer value)
			{
				return value;
			}
		});
		put(SimpleSelectType.Long, new Marshaller<Long>() {
			@Override
			public Long unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
			{
				final Object cell = row.getObject(columnIndex.value++);
				return (cell!=null) ? convert(cell) : null;
			}

			private final Long convert(final Object o)
			{
				if(o instanceof Long)
					return (Long)o;
				else
					return Long.valueOf(((Number)o).longValue());
			}

			@Override
			public String marshal(final Long value)
			{
				return value.toString();
			}
			@Override
			public Object marshalPrepared(final Long value)
			{
				return value;
			}
		});
		put(SimpleSelectType.Double, new Marshaller<Double>() {
			@Override
			public Double unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
			{
				final Object cell = row.getObject(columnIndex.value++);
				//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
				return (cell!=null) ? convert(cell) : null;
			}

			private final Double convert(final Object o)
			{
				if(o instanceof BigDecimal)
					return Double.valueOf(((BigDecimal)o).doubleValue()); // for SumAggregate on Oracle
				else
					return (Double)o;
			}

			@Override
			public String marshal(final Double value)
			{
				return value.toString();
			}
			@Override
			public Object marshalPrepared(final Double value)
			{
				return value;
			}
		});

		put(SimpleSelectType.Date, supportsNativeDate
			? new Marshaller<Date>() {
				@Override
				public Date unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
				{
					final Timestamp cell = row.getTimestamp(columnIndex.value++);
					return (cell!=null) ? new Date(cell.getTime()) : null;
				}

				@Override
				public String marshal(final Date value)
				{
					// Don't use a static instance,
					// since then access must be synchronized
					return new SimpleDateFormat("{'ts' ''yyyy-MM-dd HH:mm:ss.SSS''}").format(value);
				}

				@Override
				public Object marshalPrepared(final Date value)
				{
					return new Timestamp(value.getTime());
				}
			}
			: new Marshaller<Date>() {
				@Override
				public Date unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
				{
					final Object cell = row.getObject(columnIndex.value++);
					return (cell!=null) ? new Date(((Number)cell).longValue()) : null;
				}
				@Override
				public String marshal(final Date value)
				{
					return String.valueOf(value.getTime());
				}
				@Override
				public Object marshalPrepared(final Date value)
				{
					return value.getTime();
				}
			});

		put(SimpleSelectType.Day, new Marshaller<Day>() {
			@Override
			public Day unmarshal(final ResultSet row, final IntHolder columnIndex) throws SQLException
			{
				final java.sql.Date cell = row.getDate(columnIndex.value++);
				return (cell!=null) ? new Day(cell) : null;
			}
			@Override
			public String marshal(final Day value)
			{
				// Don't use a static instance,
				// since then access must be synchronized
				final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMinimumIntegerDigits(2);
				return "{d '"+value.getYear()+'-'+nf.format(value.getMonth())+'-'+nf.format(value.getDay())+"'}";
			}
			@Override
			public Object marshalPrepared(final Day value)
			{
				return new Timestamp(value.getTimeInMillisFrom());
			}
		});
	}

	private <E> void put(final SimpleSelectType<E> selectType, final Marshaller<E> marshaller)
	{
		if(marshallers.put(selectType.javaClass, marshaller)!=null)
			throw new RuntimeException(selectType.javaClass.getName());
	}

	Marshaller get(final Selectable select)
	{
		final SelectType<?> valueType = select.getValueType();

		if(valueType instanceof SimpleSelectType)
			return get(valueType.getJavaClass());
		else if(valueType instanceof Type)
			return ((Type)valueType).getMarshaller();
		else if(valueType instanceof EnumFieldType)
			return ((EnumFieldType)valueType).marshaller;
		else
			throw new RuntimeException(valueType.toString());
	}

	Marshaller getByValue(final Object value)
	{
		if(value instanceof Item)
		{
			return ((Item)value).getCopeType().getMarshaller();
		}
		else if(value instanceof Enum)
		{
			final Class<? extends Enum> enumClass = ((Enum)value).getClass();
			@SuppressWarnings("unchecked")
			final EnumFieldType enumFieldType = EnumFieldType.get(enumClass);
			return enumFieldType.marshaller;
		}
		else
		{
			return get(value.getClass());
		}
	}

	private <E> Marshaller<E> get(final Class<E> javaClass)
	{
		@SuppressWarnings("unchecked")
		final Marshaller<E> result = marshallers.get(javaClass);
		if(result==null)
			throw new NullPointerException(javaClass.getName());
		return result;
	}
}

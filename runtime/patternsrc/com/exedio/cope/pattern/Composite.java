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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemWildcardCast;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.WrapType;
import com.exedio.cope.util.Day;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

@WrapType(
		wildcardClassCaster=ItemWildcardCast.class,
		revertFeatureBody=true,
		top=Composite.class
)
public abstract class Composite implements Serializable, TemplatedValue
{
	private final transient CompositeType<?> type;
	private Object[] values;

	protected Composite(final SetValue<?>... setValues)
	{
		type = CompositeType.newTypeOrExisting(getClass());
		values = type.values(setValues, null);
	}

	@SuppressWarnings("unchecked")
	public final <X> X get(final FunctionField<X> member)
	{
		return (X)values[position(member)];
	}

	public final int getMandatory(final IntegerField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");

		return (Integer)values[position(member)];
	}

	public final long getMandatory(final LongField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");

		return (Long)values[position(member)];
	}

	public final double getMandatory(final DoubleField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");

		return (Double)values[position(member)];
	}

	public final boolean getMandatory(final BooleanField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");

		return (Boolean)values[position(member)];
	}

	public final <X> void set(final FunctionField<X> member, final X value)
	{
		set(SetValue.map(member, value));
	}

	public final void set(final SetValue<?>... setValues)
	{
		requireNonNull(setValues, "setValues");
		if(setValues.length==0)
			return;

		values = getCopeType().values(setValues, values);
	}

	public final void touch(final DateField member)
	{
		set(member, new Date());
	}

	public final void touch(final DayField member, final TimeZone zone)
	{
		set(member, new Day(zone));
	}


	@Override
	public final CompositeType<?> getCopeType()
	{
		return type;
	}

	private int position(final FunctionField<?> member)
	{
		return type.position(member);
	}

	public static final String getTemplateName(final FunctionField<?> template)
	{
		return CompositeType.getTemplateName(template);
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(this==other)
			return true;

		//noinspection NonFinalFieldReferenceInEquals OK: contents of values may change anyway
		return
			other!=null &&
			getClass().equals(other.getClass()) &&
			Arrays.equals(values, ((Composite)other).values);
	}

	@Override
	public final int hashCode()
	{
		//noinspection NonFinalFieldReferencedInHashCode OK: contents of values may change anyway
		return getClass().hashCode() ^ Arrays.hashCode(values);
	}

	// serialization -------------

	@Serial
	private static final long serialVersionUID = 1l;

	/**
	 * We cannot just serialize {@link #values} as the contents of
	 * {@link CompositeType#templateList} may change between serialization and deserialization.
	 * <p>
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	@Serial
	protected final Object writeReplace()
	{
		return new Serialized(type, type.templateList.toArray(new FunctionField<?>[values.length]), values);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@Serial
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@Serial
	protected final Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	private record Serialized(
			CompositeType<?> type,
			FunctionField<?>[] fields,
			@SuppressWarnings("NonSerializableFieldInSerializableClass")
			Object[] values)
			implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 2l;

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		@Serial
		private Object readResolve()
		{
			final SetValue<?>[] setValues = new SetValue<?>[values.length];
			Arrays.setAll(setValues, i -> SetValue.mapCasted(fields[i], values[i]));
			return type.newValue(setValues);
		}
	}
}

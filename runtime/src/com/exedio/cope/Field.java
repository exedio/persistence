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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.util.Cast;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An <tt>field</tt> represents a persistently
 * stored field of a {@link Type}.
 * Subclasses specify the type of information to be stored
 * in the field.
 *
 * @author Ralf Wiebicke
 */
@WrapFeature
public abstract class Field<E> extends Feature implements Settable<E>
{
	private static final long serialVersionUID = 1l;

	final boolean isfinal;
	final boolean optional;
	final Class<E> valueClass;

	Field(final boolean isfinal, final boolean optional, final Class<E> valueClass)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		this.valueClass = requireNonNull(valueClass, "valueClass");
	}

	/**
	 * Returns a new Field,
	 * that differs from this Field
	 * by being final.
	 * If this Field is already final,
	 * the the result is equal to this Field.
	 * @see #isFinal()
	 */
	public abstract Field<E> toFinal();

	/**
	 * Returns a new Field,
	 * that differs from this Field
	 * by being optional.
	 * If this Field is already optional,
	 * the the result is equal to this Field.
	 * @see #isMandatory()
	 */
	public abstract Field<E> optional();

	/**
	 * @see #toFinal()
	 */
	@Override
	public final boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public final boolean isMandatory()
	{
		return !optional;
	}

	@Override
	public final Class<?> getInitialType()
	{
		return valueClass;
	}

	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This default implementation returns
	 * <tt>{@link #isFinal()} || {@link #isMandatory()}</tt>.
	 */
	@Override
	public boolean isInitial()
	{
		return isfinal || !optional;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}

	public final Class<E> getValueClass()
	{
		return valueClass;
	}

	/**
	 * @deprecated Use {@link Cast#castElements(Class, Collection)} instead
	 */
	@Deprecated
	public final Collection<E> castCollection(final Collection<?> c)
	{
		return Cast.castElements(valueClass, c);
	}

	public final SetValue<E> mapNull()
	{
		return SetValue.map(this, null);
	}

	@Override
	public final SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ map(value) };
	}

	/**
	 * @throws ConstraintViolationException if <tt>value</tt> does not satisfy constraints.
	 */
	public final void check(final E value)
	{
		check(value, null);
	}

	/**
	 * Checks field values set by
	 * {@link Item#set(FunctionField,Object)}
	 * and {@link Item#Item(SetValue[])}
	 * and throws the exception specified there.
	 * @throws ConstraintViolationException if <tt>value</tt> does not satisfy constraints.
	 */
	final void check(final Object value, final Item exceptionItem)
	{
		if(value == null)
		{
			if(!optional)
				throw MandatoryViolationException.create(this, exceptionItem);
		}
		else
		{
			if(!valueClass.isInstance(value))
			{
				throw new ClassCastException(
						"expected a " + valueClass.getName() +
						", but was a " + value.getClass().getName() +
						" for " + toString() + '.');
			}

			checkNotNull(valueClass.cast(value), exceptionItem);
		}
	}

	/**
	 * Further checks non-null field values already checked by
	 * {@link #check(Object, Item)}.
	 * To be overidden by subclasses,
	 * the default implementation does nothing.
	 * @param value used in subclasses
	 * @param exceptionItem used in subclasses
	 */
	void checkNotNull(final E value, final Item exceptionItem)
		throws
			StringLengthViolationException
	{
		// empty default implementation
	}

	// second initialization phase ---------------------------------------------------

	private Column column;

	final void connect(final Table table)
	{
		if(table==null)
			throw new NullPointerException();
		if(column!=null)
			throw new RuntimeException();

		column = createColumn(table, getDeclaredSchemaName(), optional);
	}

	void disconnect()
	{
		column = null;
	}

	final Column getColumn()
	{
		if(column==null)
			throw new RuntimeException();

		return column;
	}

	abstract Column createColumn(Table table, String name, boolean optional);
	public abstract E get(Item item);
	public abstract void set(Item item, E value);

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link SchemaInfo#getColumnName(Field)} instead
	 */
	@Deprecated
	public final String getColumnName()
	{
		return SchemaInfo.getColumnName(this);
	}

	@Deprecated
	public final List<Pattern> getPatterns()
	{
		final Pattern pattern = getPattern();
		return
			pattern!=null
			? Collections.singletonList(pattern)
			: Collections.emptyList();
	}
}

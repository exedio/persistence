/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public abstract class FunctionAttribute<E extends Object>
	extends Attribute<E>
	implements Function<E>
{
	final UniqueConstraint implicitUniqueConstraint;
	private ArrayList<UniqueConstraint> uniqueConstraints;
	
	FunctionAttribute(final boolean isfinal, final boolean optional, final boolean unique)
	{
		super(isfinal, optional);
		this.implicitUniqueConstraint =
			unique ?
				new UniqueConstraint(this) :
				null;
	}
	
	private Class<?> valueClass;
	
	@Override
	final void initialize(final Type type, final String name, final java.lang.reflect.Type genericType)
	{
		super.initialize(type, name, genericType);
		
		if(implicitUniqueConstraint!=null)
			implicitUniqueConstraint.initialize(type, name + UniqueConstraint.IMPLICIT_UNIQUE_SUFFIX, genericType);
		
		valueClass = initialize(genericType);
	}
	
	abstract Class initialize(java.lang.reflect.Type genericType);
	
	public abstract FunctionAttribute<E> copyFunctionAttribute();

	abstract E get(final Row row);
	abstract void set(final Row row, final E surface);
	
	/**
	 * Checks attribute values set by
	 * {@link Item#setAttribute(FunctionAttribute,Object)} (for <tt>initial==false</tt>)
	 * and {@link Item(FunctionAttribute[])} (for <tt>initial==true</tt>)
	 * and throws the exception specified there.
	 */
	final void checkValue(final Object value, final Item item)
		throws
			MandatoryViolationException,
			LengthViolationException
	{
		if(value == null)
		{
			if(!optional)
				throw new MandatoryViolationException(this, item);
		}
		else
		{
			if(value.equals("") &&
					!optional &&
					!getType().getModel().supportsEmptyStrings()) // TODO dont call supportsEmptyStrings that often
				throw new MandatoryViolationException(this, item);
			
			if(!(valueClass.isAssignableFrom(value.getClass())))
			{
				throw new ClassCastException(
						"expected a " + valueClass.getName() +
						", but was a " + value.getClass().getName() +
						" for " + toString() + '.');
			}
			checkNotNullValue(value, item);
		}
	}

	/**
	 * Further checks non-null attribute values already checked by
	 * {@link #checkValue(boolean, Object, Item)}.
	 * To be overidden by subclasses,
	 * the default implementation does nothing.
	 */
	void checkNotNullValue(final Object value, final Item item)
		throws
			LengthViolationException
	{
		// empty default implementation
	}
	
	private static final Entity getEntity(final Item item)
	{
		return getEntity(item, true);
	}

	private static final Entity getEntity(final Item item, final boolean present)
	{
		return item.type.getModel().getCurrentTransaction().getEntity(item, present);
	}

	public final E get(final Item item)
	{
		if(!getType().isAssignableFrom(item.type))
			throw new RuntimeException("attribute "+toString()+" does not belong to type "+item.type.toString());
		
		return (E)getEntity(item).get(this);
	}

	public final void set(final Item item, final E value)
	{
		item.set(this, value);
	}

	public final void append(final Statement bf, final Join join)
	{
		bf.append(getColumn(), join);
	}
	
	public final void appendParameter(final Statement bf, final E value)
	{
		final Row dummyRow = new Row();
		set(dummyRow, value);
		final Column column = getColumn();
		bf.appendParameter(column, dummyRow.get(column));
	}
	
	/**
	 * Returns the unique constraint of this attribute,
	 * that has been created implicitly when creating this attribute.
	 * Does return null, if there is no such unique constraint.
	 * @see #getUniqueConstraints()
	 */
	public UniqueConstraint getImplicitUniqueConstraint()
	{
		return implicitUniqueConstraint;
	}

	/**
	 * Returns a list of unique constraints this attribute is part of.
	 * This includes an
	 * {@link #getImplicitUniqueConstraint() implicit unique constraint},
	 * if there is one for this attribute.
	 */
	public List<UniqueConstraint> getUniqueConstraints()
	{
		return uniqueConstraints!=null ? Collections.unmodifiableList(uniqueConstraints) : Collections.<UniqueConstraint>emptyList();
	}
	
	final void registerUniqueConstraint(final UniqueConstraint constraint)
	{
		if(constraint==null)
			throw new NullPointerException();
		
		if(uniqueConstraints==null)
		{
			uniqueConstraints = new ArrayList<UniqueConstraint>();
		}
		else
		{
			if(uniqueConstraints.contains(constraint))
				throw new RuntimeException(constraint.toString());
		}
		
		uniqueConstraints.add(constraint);
	}

	/**
	 * Finds an item by it's unique attributes.
	 * @return null if there is no matching item.
	 */
	public final Item searchUnique(final E value)
	{
		// TODO: search nativly for unique constraints
		return getType().searchUnique(new EqualCondition<E>(this, value));
	}

	public final SetValue map(final E value)
	{
		return new SetValue(this, value);
	}
	
	public final Map<? extends Attribute, ? extends Object> execute(final E value, final Item exceptionItem)
	{
		return Collections.singletonMap(this, value);
	}
	
	public final EqualCondition isNull()
	{
		return new EqualCondition<E>(this, null);
	}
	
	public final NotEqualCondition isNotNull()
	{
		return new NotEqualCondition<E>(this, null);
	}
	
	public final EqualCondition<E> equal(final E value)
	{
		return new EqualCondition<E>(this, value);
	}
	
	public final NotEqualCondition notEqual(final E value)
	{
		return new NotEqualCondition<E>(this, value);
	}
	
	public final LessCondition less(final E value)
	{
		return new LessCondition<E>(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final E value)
	{
		return new LessEqualCondition<E>(this, value);
	}
	
	public final GreaterCondition greater(final E value)
	{
		return new GreaterCondition<E>(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final E value)
	{
		return new GreaterEqualCondition<E>(this, value);
	}
	
}

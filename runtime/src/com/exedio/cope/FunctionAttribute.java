/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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



public abstract class FunctionAttribute
	extends Attribute
	implements Function
{
	private final Class valueClass;
	private final String valueClassName;
	private final UniqueConstraint implicitUniqueConstraint;
	private ArrayList uniqueConstraints;
	
	protected FunctionAttribute(final Option option, final Class valueClass, final String valueClassName)
	{
		super(option);
		this.valueClass = valueClass;
		this.valueClassName = valueClassName;
		this.implicitUniqueConstraint =
			option.unique ?
				new UniqueConstraint((FunctionAttribute)this) :
				null;
	}
	
	final void initialize(final Type type, final String name)
	{
		super.initialize(type, name);
		
		if(implicitUniqueConstraint!=null)
			implicitUniqueConstraint.initialize(type, name + UniqueConstraint.IMPLICIT_UNIQUE_SUFFIX);
	}
	
	public abstract FunctionAttribute copyAsTemplate();
	abstract Object get(Row row);
	abstract void set(Row row, Object surface);
	
	final Option getTemplateOption()
	{
		if(isReadOnly())
			if(isMandatory())
				return Item.READ_ONLY;
			else
				return Item.READ_ONLY_OPTIONAL;
		else
			if(isMandatory())
				return Item.MANDATORY;
			else
				return Item.OPTIONAL;
	}
	
	/**
	 * Checks attribute values set by
	 * {@link Item#setAttribute(FunctionAttribute,Object)} (for <code>initial==false</code>)
	 * and {@link Item(FunctionAttribute[])} (for <code>initial==true</code>)
	 * and throws the exception specified there.
	 */
	final void checkValue(final Object value, final Item item)
		throws
			MandatoryViolationException,
			LengthViolationException
	{
		if(value == null)
		{
			if(isMandatory())
				throw new MandatoryViolationException(item, this);
		}
		else
		{
			if(value.equals("") &&
					isMandatory() &&
					!getType().getModel().supportsEmptyStrings()) // TODO dont call supportsEmptyStrings that often
				throw new MandatoryViolationException(item, this);
				
			if(!(valueClass.isAssignableFrom(value.getClass())))
			{
				throw new ClassCastException(
						"expected " + valueClassName +
						", got " + value.getClass().getName() +
						" for " + getName());
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
	}
	
	private static final Entity getEntity(final Item item)
	{
		return getEntity(item, true);
	}

	private static final Entity getEntity(final Item item, final boolean present)
	{
		return item.type.getModel().getCurrentTransaction().getEntity(item, present);
	}

	public final Object getObject(final Item item)
	{
		if(!getType().isAssignableFrom(item.type))
			throw new RuntimeException("attribute "+toString()+" does not belong to type "+item.type.toString());
		
		return getEntity(item).get(this);
	}

	public final void append(final Statement bf, final Join join)
	{
		bf.append(getColumn(), join);
	}
		
	public final void appendParameter(final Statement bf, final Object value)
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
	public List getUniqueConstraints()
	{
		return uniqueConstraints!=null ? Collections.unmodifiableList(uniqueConstraints) : Collections.EMPTY_LIST;
	}
	
	final void registerUniqueConstraint(final UniqueConstraint constraint)
	{
		if(constraint==null)
			throw new NullPointerException();
		
		if(uniqueConstraints==null)
		{
			uniqueConstraints = new ArrayList();
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
	public final Item searchUnique(final Object value)
	{
		// TODO: search nativly for unique constraints
		return getType().searchUnique(new EqualCondition(this, value));
	}

	public final EqualCondition isNull()
	{
		return new EqualCondition(this, null);
	}
	
	public final NotEqualCondition isNotNull()
	{
		return new NotEqualCondition(this, null);
	}
	
}

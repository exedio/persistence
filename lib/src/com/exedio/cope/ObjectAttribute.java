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

import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.NotEqualCondition;


public abstract class ObjectAttribute
	extends Attribute
	implements Function
{
	private final Class valueClass;
	private final String valueClassName;
	private final UniqueConstraint singleUniqueConstraint;
	
	protected ObjectAttribute(final Option option, final Class valueClass, final String valueClassName)
	{
		super(option);
		this.valueClass = valueClass;
		this.valueClassName = valueClassName;
		this.singleUniqueConstraint =
			option.unique ?
				new UniqueConstraint((ObjectAttribute)this) :
				null;
	}
	
	final void initialize(final Type type, final String name)
	{
		super.initialize(type, name);
		
		if(singleUniqueConstraint!=null)
			singleUniqueConstraint.initialize(type, name);
	}
	
	public abstract ObjectAttribute copyAsTemplate();
	abstract Object cacheToSurface(Object cache);
	abstract Object surfaceToCache(Object surface);
	
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
	 * {@link Item#setAttribute(ObjectAttribute,Object)} (for <code>initial==false</code>)
	 * and {@link Item(ObjectAttribute[])} (for <code>initial==true</code>)
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

	public void append(final Statement bf, final Join join)
	{
		bf.text.
			append(join!=null ? bf.getName(join) : getType().getTable().protectedID).
			append('.').
			append(getColumn().protectedID);
	}
		
	/**
	 * Returns the unique constraint of this attribute,
	 * if there is a unique constraint covering this attribute and this attribute only.
	 * Does return null, if there is no such unique constraint,
	 * i.e. this attribute is not covered by any unique constraint,
	 * or this attribute is covered by a unique constraint covering more
	 * attributes than this attribute.
	 */
	public UniqueConstraint getSingleUniqueConstraint()
	{
		return singleUniqueConstraint;
	}
	
	public final Item searchUnique(final Object value)
	{
		// TODO: search nativly for unique constraints
		return getType().searchUnique(new EqualCondition(null, this, value));
	}

	public final EqualCondition isNull()
	{
		return new EqualCondition(null, this, null);
	}
	
	public final NotEqualCondition isNotNull()
	{
		return new NotEqualCondition(this, null);
	}
	
}

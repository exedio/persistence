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
package com.exedio.cope.lib;

import com.exedio.cope.lib.search.EqualCondition;


public abstract class ObjectAttribute
	extends Attribute
	implements Function
{
	protected ObjectAttribute(final Option option)
	{
		super(option);
	}
	
	abstract Object cacheToSurface(Object cache);
	abstract Object surfaceToCache(Object surface);
	
	/**
	 * Checks attribute values set by
	 * {@link Item#setAttribute(ObjectAttribute,Object)} (for <code>initial==false</code>)
	 * and {@link Item(ObjectAttribute[])} (for <code>initial==true</code>)
	 * and throws the exception specified there.
	 */
	void checkValue(final boolean initial, final Object value, final Item item)
		throws
			ReadOnlyViolationException,
			NotNullViolationException,
			LengthViolationException
	{
		if(!initial && isReadOnly())
			throw new ReadOnlyViolationException(item, this);
		if(isNotNull() && value == null)
			throw new NotNullViolationException(item, this);
	}

	public void append(final Statement bf)
	{
		bf.text.
			append(getType().getTable().protectedID).
			append('.').
			append(getMainColumn().protectedID);
	}
		
	public final Item searchUnique(final Object value)
	{
		// TODO: search nativly for unique constraints
		return getType().searchUnique(new EqualCondition(this, value));
	}

}

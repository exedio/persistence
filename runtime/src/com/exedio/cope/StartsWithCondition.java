/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;

public final class StartsWithCondition extends Condition
{
	public final DataField field;
	public final byte[] value;
	
	/**
	 * Creates a new StartsWithCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method
	 * {@link DataField#startsWith(byte[])}.
	 */
	public StartsWithCondition(final DataField field, final byte[] value)
	{
		this.field = field;
		this.value = value;

		if(field==null)
			throw new NullPointerException("field must not be null");
		if(value==null)
			throw new NullPointerException("value must not be null");
	}
	
	@Override
	void append(final Statement bf)
	{
		bf.appendStartsWith(field, value);
	}
	
	@Override
	void check(final TC tc)
	{
		//Cope.check(field, tc, null); TODO
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof StartsWithCondition))
			return false;
		
		final StartsWithCondition o = (StartsWithCondition)other;
		
		return field.equals(o.field) && Arrays.equals(value, o.value);
	}
	
	@Override
	public int hashCode()
	{
		return field.hashCode() ^ Arrays.hashCode(value) ^ 1872643;
	}
	
	@Override
	void toString(final StringBuilder bf, final boolean key, final Type defaultType)
	{
		field.toString(bf, defaultType);
		bf.append(" startsWith ").
			append(toStringForValue(value, key))
			;
	}
}

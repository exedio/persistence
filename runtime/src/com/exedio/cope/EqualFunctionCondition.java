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

public final class EqualFunctionCondition extends Condition
{
	// TODO SOON rename to function1/2
	public final Function attribute1;
	public final Function attribute2;

	/**
	 * Creates a new EqualFunctionCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see StringAttribute#equal(StringAttribute)
	 */
	public EqualFunctionCondition(
				final Function attribute1,
				final Function attribute2)
	{
		if(attribute1==null)
			throw new NullPointerException("attribute1 must not be null");
		if(attribute2==null)
			throw new NullPointerException("attribute2 must not be null");

		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
	}

	void append(final Statement bf)
	{
		bf.append(attribute1, (Join)null).
			append('=').
			append(attribute2, (Join)null);
	}

	void check(final Query query)
	{
		check(attribute1, query);
		check(attribute2, query);
	}

	public boolean equals(final Object other)
	{
		if(!(other instanceof EqualFunctionCondition))
			return false;
		
		final EqualFunctionCondition o = (EqualFunctionCondition)other;
		
		return attribute1.equals(o.attribute1) && attribute2.equals(o.attribute2);
	}
	
	public int hashCode()
	{
		return attribute1.hashCode() ^ attribute2.hashCode();
	}

	public String toString()
	{
		return attribute1.toString() + '=' + attribute2.toString();
	}

}

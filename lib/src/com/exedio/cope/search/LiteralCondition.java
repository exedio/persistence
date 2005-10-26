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

package com.exedio.cope.search;

import com.exedio.cope.Join;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.Query;
import com.exedio.cope.Statement;

public abstract class LiteralCondition extends Condition
{
	private final String operator; 
	public final ObjectAttribute attribute;
	public final Object value;

	protected LiteralCondition(final String operator, final ObjectAttribute attribute, final Object value)
	{
		this.operator = operator;
		this.attribute = attribute;
		this.value = value;

		if(operator==null)
			throw new NullPointerException();
		if(attribute==null)
			throw new NullPointerException();
		if(value==null)
			throw new NullPointerException();
	}
	
	public final void appendStatement(final Statement bf)
	{
		bf.append(attribute, (Join)null);
		if(value!=null)
			bf.append(operator).
				appendValue(attribute, value);
		else
			bf.append(" is null");
	}

	public final void check(final Query query)
	{
		check(attribute, query);
	}

	public boolean equals(final Object other)
	{
		if(!(other instanceof LiteralCondition))
			return false;
		
		final LiteralCondition o = (LiteralCondition)other;
		
		return operator.equals(o.operator) && attribute.equals(o.attribute) && value.equals(o.value);
	}
	
	public int hashCode()
	{
		return operator.hashCode() ^ attribute.hashCode() ^ value.hashCode() ^ 918276;
	}

	public final String toString()
	{
		return attribute.getName() + operator + '\'' + value + '\'';
	}

}

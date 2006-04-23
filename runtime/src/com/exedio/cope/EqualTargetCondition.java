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


public final class EqualTargetCondition extends Condition // TODO replace by EqualAttributeCondition
{
	public final ItemAttribute attribute;
	final Join targetJoin;

	/**
	 * Creates a new EqualTargetCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper methods.
	 * @see ItemAttribute#equalTarget()
	 * @see ItemAttribute#equalTarget(Join)
	 */
	public EqualTargetCondition(final ItemAttribute attribute, final Join targetJoin)
	{
		if(attribute==null)
			throw new NullPointerException("attribute must not be null");

		this.attribute = attribute;
		this.targetJoin = targetJoin;
		
		if(targetJoin!=null && targetJoin.getType()!=attribute.getTargetType())
			throw new RuntimeException("invalid type of join, expected "+targetJoin.getType()+" but was "+attribute.getTargetType());
	}

	void append(final Statement bf)
	{
		bf.append(attribute, (Join)null).
			append('=').
			appendPK(attribute.getTargetType(), targetJoin);
	}

	void check(final Query query)
	{
		check(attribute, query);
		check(attribute.getTargetType(), query);
	}

	public boolean equals(final Object other)
	{
		if(!(other instanceof EqualTargetCondition))
			return false;
		
		final EqualTargetCondition o = (EqualTargetCondition)other;
		
		return attribute.equals(o.attribute) && equals(targetJoin, o.targetJoin);
	}
	
	public int hashCode()
	{
		return attribute.hashCode() ^ hashCode(targetJoin) ^ 283471239;
	}

	public String toString()
	{
		return attribute.getName() + "=" + attribute.getTargetType().getJavaClass().getName() + ".PK";
	}

}

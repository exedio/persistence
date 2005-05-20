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

import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Query;
import com.exedio.cope.Statement;

public final class JoinCondition extends Condition
{
	public final ItemAttribute attribute;

	public JoinCondition(final ItemAttribute attribute)
	{
		this.attribute = attribute;
	}

	public final void appendStatement(final Statement bf)
	{
		bf.append(attribute).
			append('=').
			appendPK(attribute.getTargetType());
	}

	public final void check(final Query query)
	{
		check(attribute, query);
		check(attribute.getTargetType(), query);
	}

	public final String toString()
	{
		return attribute.getName() + "=" + attribute.getTargetType().getJavaClass().getName() + ".PK";
	}

}

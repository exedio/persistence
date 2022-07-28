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

import java.util.SortedSet;

final class TypeColumn extends StringColumn
{
	private final String itemColumnQuotedID;

	TypeColumn(
			final Table table,
			final ItemColumn itemColumn,
			final boolean optional,
			final int minLength,
			final SortedSet<String> allowedValues)
	{
		super(table, itemColumn.id + "Type"/* not equal to "name"! */, false, optional, minLength, allowedValues);
		this.itemColumnQuotedID = itemColumn.quotedID;
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		super.makeSchema(dsmf);

		if(kind.forbidsNull())
			return;

		newCheck(dsmf, "NS",
				"((" + quotedID + " IS NOT NULL) AND (" + itemColumnQuotedID + " IS NOT NULL)) OR " +
				"((" + quotedID + " IS "+ "NULL) AND (" + itemColumnQuotedID + " IS "+ "NULL))");
	}
}

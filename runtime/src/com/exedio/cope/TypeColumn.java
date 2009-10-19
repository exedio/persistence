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

final class TypeColumn extends StringColumn
{
	private final String itemColumnProtectedID;
	
	TypeColumn(
			final Table table,
			final ItemColumn itemColumn,
			final boolean optional,
			final String[] allowedValues)
	{
		super(table, null, itemColumn.id + "Type"/* not equal to "name"! */, optional, allowedValues);
		this.itemColumnProtectedID = itemColumn.protectedID;
	}
	
	@Override
	String getCheckConstraintIfNotNull()
	{
		final String superResult = super.getCheckConstraintIfNotNull();
		if(!optional)
			return superResult;
		
		assert superResult!=null;
		return "(" + superResult + ") AND (" + itemColumnProtectedID + " IS NOT NULL)";
	}
	
	@Override
	String getCheckConstraintIfNull()
	{
		if(super.getCheckConstraintIfNull()!=null)
			throw new RuntimeException(); // not implemented
		
		return itemColumnProtectedID + " IS NULL";
	}
	
	/*@Override
	String getCheckConstraintIgnoringMandatory()
	{
		final String superResult = super.getCheckConstraintIgnoringMandatory();
		if(!optional)
			return superResult;
		
		assert superResult!=null;
		return
			"(" + superResult + ") AND (" +
				"((" + protectedID + " IS NULL) AND ("     + itemColumnProtectedID + " IS NULL))" +
				" OR " +
				"((" + protectedID + " IS NOT NULL) AND (" + itemColumnProtectedID + " IS NOT NULL))" +
			")";
	}*/
}

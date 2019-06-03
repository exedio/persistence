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

package com.exedio.dsmf;

public final class UniqueConstraint extends Constraint
{
	private final String clause;

	/**
	 * @deprecated Use {@link Table#newUnique(Column,String,String)} instead
	 */
	@Deprecated
	public UniqueConstraint(
			final Table table,
			final Column column,
			final String name,
			final String clause)
	{
		this(table, column, name, true, clause);
	}

	UniqueConstraint(
			final Table table,
			final Column column,
			final String name,
			final boolean required,
			final String clause)
	{
		super(table, column, name, Type.Unique, required, clause);

		if(clause==null)
			throw new RuntimeException(name);

		this.clause = clause;
		//System.out.println("-------------"+name+"-"+clause);
	}

	public String getClause()
	{
		return clause;
	}

	@Override
	void appendCreateClause(final StringBuilder bf)
	{
		bf.append("CONSTRAINT ").
			append(quoteName(name)).
			append(" UNIQUE").
			append(clause);
	}

	@Override
	void drop(final StringBuilder bf)
	{
		dialect.dropUniqueConstraint(bf, quoteName(table.name), quoteName(name));
	}
}

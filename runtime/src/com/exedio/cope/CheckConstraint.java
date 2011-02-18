/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Intern.intern;

public final class CheckConstraint extends Feature
{
	private static final long serialVersionUID = 1l;

	private final Condition condition;

	public CheckConstraint(final Condition condition)
	{
		if(condition==null)
			throw new NullPointerException("condition");
		if(condition instanceof Condition.Literal)
			throw new IllegalArgumentException("literal condition makes no sense, but was Condition." + condition.toString());

		this.condition = condition;
	}

	public Condition getCondition()
	{
		return condition;
	}

	void check(final Item item, final Entity entity, final Item exceptionItem)
	{
		if(!condition.get(item))
		{
			entity.discard();
			throw new CheckViolationException(exceptionItem, this);
		}
	}

	void makeSchema(final Table table, final com.exedio.dsmf.Table dsmfTable)
	{
		final Statement statement = new Statement(table.database.dialect);
		condition.append(statement);

		new com.exedio.dsmf.CheckConstraint(
				dsmfTable,
				intern(table.makeGlobalID(getSchemaName())),
				statement.getText());
	}
}

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

import static com.exedio.cope.Intern.intern;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Map;

public final class CheckConstraint extends Feature implements Copyable
{
	private static final long serialVersionUID = 1l;

	private final Condition condition;

	public CheckConstraint(final Condition condition)
	{
		this.condition = requireNonNull(condition, "condition");

		if(condition instanceof Condition.Literal)
			throw new IllegalArgumentException("literal condition makes no sense, but was Condition." + condition);
		try
		{
			condition.requireSupportForGetTri();
		}
		catch(final UnsupportedGetException e)
		{
			throw new IllegalArgumentException(
					"check constraint condition contains unsupported function: " + e.function);
		}
	}

	@Override
	public CheckConstraint copy(final CopyMapper mapper)
	{
		return new CheckConstraint(condition.copy(mapper));
	}

	public Condition getCondition()
	{
		return condition;
	}

	void check(final FieldValues item)
	{
		try
		{
			if(condition.getTri(item)==Trilean.False)
				throw new CheckViolationException(item, this);
		}
		catch(final UnsupportedGetException e)
		{
			throw new RuntimeException(
					"should not happen, as condition has been tested for support in constructor", e);
		}
	}

	public void check(final Map<FunctionField<?>, Object> values)
	{
		check(new FieldValues(values));
	}

	void makeSchema(final Table table, final com.exedio.dsmf.Table dsmf)
	{
		if(!isSupportedBySchemaIfSupportedByDialect())
			return;

		final Statement statement = new Statement(table.database.dialect, table.database.executor.marshallers);
		condition.append(statement);

		dsmf.newCheck(
				intern(table.makeGlobalID(TrimClass.Constraint, getDeclaredSchemaName())),
				statement.getText());
	}

	/**
	 * Return true iff this check constraint can be supported by
	 * a database dialect supporting check constraints.
	 * This is the case, iff all table columns mentioned by
	 * {@link #getCondition() the condition}
	 * if the check constraints are located within the same database table.
	 * <p>
	 * The result of this method does not depend on
	 * {@link SchemaInfo#supportsCheckConstraint(Model)}.
	 * <p>
	 * If returns false, you should call {@link #check()} for checking
	 * database consistency after any modifications of the database
	 * bypassing cope.
	 */
	public boolean isSupportedBySchemaIfSupportedByDialect()
	{
		final Type<?> type = getType();
		final HashSet<Table> tables = type.newQuery(condition).getTables();

		assert tables.contains(type.table);
		return tables.size()==1;
	}

	/**
	 * Checks, whether the database fulfills this check constraint.
	 * Should be called for checking
	 * database consistency after any modifications of the database
	 * bypassing cope.
	 * Is needed especially iff {@link CheckConstraint#isSupportedBySchemaIfSupportedByDialect()}
	 * returns false.
	 * <p>
	 * Returns the number of items violating the check constraint.
	 * As a consequence, this methods returns 0 (zero),
	 * iff the database fulfills this check constraint.
	 */
	public int check()
	{
		return getType().newQuery(condition.not()).total();
	}
}

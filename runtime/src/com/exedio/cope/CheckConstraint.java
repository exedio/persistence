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

import com.exedio.cope.ConnectProperties.TrimClass;
import com.exedio.dsmf.Schema;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class CheckConstraint extends AbstractFeature implements Copyable
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Condition condition;
	private final Set<Field<?>> fieldsCoveredByCondition;

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
		{
			final ArrayList<Field<?>> l = new ArrayList<>();
			condition.forEachFieldCovered(l::add);
			this.fieldsCoveredByCondition = Set.copyOf(l);
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

	/**
	 * The order of the set is undefined.
	 */
	public Set<Field<?>> getFieldsCoveredByCondition()
	{
		return fieldsCoveredByCondition;
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

	void makeSchema(final Table table, final Schema schema)
	{
		final Table tableAffected = getOnlyTableAffected();
		if(tableAffected==null)
			return;

		final List<? extends Type<?>> typesOfConstraintIfDivergent;
		{
			final Type<?> type = getType();
			typesOfConstraintIfDivergent =
					tableAffected!=type.getTable() ? type.getTypesOfInstances() : null;
		}
		final Statement statement = new Statement(table.database.dialect, table.database.executor.marshallers);
		if(typesOfConstraintIfDivergent!=null)
		{
			// below is an implication: "p implies q" is equal to "not p or q"

			statement.
					append('(').
					append(tableAffected.typeColumn);

			switch(typesOfConstraintIfDivergent.size())
			{
				case 0 -> throw new RuntimeException();
				case 1 ->
						statement.
								append("<>").
								appendParameter(typesOfConstraintIfDivergent.get(0).schemaId);
				default ->
				{
					statement.append(" NOT IN (");
					boolean first = true;
					for(final Type<?> type : typesOfConstraintIfDivergent)
					{
						if(first)
							first = false;
						else
							statement.append(',');

						statement.appendParameter(type.schemaId);
					}
					statement.append(')');
				}
			}
			statement.append(") OR (");
		}
		condition.append(statement);
		if(typesOfConstraintIfDivergent!=null)
			statement.
					append(')');

		schema.getTable(tableAffected.id).newCheck(
				// NOTE: Divergent name prefix
				// Below, table is used instead of tableAffected to create the global id if the constraint.
				// This may seem weird - the table prefix of the global id not matching the table of the constraint.
				// However, quite often there are equally named check constraints in subclasses
				// affecting just one field of the super class.
				// So we avoid name collisions in these cases.
				intern(table.makeGlobalID(TrimClass.standard, getDeclaredSchemaName())),
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
		return getOnlyTableAffected()!=null;
	}

	private Table getOnlyTableAffected()
	{
		final OnlyTableConsumer otc = new OnlyTableConsumer();
		condition.forEachFieldCovered(otc);
		return otc.multi ? null : otc.result;
	}

	private static class OnlyTableConsumer implements Consumer<Field<?>>
	{
		Table result = null;
		boolean multi = false;

		@Override
		public void accept(final Field<?> field)
		{
			final Table fieldTable = field.getColumn().table;

			if(result==null)
				result = fieldTable;
			else if(result!=fieldTable)
				multi = true;
		}
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

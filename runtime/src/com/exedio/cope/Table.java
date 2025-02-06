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

import com.exedio.dsmf.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

final class Table
{
	final Database database;
	final String id;
	final String quotedID;
	final IntegerColumn primaryKey;
	final StringColumn typeColumn;
	final IntegerColumn updateCounter;
	volatile boolean knownToBeEmptyForTest = false;

	Table(
			final Database database,
			final String id,
			final Type<?> supertype,
			final int typeColumnMinLength,
			final SortedSet<String> typesOfInstancesColumnValues,
			final boolean updateCounter,
			final long maximum)
	{
		this.database = database;
		this.id = intern(TrimClass.legacy.trimmer.trimString(id));
		this.quotedID = intern(database.dsmfDialect.quoteName(this.id));
		//noinspection ThisEscapedInObjectConstruction
		this.primaryKey =
			(supertype!=null)
			? new ItemColumn(this, supertype, maximum)
			: new IntegerColumn(this, maximum);
		//noinspection ThisEscapedInObjectConstruction
		this.typeColumn =
			(typesOfInstancesColumnValues!=null)
			? new StringColumn(this, TYPE_COLUMN_NAME, true, false, typeColumnMinLength, typesOfInstancesColumnValues)
			: null;
		//noinspection ThisEscapedInObjectConstruction
		this.updateCounter =
			updateCounter
			? new IntegerColumn(this, UPDATE_COUNTER_COLUMN_NAME, true, false, 0, Integer.MAX_VALUE, false)
			: null;
		//noinspection ThisEscapedInObjectConstruction
		database.addTable(this);
	}

	private List<Column> columns = null;
	private List<Column> allColumnsModifiable = new ArrayList<>();
	private List<Column> allColumns = null;

	@SuppressWarnings("TypeParameterExtendsFinalClass") // OK: effectively makes collection somewhat compiler-unmodifiable
	private List<? extends UniqueConstraint> uniqueConstraints = null;
	@SuppressWarnings("TypeParameterExtendsFinalClass") // OK: effectively makes collection somewhat compiler-unmodifiable
	private List<? extends  CheckConstraint>  checkConstraints = null;

	/**
	 * The column name for the primary key.
	 * The value "this" prevents name collisions
	 * with columns for cope fields,
	 * since "this" is a reserved java keyword,
	 * which cannot be used for java fields.
	 */
	static final String PK_COLUMN_NAME = "this";

	/**
	 * The column name for the type information.
	 * The value "class" prevents name collisions
	 * with columns for cope fields,
	 * since "class" is a reserved java keyword,
	 * which cannot be used for java fields.
	 */
	private static final String TYPE_COLUMN_NAME = "class";

	/**
	 * The column name for the update counter.
	 * The value "catch" prevents name collisions
	 * with columns for cope fields,
	 * since "catch" is a reserved java keyword,
	 * which cannot be used for java fields.
	 */
	private static final String UPDATE_COUNTER_COLUMN_NAME = "catch";

	/**
	 * A name for aliases is sql statements.
	 * The value prevents name collisions
	 * with columns for cope fields / types,
	 * since is is a reserved java keyword,
	 * which cannot be used for java fields / classes.
	 */
	static final String SQL_ALIAS_1 = "return", SQL_ALIAS_2 = "break";

	void addColumn(final Column column)
	{
		allColumnsModifiable.add(column);
	}

	/**
	 * Returns &quot;payload&quot; columns of this type only,
	 * excluding primary key column
	 * and the optional type column for the primary key.
	 * @see #getAllColumns()
	 * @see #primaryKey
	 */
	@SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType", "RedundantSuppression"}) // columns is unmodifiable
	List<Column> getColumns()
	{
		if(columns==null)
			throw new RuntimeException();

		return columns;
	}

	/**
	 * Returns all columns of this type,
	 * including primary key column.
	 * @see #getColumns()
	 * @see #primaryKey
	 */
	@SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType", "RedundantSuppression"}) // allColumns is unmodifiable
	List<Column> getAllColumns()
	{
		if(allColumns==null)
			throw new RuntimeException();

		return allColumns;
	}

	@SuppressWarnings({
			"AssignmentToCollectionOrArrayFieldFromParameter", "RedundantSuppression",
			"TypeParameterExtendsFinalClass"}) // OK: effectively makes collection somewhat compiler-unmodifiable
	void setUniqueConstraints(final List<? extends UniqueConstraint> uniqueConstraints)
	{
		if(uniqueConstraints==null)
			throw new IllegalArgumentException();
		if(allColumns!=null)
			throw new RuntimeException();
		if(this.uniqueConstraints!=null)
			throw new RuntimeException();

		this.uniqueConstraints = uniqueConstraints;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType", "RedundantSuppression"}) // OK: effectively makes collection somewhat compiler-unmodifiable
	List<? extends UniqueConstraint> getUniqueConstraints()
	{
		if(uniqueConstraints==null)
			throw new RuntimeException();

		return uniqueConstraints;
	}

	@SuppressWarnings({
			"AssignmentToCollectionOrArrayFieldFromParameter", "RedundantSuppression",
			"TypeParameterExtendsFinalClass"}) // OK: effectively makes collection somewhat compiler-unmodifiable
	void setCheckConstraints(final List<? extends CheckConstraint> checkConstraints)
	{
		if(checkConstraints==null)
			throw new IllegalArgumentException();
		if(allColumns!=null)
			throw new RuntimeException();
		if(this.checkConstraints!=null)
			throw new RuntimeException();

		this.checkConstraints = checkConstraints;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType", "RedundantSuppression"}) // OK: effectively makes collection somewhat compiler-unmodifiable
	List<? extends CheckConstraint> getCheckConstraints()
	{
		if(checkConstraints==null)
			throw new RuntimeException();

		return checkConstraints;
	}

	private boolean assertSynthetic()
	{
		for(final Column c : allColumnsModifiable)
			if(c.synthetic != (primaryKey==c || typeColumn==c || updateCounter==c))
				return false;

		return true;
	}

	void finish()
	{
		assert assertSynthetic();

		final ArrayList<Column> columns = new ArrayList<>();
		for(final Column column : allColumnsModifiable)
			if(!column.synthetic)
				columns.add(column);

		this.columns = List.copyOf(columns);
		allColumns = List.copyOf(allColumnsModifiable);
		allColumnsModifiable = null;
	}

	String makeGlobalID(final TrimClass trimClass, final String suffix)
	{
		return trimClass.trimmer.trimString(id + '_' + suffix);
	}

	void makeSchema(final Schema schema)
	{
		final com.exedio.dsmf.Table result = schema.newTable(id);

		for(final Column c : getAllColumns())
			c.makeSchema(result);

		for(final UniqueConstraint uc : getUniqueConstraints())
			uc.makeSchema(result);

		for(final CheckConstraint cc : getCheckConstraints())
			cc.makeSchema(this, schema);
	}

	@Override
	public String toString()
	{
		return id;
	}
}

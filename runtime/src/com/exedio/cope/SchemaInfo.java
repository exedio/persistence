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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Returns information about the database schema accessed by cope
 * - <b>use with care!</b>
 * <p>
 * This information is needed only, if you want to access
 * the database without cope.
 * In this case you should really know, what you are doing.
 * Any INSERT/UPDATE/DELETE on the database bypassing cope
 * may lead to inconsistent caches.
 * Please note, that the results of all methods may vary,
 * if a cope model is configured for different databases.
 */
public final class SchemaInfo
{
	/**
	 * Its your responsibility to close the returned connection.
	 * @see Connection#close()
	 */
	public static Connection newConnection(final Model model) throws SQLException
	{
		return model.connect().connectionFactory.createRaw();
	}

	/**
	 * Quotes a database name.
	 * This prevents the name from being interpreted as a SQL keyword.
	 */
	public static String quoteName(final Model model, final String name)
	{
		if(model==null)
			throw new NullPointerException("model");
		if(name==null)
			throw new NullPointerException("name");
		if(name.isEmpty())
			throw new IllegalArgumentException("name must not be empty");

		return model.connect().dialect.dsmfDialect.quoteName(name);
	}

	public static boolean supportsCheckConstraints(final Model model)
	{
		return model.connect().database.dsmfDialect.supportsCheckConstraints();
	}

	public static boolean supportsNativeDate(final Model model)
	{
		return model.connect().supportsNativeDate();
	}

	/**
	 * Returns whether detecting
	 * {@link UniqueViolationException}s from
	 * {@link SQLException}s is supported.
	 * If not, then cope must issue explicit searches before
	 * any insert/update covering a unique constraint.
	 */
	public static boolean supportsUniqueViolation(final Model model)
	{
		return model.connect().executor.supportsUniqueViolation;
	}

	public static boolean supportsSequences(final Model model)
	{
		return model.connect().dialect.dsmfDialect.supportsSequences();
	}

	/**
	 * Returns the name of database table for the type.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Type#getID() id} of the type.
	 */
	public static String getTableName(final Type type)
	{
		return type.table.idLower;
	}

	/**
	 * Returns the name of primary key column in the database for the type.
	 * If not configured otherwise
	 * the name equals "this".
	 */
	public static String getPrimaryKeyColumnName(final Type type)
	{
		return type.table.primaryKey.id;
	}

	/**
	 * Returns the name of type column in the database for the type.
	 * If not configured otherwise
	 * the name equals "class".
	 * @throws IllegalArgumentException
	 *         if there is no type column for this type,
	 *         because <code>{@link Type#getTypesOfInstances()}</code>
	 *         contains one type only.
	 */
	public static String getTypeColumnName(final Type type)
	{
		final StringColumn column = type.table.typeColumn;
		if(column==null)
			throw new IllegalArgumentException("no type column for " + type);

		return column.id;
	}

	/**
	 * @see #getUpdateCounterColumnName(Type)
	 */
	public static boolean isUpdateCounterEnabled(final Model model)
	{
		return model.connect().properties.updateCounter.booleanValue();
	}

	/**
	 * Returns the name of update counter column in the database for the type.
	 * If not configured otherwise
	 * the name equals "catch".
	 * @throws IllegalArgumentException
	 *         if there is no update counter column for this type,
	 *         because {@link #isUpdateCounterEnabled(Model) Update Counters}
	 *         has been switched off,
	 *         or because there are no modifiable (non-{@link Field#isFinal() final})
	 *         fields on the type or its subtypes.
	 */
	public static String getUpdateCounterColumnName(final Type type)
	{
		final IntegerColumn column = type.table.updateCounter;
		if(column==null)
			throw new IllegalArgumentException("no update counter for " + type);

		return column.id;
	}

	/**
	 * Returns the name of database column for the field.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Field#getName() name} of the field.
	 */
	public static String getColumnName(final Field field)
	{
		return field.getColumn().id;
	}

	/**
	 * Returns the name of type column in the database for the field.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Field#getName() name} of the field
	 * plus the appendix "Type".
	 * @throws IllegalArgumentException
	 *         if there is no type column for this ItemField,
	 *         because <code>{@link ItemField#getValueType() getValueType()}.{@link Type#getTypesOfInstances() getTypesOfInstances()}</code>
	 *         contains one type only.
	 */
	public static String getTypeColumnName(final ItemField field)
	{
		final Column column = field.getTypeColumn();
		if(column==null)
			throw new IllegalArgumentException("no type column for " + field);

		return column.id;
	}

	/**
	 * Returns the value of database column for the field
	 * and the given enum value.
	 */
	public static <E extends Enum<E>> int getColumnValue(final E value)
	{
		return EnumFieldType.get(value.getDeclaringClass()).getNumber(value);
	}

	private SchemaInfo()
	{
		// prevent instantiation
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getColumnValue(Enum)} instead.
	 */
	@Deprecated
	public static <E extends Enum<E>> int getColumnValue(final EnumField<E> field, final E value)
	{
		return field.valueType.getNumber(value);
	}

	/**
	 * @deprecated Use {@link #isUpdateCounterEnabled(Model)} instead
	 */
	@Deprecated
	public static boolean isConcurrentModificationDetectionEnabled(final Model model)
	{
		return isUpdateCounterEnabled(model);
	}

	/**
	 * @deprecated Use {@link #getUpdateCounterColumnName(Type)} instead
	 */
	@Deprecated
	public static String getModificationCounterColumnName(final Type type)
	{
		return getUpdateCounterColumnName(type);
	}
}

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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

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
 * may lead to <b>inconsistent caches</b>.
 * Please note, that the results of all methods may vary,
 * if a cope model is configured for different databases.
 * BEWARE:
 * This class is <b>not</b> part of the <b>stable API</b> of cope.
 * It may change its syntax and/or semantics in the future.
 */
public final class SchemaInfo
{
	/**
	 * Its your responsibility to close the returned connection.
	 * @see Connection#close()
	 */
	public static Connection newConnection(final Model model) throws SQLException
	{
		model.transactions.assertNoCurrentTransaction();

		return model.connect().connectionFactory.createRaw();
	}

	/**
	 * Quotes a database name.
	 * This prevents the name from being interpreted as a SQL keyword.
	 */
	public static String quoteName(final Model model, final String name)
	{
		requireNonNull(model, "model");
		requireNonEmpty(name, "name");

		return model.connect().dialect.dsmfDialect.quoteName(name);
	}

	public static boolean supportsCheckConstraints(final Model model)
	{
		return model.connect().supportsCheckConstraints;
	}

	public static boolean supportsNativeDate(final Model model)
	{
		return model.connect().supportsNativeDate;
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
		return model.connect().supportsUniqueViolation;
	}

	/**
	 * Returns the name of database table for the type.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Type#getID() id} of the type.
	 */
	public static String getTableName(final Type<?> type)
	{
		return type.table.idLower;
	}

	/**
	 * Returns the name of primary key column in the database for the type.
	 * If not configured otherwise
	 * the name equals "this".
	 */
	public static String getPrimaryKeyColumnName(final Type<?> type)
	{
		return type.table.primaryKey.id;
	}

	/**
	 * Returns the value of primary key column in the database for the item.
	 * @deprecated Use {@link #getPrimaryKeyColumnValueL(Item)} instead.
	 */
	@Deprecated
	public static int getPrimaryKeyColumnValue(final Item item)
	{
		return toIntExact(getPrimaryKeyColumnValueL(item));
	}

	/**
	 * Returns the value of primary key column in the database for the item.
	 */
	public static long getPrimaryKeyColumnValueL(final Item item)
	{
		return item.pk;
	}

	/**
	 * Returns the name of the sequence for generating values for the
	 * {@link #getPrimaryKeyColumnName(Type) primary key column}
	 * of the type.
	 * @throws IllegalArgumentException
	 *         if there is no such sequence for this type,
	 *         because primary keys are generated otherwise.
	 */
	public static String getPrimaryKeySequenceName(final Type<?> type)
	{
		final String result = type.getPrimaryKeySequenceSchemaName();
		if(result==null)
			throw new IllegalArgumentException("no sequence for " + type);

		return result;
	}

	/**
	 * Returns the name of type column in the database for the type.
	 * If not configured otherwise
	 * the name equals "class".
	 * Values suitable for this column can be retrieved by {@link #getTypeColumnValue(Type)}.
	 * @throws IllegalArgumentException
	 *         if there is no type column for this type,
	 *         because <code>{@link Type#getTypesOfInstances()}</code>
	 *         contains one type only.
	 */
	public static String getTypeColumnName(final Type<?> type)
	{
		final StringColumn column = type.table.typeColumn;
		if(column==null)
			throw new IllegalArgumentException("no type column for " + type);

		return column.id;
	}

	/**
	 * Returns the value to be put into a type column for the type.
	 * Defaults to {@link Type#getID()},
	 * but can be overridden by {@link CopeSchemaName}.
	 * @see #getTypeColumnName(Type)
	 * @see #getTypeColumnName(ItemField)
	 */
	public static String getTypeColumnValue(final Type<?> type)
	{
		return type.schemaId;
	}

	/**
	 * Returns the name of update counter column in the database for the type.
	 * If not configured otherwise
	 * the name equals "catch".
	 * @throws IllegalArgumentException
	 *         if there is no update counter column for this type,
	 *         because there are no modifiable (non-{@link Field#isFinal() final})
	 *         fields on the type or its subtypes.
	 */
	public static String getUpdateCounterColumnName(final Type<?> type)
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
	public static String getColumnName(final Field<?> field)
	{
		return field.getColumn().id;
	}

	/**
	 * Returns the name of type column in the database for the field.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Field#getName() name} of the field
	 * plus the appendix "Type".
	 * Values suitable for this column can be retrieved by {@link #getTypeColumnValue(Type)}.
	 * @throws IllegalArgumentException
	 *         if there is no type column for this ItemField,
	 *         because <code>{@link ItemField#getValueType() getValueType()}.{@link Type#getTypesOfInstances() getTypesOfInstances()}</code>
	 *         contains one type only.
	 */
	public static String getTypeColumnName(final ItemField<?> field)
	{
		final Column column = field.getTypeColumn();
		if(column==null)
			throw new IllegalArgumentException("no type column for " + field);

		return column.id;
	}

	public static String getForeignKeyConstraintName(final ItemField<?> field)
	{
		return ((ItemColumn)field.getColumn()).integrityConstraintName;
	}

	/**
	 * Returns the value of database column for the field
	 * and the given enum value.
	 * Defaults to 10 * ( {@link Enum#ordinal()} + 1 ),
	 * but can be overridden by {@link CopeSchemaValue}.
	 */
	@SuppressWarnings("unchecked") // I have no idea why I have to do this stuff
	public static int getColumnValue(final Enum<?> value)
	{
		return getColumnValueInternal((Enum)value);
	}

	private static <E extends Enum<E>> int getColumnValueInternal(final E value)
	{
		return EnumFieldType.get(value.getDeclaringClass()).getNumber(value);
	}

	/**
	 * Returns the name of the sequence for generating values for the
	 * {@link IntegerField#defaultToNext(int) defaultToNext}
	 * mechanism of the field.
	 * @throws IllegalArgumentException
	 *         if there is no such sequence for this field,
	 *         because values are generated otherwise.
	 */
	public static String getDefaultToNextSequenceName(final IntegerField field)
	{
		return field.getDefaultToNextSequenceName();
	}

	/**
	 * Returns the name of the sequence for generating values via
	 * {@link Sequence#next()}.
	 */
	public static String getSequenceName(final Sequence sequence)
	{
		return sequence.sequenceX.getSchemaName();
	}

	public static String getConstraintName(final UniqueConstraint constraint)
	{
		return constraint.getDatabaseID();
	}

	public static String search(final Query<?> query)
	{
		if(query.getCondition()==Condition.FALSE)
			return "skipped because condition==false: " + query;
		if(query.getPageLimitOrMinusOne()==0)
			return "skipped because limit==0: " + query;

		return search(query, Query.Mode.SEARCH);
	}

	public static String total(final Query<?> query)
	{
		if(query.getCondition()==Condition.FALSE)
			return "skipped because condition==false: " + query;

		return search(query, Query.Mode.TOTAL);
	}

	private static String search(final Query<?> query, final Query.Mode mode)
	{
		final StringBuilder bf = new StringBuilder();
		query.search(null, query.getType().getModel().connect().executor, mode, bf, null);
		return bf.toString();
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
	 * @deprecated always returns true
	 * @see #getUpdateCounterColumnName(Type)
	 */
	@Deprecated
	public static boolean isUpdateCounterEnabled(@SuppressWarnings("unused") final Model model)
	{
		return true;
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
	public static String getModificationCounterColumnName(final Type<?> type)
	{
		return getUpdateCounterColumnName(type);
	}

	/**
	 * @deprecated Always returns true, because all databases are required to support sequences.
	 * @param model is ignored
	 */
	@Deprecated
	public static boolean supportsSequences(final Model model)
	{
		return true;
	}

	/**
	 * @deprecated Always returns true, because all databases are required to support not-null columns.
	 */
	@Deprecated
	public static boolean supportsNotNull(final Model model)
	{
		model.connect(); // make sure it works only when connected
		return true;
	}
}

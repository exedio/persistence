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

import static com.exedio.cope.Statement.Mode.SQL_ONLY;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static com.exedio.cope.vault.VaultProperties.checkBucket;
import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Returns information about the database schema accessed by cope
 * - <b>use with care!</b>
 * <p>
 * This information is needed only, if you want to access
 * the database without cope.
 * In this case you should really know what you are doing.
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
	 * It's your responsibility to close the returned connection.
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

	/**
	 * @see #quoteName(Model, String)
	 */
	private static String quote(final Type<?> type, final String name)
	{
		return quoteName(type.getModel(), name);
	}

	/**
	 * @see #quoteName(Model, String)
	 */
	private static String quote(final Field<?> field, final String name)
	{
		return quoteName(field.getType().getModel(), name);
	}

	/**
	 * @deprecated Use {@link #supportsCheckConstraint(Model)} instead.
	 */
	@Deprecated
	public static boolean supportsCheckConstraints(final Model model)
	{
		return supportsCheckConstraint(model);
	}

	public static boolean supportsCheckConstraint(final Model model)
	{
		return model.connect().supportsCheckConstraint;
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
	 * @see #tableNameQuoted(Type)
	 */
	public static String tableName(final Type<?> type)
	{
		return type.getTable().id;
	}

	/**
	 * @return {@link #tableName(Type)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String tableNameQuoted(final Type<?> type)
	{
		return quote(type, tableName(type)); // TODO use quotedID
	}

	/**
	 * @deprecated Use {@link #tableName(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getTableName(final Type<?> type)
	{
		return tableName(type);
	}

	/**
	 * Returns the name of primary key column in the database for the type.
	 * If not configured otherwise
	 * the name equals "this".
	 * @see #primaryKeyColumnNameQuoted(Type)
	 */
	public static String primaryKeyColumnName(final Type<?> type)
	{
		return type.getTable().primaryKey.id;
	}

	/**
	 * @return {@link #primaryKeyColumnName(Type)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String primaryKeyColumnNameQuoted(final Type<?> type)
	{
		return quote(type, primaryKeyColumnName(type)); // TODO use quotedID
	}

	/**
	 * @deprecated Use {@link #primaryKeyColumnName(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getPrimaryKeyColumnName(final Type<?> type)
	{
		return primaryKeyColumnName(type);
	}

	/**
	 * Returns the value of primary key column in the database for the item.
	 */
	public static long columnValue(final Item value)
	{
		return value.pk;
	}

	/**
	 * @deprecated Use {@link #columnValue(Item)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static long getPrimaryKeyColumnValueL(final Item value)
	{
		return columnValue(value);
	}

	/**
	 * Returns the name of the sequence for generating values for the
	 * {@link #primaryKeyColumnName(Type) primary key column}
	 * of the type.
	 * @throws IllegalArgumentException
	 *         if there is no such sequence for this type,
	 *         because primary keys are generated otherwise.
	 */
	public static String primaryKeySequenceName(final Type<?> type)
	{
		final String result = type.getPrimaryKeySequenceSchemaName();
		if(result==null)
			throw new IllegalArgumentException("no sequence for " + type);

		return result;
	}

	/**
	 * @deprecated Use {@link #primaryKeySequenceName(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getPrimaryKeySequenceName(final Type<?> type)
	{
		return primaryKeySequenceName(type);
	}

	/**
	 * Returns the name of type column in the database for the type.
	 * If not configured otherwise
	 * the name equals "class".
	 * Values suitable for this column can be retrieved by {@link #typeColumnValue(Type)}.
	 * @throws IllegalArgumentException
	 *         if there is no type column for this type,
	 *         because <code>{@link Type#getTypesOfInstances()}</code>
	 *         contains one type only.
	 * @see #typeColumnNameQuoted(Type)
	 */
	public static String typeColumnName(final Type<?> type)
	{
		final StringColumn column = type.getTable().typeColumn;
		if(column==null)
			throw new IllegalArgumentException("no type column for " + type);

		return column.id;
	}

	/**
	 * @return {@link #typeColumnName(Type)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String typeColumnNameQuoted(final Type<?> type)
	{
		return quote(type, typeColumnName(type)); // TODO use quotedID
	}

	/**
	 * @deprecated Use {@link #typeColumnName(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getTypeColumnName(final Type<?> type)
	{
		return typeColumnName(type);
	}

	/**
	 * Returns the value to be put into a type column for the type.
	 * Defaults to {@link Type#getID()},
	 * but can be overridden by {@link CopeSchemaName}.
	 * @see #typeColumnName(Type)
	 * @see #typeColumnName(ItemField)
	 */
	public static String typeColumnValue(final Type<?> type)
	{
		return type.schemaId;
	}

	/**
	 * @deprecated Use {@link #typeColumnValue(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getTypeColumnValue(final Type<?> type)
	{
		return typeColumnValue(type);
	}

	/**
	 * Returns the name of update counter column in the database for the type.
	 * If not configured otherwise
	 * the name equals "catch".
	 * @throws IllegalArgumentException
	 *         if there is no update counter column for this type,
	 *         because there are no modifiable (non-{@link Field#isFinal() final})
	 *         fields on the type or its subtypes.
	 * @see #updateCounterColumnNameQuoted(Type)
	 */
	public static String updateCounterColumnName(final Type<?> type)
	{
		final IntegerColumn column = type.getTable().updateCounter;
		if(column==null)
			throw new IllegalArgumentException("no update counter for " + type);

		return column.id;
	}

	/**
	 * @return {@link #updateCounterColumnName(Type)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String updateCounterColumnNameQuoted(final Type<?> type)
	{
		return quote(type, updateCounterColumnName(type)); // TODO use quotedID
	}

	/**
	 * @deprecated Use {@link #updateCounterColumnName(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getUpdateCounterColumnName(final Type<?> type)
	{
		return updateCounterColumnName(type);
	}

	/**
	 * Returns the name of database column for the field.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Field#getName() name} of the field.
	 * @see #columnNameQuoted(Field)
	 */
	public static String columnName(final Field<?> field)
	{
		return field.getColumn().id;
	}

	/**
	 * @return {@link #columnName(Field)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String columnNameQuoted(final Field<?> field)
	{
		return quote(field, columnName(field)); // TODO use quotedID
	}

	/**
	 * @deprecated Use {@link #columnName(Field)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getColumnName(final Field<?> field)
	{
		return columnName(field);
	}

	/**
	 * Returns the name of type column in the database for the field.
	 * If not configured otherwise
	 * or trimmed to fit into name length restrictions,
	 * the name equals the {@link Field#getName() name} of the field
	 * plus the appendix "Type".
	 * Values suitable for this column can be retrieved by {@link #typeColumnValue(Type)}.
	 * @throws IllegalArgumentException
	 *         if there is no type column for this ItemField,
	 *         because <code>{@link ItemField#getValueType() getValueType()}.{@link Type#getTypesOfInstances() getTypesOfInstances()}</code>
	 *         contains one type only.
	 * @see #typeColumnNameQuoted(ItemField)
	 */
	public static String typeColumnName(final ItemField<?> field)
	{
		final Column column = field.getTypeColumn();
		if(column==null)
			throw new IllegalArgumentException("no type column for " + field);

		return column.id;
	}

	/**
	 * @return {@link #typeColumnName(ItemField)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String typeColumnNameQuoted(final ItemField<?> field)
	{
		return quote(field, typeColumnName(field)); // TODO use quotedID
	}

	/**
	 * @deprecated Use {@link #typeColumnName(ItemField)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getTypeColumnName(final ItemField<?> field)
	{
		return typeColumnName(field);
	}

	public static String foreignKeyConstraintName(final ItemField<?> field)
	{
		return ((ItemColumn)field.getColumn()).foreignKeyConstraintName;
	}

	/**
	 * @deprecated Use {@link #foreignKeyConstraintName(ItemField)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getForeignKeyConstraintName(final ItemField<?> field)
	{
		return foreignKeyConstraintName(field);
	}

	public static String superForeignKeyConstraintName(final Type<?> type)
	{
		final IntegerColumn column = type.getTable().primaryKey;
		if(!(column instanceof ItemColumn))
			throw new IllegalArgumentException("no super type for " + type);
		return ((ItemColumn)column).foreignKeyConstraintName;
	}

	/**
	 * @deprecated Use {@link #superForeignKeyConstraintName(Type)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getSuperForeignKeyConstraintName(final Type<?> type)
	{
		return superForeignKeyConstraintName(type);
	}

	/**
	 * Returns the value of database column for the field
	 * and the given enum value.
	 * Allocation of schema values
	 * is documented at {@link CopeSchemaValue}.
	 */
	@SuppressWarnings({"unchecked","rawtypes"}) // I have no idea why I have to do this stuff
	public static int columnValue(final Enum<?> value)
	{
		return columnValueInternal((Enum)value);
	}

	private static <E extends Enum<E>> int columnValueInternal(final E value)
	{
		return EnumFieldType.get(value.getDeclaringClass()).getNumber(value);
	}

	/**
	 * @deprecated Use {@link #columnValue(Enum)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static int getColumnValue(final Enum<?> value)
	{
		return columnValue(value);
	}

	/**
	 * Returns the name of the sequence for generating values for the
	 * {@link IntegerField#defaultToNext(int) defaultToNext}
	 * mechanism of the field.
	 * @throws IllegalArgumentException
	 *         if there is no such sequence for this field,
	 *         because values are generated otherwise.
	 * @deprecated Use {@link #sequenceName(Sequence)} and {@link IntegerField#getDefaultNextSequence()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated @SuppressWarnings("DeprecatedIsStillUsed") // OK: testing deprecated api
	public static String getDefaultToNextSequenceName(final IntegerField field)
	{
		return sequenceName(field.getDefaultNextSequence());
	}

	/**
	 * Returns the name of the sequence for generating values via
	 * {@link Sequence#next()}.
	 * @see #sequenceNameQuoted(Sequence)
	 */
	public static String sequenceName(final Sequence sequence)
	{
		return sequence.sequenceX.getSchemaName();
	}

	/**
	 * @return {@link #sequenceName(Sequence)}, but {@link #quoteName(Model, String) quoted}.
	 */
	public static String sequenceNameQuoted(final Sequence sequence)
	{
		return quote(sequence.getType(), sequenceName(sequence)); // TODO use quotedName
	}

	/**
	 * @deprecated Use {@link #sequenceName(Sequence)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getSequenceName(final Sequence sequence)
	{
		return sequenceName(sequence);
	}

	public static String constraintName(final UniqueConstraint constraint)
	{
		return constraint.getDatabaseID();
	}

	/**
	 * @deprecated Use {@link #constraintName(UniqueConstraint)} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	public static String getConstraintName(final UniqueConstraint constraint)
	{
		return constraintName(constraint);
	}

	/**
	 * @see Query#search()
	 */
	public static String search(final Query<?> query)
	{
		if(query.getCondition()==Condition.ofFalse())
			return "skipped because condition==false: " + query;
		if(query.getPageLimitOrMinusOne()==0)
			return "skipped because limit==0: " + query;

		return search(query, Query.Mode.SEARCH);
	}

	/**
	 * @see Query#total()
	 */
	public static String total(final Query<?> query)
	{
		if(query.getCondition()==Condition.ofFalse())
			return "skipped because condition==false: " + query;

		return search(query, Query.Mode.TOTAL);
	}

	/**
	 * @see Query#exists()
	 */
	public static String exists(final Query<?> query)
	{
		if(query.getCondition()==Condition.ofFalse())
			return "skipped because condition==false: " + query;

		return search(query, Query.Mode.EXISTS);
	}

	private static String search(final Query<?> query, final Query.Mode mode)
	{
		final StringBuilder sb = new StringBuilder();
		query.search(null, query.getType().getModel().connect().executor, mode, sb);
		return sb.toString();
	}

	/**
	 * @deprecated since update counter consistency between tables has been abandoned
	 * @throws RuntimeException always
	 * @see Type#checkUpdateCounterL()
	 */
	@Deprecated
	public static String checkUpdateCounter(final Type<?> type)
	{
		requireNonNull(type, "type");
		throw new RuntimeException("update counter consistency between tables has been abandoned");
	}

	/**
	 * @see ItemFunction#checkTypeColumnL()
	 */
	public static String checkTypeColumn(final ItemFunction<?> function)
	{
		return requireNonNull(function, "function").
				checkTypeColumnStatement(SQL_ONLY).getText();
	}

	/**
	 * @see Type#checkCompletenessL(Type)
	 */
	public static <T extends Item> String checkCompleteness(final Type<T> type, final Type<? extends T> subType)
	{
		return requireNonNull(type, "type").
				checkCompletenessStatement(subType, SQL_ONLY).getText();
	}

	/**
	 * @see CopyConstraint#check()
	 */
	public static String check(final CopyConstraint constraint)
	{
		return total(requireNonNull(constraint, "constraint").checkQuery());
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#tableName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailTableName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).tableName();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#hashColumnName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailHashColumnName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).hashColumnName();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#lengthColumnName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailLengthColumnName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).lengthColumnName();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#startColumnName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailStartColumnName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).startColumnName();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#startColumnLimit()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static int getVaultTrailStartColumnLimit(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).startColumnLimit();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#dateColumnName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailDateColumnName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).dateColumnName();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#fieldColumnName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailFieldColumnName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).fieldColumnName();
	}

	/**
	 * @deprecated Use {@link #vaultTrail(Model, String)} and {@link VaultTrailSchemaInfo#originColumnName()} instead.
	 * You may use an "Inline Method" - functionality of your IDE to update your code.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public static String getVaultTrailOriginColumnName(final Model model, final String bucket)
	{
		return vaultTrail(model, bucket).originColumnName();
	}

	public static VaultTrailSchemaInfo vaultTrail(final Model model, final String bucket)
	{
		checkBucket(bucket, message ->
				new IllegalArgumentException("bucket " + message));
		return model.connect().vault(bucket).trail;
	}

	/**
	 * @see DataField#checkVaultTrail()
	 */
	public static String checkVaultTrail(final DataField field)
	{
		return requireNonNull(field, "field").
				checkVaultTrailStatement(SQL_ONLY).getText();
	}


	private SchemaInfo()
	{
		// prevent instantiation
	}
}

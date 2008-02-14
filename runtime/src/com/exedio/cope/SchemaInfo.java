/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
	 * Returns the name of database table for the type.
	 */
	public static String getTableName(final Type type)
	{
		return type.table.idLower;
	}
	
	/**
	 * Returns the name of primary key column in the database for the type.
	 */
	public static String getPrimaryKeyColumnName(final Type type)
	{
		return type.table.primaryKey.id;
	}
	
	/**
	 * Returns the name of type column in the database for the type.
	 * @throws IllegalArgumentException
	 *         if there is no type column for this type,
	 *         because <code>{@link Type#getTypesOfInstances()}</code>
	 *         contains one type only.
	 */
	public static String getTypeColumnName(final Type type)
	{
		final Table table = type.table;
		if(table.typeColumn==null)
			throw new IllegalArgumentException("no type column for " + type);

		return table.typeColumn.id;
	}
	
	/**
	 * Returns the name of database column for the field.
	 */
	public static String getColumnName(final Field field)
	{
		return field.getColumn().id;
	}
	
	/**
	 * Returns the name of type column in the database for the field.
	 * @throws IllegalArgumentException
	 *         if there is no type column for this ItemField,
	 *         because <code>{@link ItemField#getValueType() getValueType()}.{@link Type#getTypesOfInstances() getTypesOfInstances()}</code>
	 *         contains one type only.
	 */
	public static String getTypeColumnName(final ItemField field)
	{
		final Column typeColumn = field.getTypeColumn();
		if(typeColumn==null)
			throw new IllegalArgumentException("no type column for " + field);

		return typeColumn.id;
	}
	
	/**
	 * Returns the value of database column for the field
	 * and the given enum value.
	 */
	public static <E extends Enum<E>> int getColumnValue(final EnumField<E> field, final E value)
	{
		return field.columnValue(value);
	}
}

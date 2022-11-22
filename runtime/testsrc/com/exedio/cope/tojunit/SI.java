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

package com.exedio.cope.tojunit;

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getUpdateCounterColumnName;
import static com.exedio.cope.SchemaInfo.quoteName;

import com.exedio.cope.Field;
import com.exedio.cope.ItemField;
import com.exedio.cope.Type;

/**
 * Shortcuts for {@link com.exedio.cope.SchemaInfo}
 */
public final class SI
{
	public static String tab(final Type<?> type)
	{
		return quoteName(type.getModel(), getTableName(type));
	}

	public static String view(final Type<?> type)
	{
		return quoteName(type.getModel(), getTableName(type) + "V");
	}

	public static String pk(final Type<?> type)
	{
		return quoteName(type.getModel(), getPrimaryKeyColumnName(type));
	}

	public static String pkq(final Type<?> type)
	{
		return tab(type) + "." + pk(type);
	}

	public static String type(final Type<?> type)
	{
		return quoteName(type.getModel(), getTypeColumnName(type));
	}

	public static String typeq(final Type<?> type)
	{
		return tab(type) + "." + type(type);
	}

	public static String update(final Type<?> type)
	{
		return quoteName(type.getModel(), getUpdateCounterColumnName(type));
	}

	public static String updateq(final Type<?> type)
	{
		return tab(type) + "." + update(type);
	}

	public static String col(final Field<?> field)
	{
		return quoteName(field.getType().getModel(), getColumnName(field));
	}

	public static String colq(final Field<?> field)
	{
		return tab(field.getType()) + "." + col(field);
	}

	public static String type(final ItemField<?> field)
	{
		return quoteName(field.getType().getModel(), getTypeColumnName(field));
	}


	private SI()
	{
		// prevent instantiation
	}
}

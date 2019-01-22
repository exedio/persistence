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

package com.exedio.cope.misc;

import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.SchemaInfo.quoteName;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SchemaView
{
	private static final Logger logger = LoggerFactory.getLogger(SchemaView.class);

	private final Model model;

	public SchemaView(final Model model)
	{
		this.model = requireNonNull(model, "model");
	}

	public void create() throws SQLException
	{
		try(Connection connection = SchemaInfo.newConnection(model))
		{
			final StringBuilder bf = new StringBuilder();

			for(final Type<?> type : model.getTypes())
			{
				bf.append("CREATE VIEW ").
					append(view(type)).
					append(" AS (SELECT ").
					append(table(type)).append('.').append(column(type));

				final LinkedList<Type<?>> superTypes = new LinkedList<>();
				for(Type<?> superType = type; superType!=null; superType = superType.getSupertype())
					superTypes.add(0, superType);

				for(final Type<?> superType : superTypes)
				{
					for(final Field<?> field : superType.getDeclaredFields())
					{
						bf.append(',');
						if(field instanceof EnumField)
						{
							bf.append("CASE ").
								append(table(superType)).append('.').append(column(field));

							//noinspection OverlyStrongTypeCast bug in inspection
							for(final Enum<?> v : ((EnumField<?>)field).getValueClass().getEnumConstants())
							{
								final int columnValue = getColumnValue(v);
								bf.append(" WHEN ").
									append(columnValue).
									append(" THEN '").
									append(v.name()).
									append('\'');
							}
							bf.append(" END AS ").append(column(field));
						}
						else if(field instanceof DateField && !SchemaInfo.supportsNativeDate(model))
						{
							bf.append("FROM_UNIXTIME(").
								append(table(superType)).append('.').append(column(field)).
								append("/1000) AS ").append(column(field));
						}
						else
						{
							bf.append(table(superType)).append('.').append(column(field));
						}
					}
				}

				bf.append(" FROM ").
					append(table(type));

				for(Type<?> superType = type.getSupertype(); superType!=null; superType=superType.getSupertype())
				{
					bf.append(" JOIN ").
						append(table(superType)).
						append(" ON ").
						append(table(superType)).append('.').append(column(superType)).
						append('=').
						append(table(type)).append('.').append(column(type));
				}

				bf.append(')');

				execute(connection, bf);
			}
		}
	}

	public void tearDown() throws SQLException
	{
		try(Connection connection = SchemaInfo.newConnection(model))
		{
			final StringBuilder bf = new StringBuilder();

			for(final Type<?> type : model.getTypes())
			{
				bf.append("DROP VIEW IF EXISTS ").
					append(view(type));
				execute(connection, bf);
			}
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private static void execute(final Connection connection, final StringBuilder bf) throws SQLException
	{
		final String sql = bf.toString();
		logger.info(sql);

		try(Statement statement = connection.createStatement())
		{
			statement.execute(sql);
		}
		bf.setLength(0);
	}

	private String table(final Type<?> type)
	{
		return quoteName(model, SchemaInfo.getTableName(type));
	}

	private String view(final Type<?> type)
	{
		return quoteName(model, SchemaInfo.getTableName(type)+'V');
	}

	private String column(final Type<?> type)
	{
		return quoteName(model, SchemaInfo.getPrimaryKeyColumnName(type));
	}

	private String column(final Field<?> field)
	{
		return quoteName(model, SchemaInfo.getColumnName(field));
	}
}

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

package com.exedio.dsmf;

import static com.exedio.dsmf.Dialect.notifyExistentUnique;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;

final class UniqueConstraintCollector
{
	private final Schema schema;

	UniqueConstraintCollector(final Schema schema)
	{
		this.schema = requireNonNull(schema);
	}

	private Table table = null;
	private String name = null;
	private final ArrayList<String> columns = new ArrayList<>();

	void onColumn(
			final Table table,
			final String name,
			final String column)
	{
		requireNonNull(table);
		requireNonNull(name);
		requireNonNull(column);

		if(this.table==null)
		{
			this.table = table;
			this.name = name;
			this.columns.add(column);
		}
		else if(this.table==table && this.name.equals(name))
		{
			this.columns.add(column);
		}
		else
		{
			flush();
			this.table = table;
			this.name = name;
			this.columns.add(column);
		}
	}

	void finish()
	{
		if(table!=null)
			flush();
	}

	private void flush()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('(');
		boolean first = true;
		for(final String column: columns)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(schema.quoteName(column));
		}
		bf.append(')');
		notifyExistentUnique(table, name, bf.toString());

		this.table = null;
		this.name = null;
		this.columns.clear();
	}
}

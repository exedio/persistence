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

import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static com.exedio.cope.util.Check.requireNonNegative;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DatabaseLogListener implements DatabaseListener
{
	public static final class Builder
	{
		private final PrintStream out;
		private int durationThreshold = 0;
		private String sqlFilter = null;

		public Builder(final PrintStream out)
		{
			this.out = requireNonNull(out, "out");
		}

		public Builder durationThreshold(final int durationThreshold)
		{
			this.durationThreshold = requireGreaterZero(durationThreshold, "durationThreshold");
			return this;
		}

		public Builder sqlFilter(final String sqlFilter)
		{
			this.sqlFilter = requireNonEmpty(sqlFilter, "sqlFilter");
			return this;
		}

		public DatabaseLogListener build()
		{
			return new DatabaseLogListener(durationThreshold, sqlFilter, out);
		}
	}

	private final long date;
	private final int threshold;
	private final String sql;
	private final PrintStream out;

	public DatabaseLogListener(final int threshold, final String sql, final PrintStream out)
	{
		this.date = System.currentTimeMillis();
		this.threshold = requireNonNegative(threshold, "threshold");
		this.sql = sql;
		this.out = requireNonNull(out, "out");
	}

	public Date getDate()
	{
		return new Date(date);
	}

	public int getThreshold()
	{
		return threshold;
	}

	public String getSQL()
	{
		return sql;
	}

	@Override
	public void onStatement(
			final String statement,
			final List<Object> parameters,
			final long durationPrepare,
			final long durationExecute,
			final long durationRead,
			final long durationClose)
	{
		if(( (threshold==0) || ((durationPrepare+durationExecute+durationRead+durationClose)>=threshold) ) &&
			( (sql==null)    || (statement.contains(sql)) ))
		{
			final StringBuilder bf = new StringBuilder(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", Locale.ENGLISH).format(new Date()));

			bf.append('|');
			bf.append(durationPrepare);
			bf.append('|');
			bf.append(durationExecute);
			bf.append('|');
			bf.append(durationRead);
			bf.append('|');
			bf.append(durationClose);
			bf.append('|');
			bf.append(statement);

			if(parameters!=null)
			{
				bf.append('|');
				bf.append(parameters);
			}

			out.println(bf);
		}
	}
}

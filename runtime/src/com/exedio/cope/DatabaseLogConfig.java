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

import java.io.PrintStream;

final class DatabaseLogConfig
{
	final int threshold;
	final String sql;
	private final PrintStream out;
	
	DatabaseLogConfig(final int threshold, final String sql, final PrintStream out)
	{
		if(threshold<0)
			throw new IllegalArgumentException("threshold must not be negative, but was " + threshold);
		if(out==null)
			throw new NullPointerException("out must not be null");
		
		this.threshold = threshold;
		this.sql = sql;
		this.out = out;
	}

	void log(final Statement statement, final long... times)
	{
		if((times[times.length-1]-times[0])>=threshold && (sql==null || statement.getText().indexOf(sql)>=0))
			statement.log(out, times);
	}
}
